package cn.st4rlight.filestorage.service;

import cn.st4rlight.filestorage.dto.UploadResp;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileUploadService {

    UploadResp uploadFile(MultipartFile file) throws Exception;
    String getFile(HttpServletResponse response, int code) throws IOException;
}
