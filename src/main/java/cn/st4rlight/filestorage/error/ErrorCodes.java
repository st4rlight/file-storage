package cn.st4rlight.filestorage.error;

import cn.st4rlight.filestorage.domain.TimeUnit;
import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorCodes {
    
    public RestResponse<Void> noUploadInfo(long uploadId){
        log.warn("上传信息不存在, upload_id: {}", uploadId);
        return RestResponse.errorMsg(4001, "上传信息不存在, upload_id: " + uploadId);
    }

    public RestResponse<Void> wrongCode(long rightCode, long errorCode){
        log.warn("提取码不正确, req_code: {}", rightCode, errorCode);
        return RestResponse.errorMsg(4002, "提取码不正确, code: " + errorCode);
    }

    public RestResponse<Void> sameCode(long code){
        log.warn("新提取码与旧提取码一致, code: {}", code);
        return RestResponse.errorMsg(4003, "新提取码与旧提取码一致, code: " + code);
    }

    public RestResponse<Void> duplicateCode(long code){
        log.warn("提取码已存在, code: {}", code);
        return RestResponse.errorMsg(4004, "提取码已存在, code: " + code);
    }

    public RestResponse<Void> timeExceed30Days(long time, TimeUnit timeUnit){
        log.warn("有效时间超过30天，time: {}, timeUnit: {}", time, timeUnit);
        return RestResponse.errorMsg(4005, "有效不能时间超过30天, time: " + time + ", time_unit: " + timeUnit);
    }

    public RestResponse<Void> invalidCode(long code){
        log.warn("提取码无效, req_code: {}", code);
        return RestResponse.errorMsg(4006, "提取码无效, code: " + code);
    }

    public RestResponse<Void> expireCode(long code){
        log.warn("提取码已过期, req_code: {}", code);
        return RestResponse.errorMsg(4007, "提取码已过期, code: " + code);
    }

    public RestResponse<Void> needAuth(long code){
        log.warn("需要密码, req_code: {}", code);
        return RestResponse.errorMsg(4008, "需要密码, code: " + code);
    }

    public RestResponse<Void> wrongPassword(String password){
        log.warn("密码错误, password: {}", password);
        return RestResponse.errorMsg(4009, "密码错误, password: " + password);
    }

    public RestResponse<Void> emptyFile(){
        log.warn("上传文件数为0");
        return RestResponse.errorMsg(4010, "上传文件数为0");
    }
}
