package cn.st4rlight.filestorage.controller;

import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.error.ErrorCodes;
import cn.st4rlight.filestorage.query.ChangeInfoReq;
import cn.st4rlight.filestorage.service.FileUploadService;
import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;


    @PostMapping("/upload")
    public RestResponse<UploadResp> uploadFile(@RequestParam("file")MultipartFile file){
        try {
            log.info("上传请求，fileName: {}, contentType: {}, size: {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
            UploadResp uploadResp = fileUploadService.uploadFile(file);
            return RestResponse.of(uploadResp);
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.error(ex);
        }
    }

    @GetMapping(value = {"/{code}", "/{code}/{password}"})
    public RestResponse<Void> getFile(
        HttpServletResponse response, @PathVariable("code") int code,
        @PathVariable(name = "password", required = false) String password
    ){
        log.info("提取文件请求，code: {}, password", code, password);
        if(code < 0)
            return ErrorCodes.invalidCode(code);

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
}
