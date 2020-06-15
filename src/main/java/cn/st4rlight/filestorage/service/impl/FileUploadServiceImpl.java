package cn.st4rlight.filestorage.service.impl;

import cn.st4rlight.filestorage.domain.FileUpload;
import cn.st4rlight.filestorage.domain.MyFile;
import cn.st4rlight.filestorage.domain.Status;
import cn.st4rlight.filestorage.domain.TimeUnit;
import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.error.ErrorCodes;
import cn.st4rlight.filestorage.query.ChangeInfoReq;
import cn.st4rlight.filestorage.repository.FileRepository;
import cn.st4rlight.filestorage.repository.FileUploadRepository;
import cn.st4rlight.filestorage.service.FileUploadService;
import cn.st4rlight.filestorage.util.MD5;
import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private FileUploadRepository fileUploadRepository;

    @Resource
    private FileRepository fileRepository;

    private static final Random random = new Random();
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final int DEFALUT_EXPIRE_DAYS = 3;
    private static final String ADDRESS = "http://121.36.6.81:8972/";

    private static final int MIN_CODE = 10_000_000;
    private static final int MAX_CODE = 99_999_999;

    @Value("${file-storage.path}")
    private String BASE_FILE_PATH;


    @Override
    public UploadResp uploadFile(MultipartFile file) throws Exception {
        long fileId = getFileId(file);


        // 先产生一个提取码
        long code = getRandomCode();
        while(fileUploadRepository.existsByExtractCodeAndStatusIsNot(code, Status.DELETED))
            code = getRandomCode();


        // 构建新的上传信息
        FileUpload fileUpload = FileUpload.builder()
                .fileIds(String.valueOf(fileId).getBytes())
                .uploadTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(3))
                .extractCode(code)
                .fileName(file.getOriginalFilename())
                .status(Status.NORMAL)
                .build();

        long uploadId = fileUploadRepository.saveAndFlush(fileUpload).getUploadId();
        UploadResp uploadResp = UploadResp.builder()
                .uploadId(uploadId)
                .extractCode(code)
                .qrCode(getQrCode(String.valueOf(code)))
                .time(DEFALUT_EXPIRE_DAYS)
                .timeUnit(TimeUnit.DAYS)
                .build();

        log.info("文件上传成功, upload_id: {}", uploadId);
        return uploadResp;
    }

    @Override
    public RestResponse<? extends Object> getFile(HttpServletResponse response, int code, String password) throws RuntimeException {
        Optional<FileUpload> optional = fileUploadRepository.findByExtractCodeAndStatusNot(code, Status.DELETED);

        // 校验提取码
        if(optional.isEmpty())
            return ErrorCodes.invalidCode(code);

        // 校验过期时间
        FileUpload fileUpload = optional.get();
        if(fileUpload.getExpireTime().isBefore(LocalDateTime.now()))
            ErrorCodes.expireCode(code);

        // 校验密码
        String realPassword = fileUpload.getPassword();
        if(Strings.isNotBlank(realPassword)){
            // 密码为空
            if(Strings.isBlank(password))
                return ErrorCodes.needAuth(code);

            // 密码错误
            if(!realPassword.equals(MD5.encryptPassword(password)))
                return ErrorCodes.wrongPassword(password);
        }

        // survive
        String ids = new String(fileUpload.getFileIds());
        String fileId = ids.split(",")[0];

        Optional<MyFile> byId = fileRepository.findById(Long.parseLong(fileId));
        MyFile myFile = byId.orElse(null);

        try {
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader(
            "Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(myFile.getFileName(), StandardCharsets.UTF_8) // 解决文件名显示中文的问题
            );
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", myFile.getExpireTime().format(DATE_TIME_FORMATTER));
            response.addHeader("Last-Modified", myFile.getCreateTime().format(DATE_TIME_FORMATTER));
            response.addHeader("ETag", String.valueOf(System.currentTimeMillis()));
            response.setContentType("application/force-download");  // 设置强制下载不打开
            response.addHeader("content-type", myFile.getContentType());
            response.addHeader("content-length", String.valueOf(myFile.getFileSize())); // 显示文件大小

            File file = new File(myFile.getFilePath());
            inToOut(new FileInputStream(file), response.getOutputStream());
        } catch (IOException ex) {
            log.error("文件读写或者header编码出错", ex);
            throw new RuntimeException(ex);
        }


        return null;
    }

    @Override
    public RestResponse<? extends Object> changeInfo(ChangeInfoReq req) {
        Optional<FileUpload> optional = fileUploadRepository.findById(req.getUploadId());

        // 检查上传的信息
        if(optional.isEmpty())
            return ErrorCodes.noUploadInfo(req.getUploadId());

        // 检查提取码
        FileUpload fileUpload = optional.orElse(null);
        if(fileUpload.getExtractCode() != req.getOldCode())
            return ErrorCodes.wrongCode(fileUpload.getExtractCode(), req.getOldCode());

        // 检查提取码是否过期
        if(fileUpload.getExpireTime().isBefore(LocalDateTime.now()))
            return ErrorCodes.expireCode(req.getOldCode());

        // 检查新旧提取码是否一致
        if(req.getOldCode() == req.getNewCode())
            return ErrorCodes.sameCode(req.getOldCode());

        // 检查提取码是否已经存在
        boolean flag = fileUploadRepository.existsByExtractCodeAndStatusIsNot(req.getNewCode(), Status.DELETED);
        if(flag)
            return ErrorCodes.duplicateCode(req.getNewCode());


        // 检查最大保存时间是否超过30天
        LocalDateTime time = fileUpload.getUploadTime();
        if(req.getTimeUnit() == TimeUnit.DAYS)
            time = time.plusDays(req.getTime());
        else
            time = time.plusHours(req.getTime());
        if(time.isAfter(fileUpload.getUploadTime().plusDays(30)))
            return ErrorCodes.timeExceed30Days(req.getTime(), req.getTimeUnit());


        // survive
        fileUpload.setExpireTime(time);
        fileUpload.setExtractCode(req.getNewCode());
        if(Strings.isNotBlank(req.getPassword()))
            fileUpload.setPassword(MD5.encryptPassword(req.getPassword().trim()));
        fileUploadRepository.saveAndFlush(fileUpload);


        UploadResp resp = UploadResp.builder()
                .extractCode(fileUpload.getExtractCode())
                .qrCode(getQrCode(String.valueOf(fileUpload.getExtractCode())))
                .time(req.getTime())
                .timeUnit(req.getTimeUnit())
                .uploadId(fileUpload.getUploadId())
                .build();

        return RestResponse.of(resp);
    }



    // 获取文件id
    public long getFileId(MultipartFile file) throws RuntimeException{
        long fileId = 0;

        try {
            InputStream inputStream = file.getInputStream();
            // 计算文件md5
            String md5 = MD5.getMD5(inputStream);
            // 判断数据中是否已经有该文件
            Optional<MyFile> optional = fileRepository.findByMd5(md5.getBytes());

            fileId = -1L;
            if(optional.isPresent()){
                // 若存在文件，则判断下是否要修改下默认的过期时间
                MyFile myFile = optional.get();
                if (myFile.getExpireTime().isBefore(LocalDateTime.now().plusDays(3)))
                    myFile.setExpireTime(LocalDateTime.now().plusDays(3));

                fileId = myFile.getFileId();
                log.info("当前上传的文件已存在, id: {}", fileId);


            }else{
                // 判断当前日期的文件夹是存在
                Path path = Path.of(BASE_FILE_PATH, LocalDate.now().format(DATE_FORMATTER));
                if (Files.notExists(path))
                    Files.createDirectory(path);

                // 建立当前要保存文件的路径，为了防止重名，名字前统一加上uuid
                Path filePath = Path.of(path.toString(), UUID.randomUUID().toString() + "__" +  file.getOriginalFilename());
                File fileObj = filePath.toFile();

                // 写入文件
                inToOut(file.getInputStream(), new FileOutputStream(fileObj));
                MyFile newFile = MyFile.builder()
                        .filePath(filePath.toString())
                        .fileName(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .md5(md5.getBytes())
                        .createTime(LocalDateTime.now())
                        .expireTime(LocalDateTime.now().plusDays(3))
                        .status(Status.NORMAL)
                        .build();

                fileId = fileRepository.saveAndFlush(newFile).getFileId();
                log.info("新文件, id: {}, path: {}", fileId, filePath.toString());
            }

            return fileId;
        } catch (IOException ex) {
            log.error("获取文件输入流出错", ex);
            throw new RuntimeException(ex);
        }
    }

    public void inToOut(InputStream is, OutputStream os) throws IOException{
        BufferedInputStream bif = new BufferedInputStream(is);
        BufferedOutputStream bof = new BufferedOutputStream(os);

        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = bif.read(buffer)) != -1)
            bof.write(buffer, 0, len);

        bof.close();
        bif.close();
        is.close();
        os.close();
    }

    public long getRandomCode(){
        return Math.abs(random.nextInt()) % (MAX_CODE - MIN_CODE + 1) + MIN_CODE;
    }

    public String getQrCode(String str){
        return "http://qr.liantu.com/api.php?text=" + ADDRESS + str;
    }
}
