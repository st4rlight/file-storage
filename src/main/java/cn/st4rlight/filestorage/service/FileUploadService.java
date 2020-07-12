package cn.st4rlight.filestorage.service;

import cn.st4rlight.filestorage.dto.UploadResp;
import cn.st4rlight.filestorage.query.ChangeInfoReq;
import cn.st4rlight.filestorage.util.RestResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileUploadService {

    UploadResp uploadFile(MultipartFile file) throws Exception;
    UploadResp uploadMultiFile(List<MultipartFile> files) throws Exception;
    RestResponse<? extends Object> getFile(HttpServletResponse response, String code, String password) throws IOException;
    RestResponse<? extends Object> changeInfo(ChangeInfoReq req);
    RestResponse<? extends Object> checkDownload(String code, String password);
}
