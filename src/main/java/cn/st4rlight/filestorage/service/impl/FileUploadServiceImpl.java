package cn.st4rlight.filestorage.service.impl;

import cn.st4rlight.filestorage.domain.FileUpload;
import cn.st4rlight.filestorage.domain.MyFile;
import cn.st4rlight.filestorage.domain.Status;
import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.repository.FileRepository;
import cn.st4rlight.filestorage.repository.FileUploadRepository;
import cn.st4rlight.filestorage.service.FileUploadService;
import cn.st4rlight.filestorage.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
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

    private Random random = new Random();

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final int DEFALUT_EXPIRE_DAYS = 3;
    private static final String ADDRESS = "http://121.36.6.81:8972/";
    private static final String BASE_FILE_PATH = "E:\\files\\";

    @Override
    public UploadResp uploadFile(MultipartFile file) throws Exception {
        long fileId = getFileId(file);

        // 先产生一个提取码
        int code = Math.abs(random.nextInt());
        while(fileUploadRepository.existsByExtractCodeAndStatusIsNot(code, Status.DELETED))
            code = Math.abs(random.nextInt());


        // 构建新的上传信息
        FileUpload fileUpload = FileUpload.builder()
                .fileIds(String.valueOf(fileId).getBytes())
                .uploadTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(3))
                .extractCode(code)
                .status(Status.NORMAL)
                .build();

        long uploadId = fileUploadRepository.saveAndFlush(fileUpload).getUploadId();
        UploadResp uploadResp = UploadResp.builder()
                .extractCode(code)
                .qrCode("http://qr.liantu.com/api.php?text=" +  ADDRESS + code)
                .time(DEFALUT_EXPIRE_DAYS)
                .timeUnit(UploadResp.TimeUnit.DAYS)
                .build();

        log.info("文件上传成功, upload_id: {}", uploadId);
        return uploadResp;
    }

    @Override
    public String getFile(HttpServletResponse response, int code) throws RuntimeException {
        Optional<FileUpload> optional = fileUploadRepository.findByExtractCodeAndStatusNot(code, Status.DELETED);
        if(optional.isEmpty()){
            log.info("提取码无效, code: {}", code);
            return "提取码无效";
        }

        FileUpload fileUpload = optional.get();
        if(fileUpload.getExpireTime().isBefore(LocalDateTime.now())){
            log.info("下载请求过期, code: {}", code);
            return "提取码已过期";
        }

        String ids = new String(fileUpload.getFileIds());
        String fileId = ids.split(",")[0];

        Optional<MyFile> byId = fileRepository.findById(Long.parseLong(fileId));
        MyFile myFile = byId.orElse(null);

        try {
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader(
            "Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(myFile.getFileName(), StandardCharsets.UTF_8) +
                ";filename*=utf-8" + URLEncoder.encode(myFile.getFileName(), StandardCharsets.UTF_8)
            );
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", myFile.getExpireTime().format(DATE_TIME_FORMATTER));
            response.addHeader("Last-Modified", myFile.getCreateTime().format(DATE_TIME_FORMATTER));
            response.addHeader("ETag", String.valueOf(System.currentTimeMillis()));
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("content-type", myFile.getContentType() + ";charset=UTF-8");

            File file = new File(myFile.getFilePath());
            inToOut(new FileInputStream(file), response.getOutputStream());
        } catch (IOException ex) {
            log.error("文件读写或者header编码出错", ex);
            throw new RuntimeException(ex);
        }


        return null;
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
}
