package cn.st4rlight.filestorage.controller;

import cn.st4rlight.filestorage.dto.UploadResp;
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

    @GetMapping("/{code}")
    public String getFile(HttpServletResponse response, @PathVariable("code") int code){
        if(code < 0){
            log.warn("错误的下载访问请求, extra_code: {}", code);
            return "请求的提取码有误";
        }

        String result = null;
        try {
            result = fileUploadService.getFile(response, code);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }
}
