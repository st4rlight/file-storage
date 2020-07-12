package cn.st4rlight.filestorage.controller;

import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.error.ErrorCodes;
import cn.st4rlight.filestorage.query.ChangeInfoReq;
import cn.st4rlight.filestorage.service.FileUploadService;
import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private ErrorCodes errorCodes;

    // 单文件上传
    @PostMapping("/upload")
    public RestResponse<? extends Object> uploadFile(@RequestParam("file")MultipartFile file){
        if(ObjectUtils.isEmpty(file))
            return errorCodes.emptyFile();

        try {
            log.info("单文件上传请求，fileName: {}, contentType: {}, size: {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
            UploadResp uploadResp = fileUploadService.uploadFile(file);
            return RestResponse.of(uploadResp);
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.error(ex);
        }
    }


    // 多文件上传
    @PostMapping("/upload/multi")
    public RestResponse<? extends Object> uploadMultiFile(@RequestParam("files") List<MultipartFile> fileList){
        if(CollectionUtils.isEmpty(fileList))
            return errorCodes.emptyFile();

        try {
            long size = fileList.stream().map(MultipartFile::getSize).reduce(0L, Long::sum);
            log.info("多文件上传请求，文件数: {}, size: {}", fileList.size(), size);

            UploadResp uploadResp = fileUploadService.uploadMultiFile(fileList);
            return RestResponse.of(uploadResp);
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.error(ex);
        }

    }

    @GetMapping(value = {"/retrieve/{code}", "/retrieve/{code}/{password}"})
    public RestResponse<Void> getFile(
        HttpServletResponse response,
        @PathVariable("code") String code,
        @PathVariable(name = "password", required = false) String password
    ){
        log.info("提取文件请求，code: {}, password: {}", code, password);

        try {
            return (RestResponse<Void>) fileUploadService.getFile(response, code, password);

        } catch (IOException ex) {
            ex.printStackTrace();
            return RestResponse.error(ex);
        }
    }

    @PostMapping("/changeInfo")
    public RestResponse<UploadResp> changeInfo(@RequestBody ChangeInfoReq req){
        try{
            log.info("修改信息请求，rep: {}", req);
            return (RestResponse<UploadResp>) fileUploadService.changeInfo(req);
        }catch (Exception ex){
            ex.printStackTrace();
            return RestResponse.error(ex);
        }
    }


    @GetMapping(value = {"/check/{code}", "/check/{code}/{password}"})
    public RestResponse<Void> checkDownload(@PathVariable("code")String code, @PathVariable(value = "password", required = false) String password){
        try{
            log.info("检查文件提取, code: {}, password", code, password);
            return (RestResponse<Void>)fileUploadService.checkDownload(code, password);

        }catch (Exception ex){
            ex.printStackTrace();
            return RestResponse.error(ex);
        }
    }
}
