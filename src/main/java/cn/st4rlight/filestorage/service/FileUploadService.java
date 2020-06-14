package cn.st4rlight.filestorage.service;

import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.query.ChangeInfoReq;
import cn.st4rlight.filestorage.util.RestResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileUploadService {

    UploadResp uploadFile(MultipartFile file) throws Exception;
    RestResponse<? extends Object> getFile(HttpServletResponse response, int code, String password) throws IOException;
    RestResponse<? extends Object> changeInfo(ChangeInfoReq req);
}
