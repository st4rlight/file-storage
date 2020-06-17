package cn.st4rlight.filestorage.aspects;

import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalErrorAdvice {

    // 全局异常捕获处理
    @ExceptionHandler(Throwable.class)
    public RestResponse<Void> GlobalExceptionHandler(Throwable ex){
        log.error("产生未捕获的异常", ex);

        return RestResponse.errorMsg(500, "服务器产生未捕获的异常\r\n" + ex.getMessage());
    }
}
