package cn.st4rlight.filestorage.aspects;

import cn.st4rlight.filestorage.util.RequestUtil;
import cn.st4rlight.filestorage.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalErrorAdvice {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public RestResponse<Void> methodNotSupport(HttpRequestMethodNotSupportedException ex){
        log.error("请求方法错误, method: {}, request_id: {}", ex.getMethod(), RequestUtil.getRequestId());

        return RestResponse.errorMsg(400, ex.getMessage());
    }


    // 全局异常捕获处理
    @ExceptionHandler(Throwable.class)
    @ResponseBody // 注意这里要加上这个注解
    public RestResponse<Void> GlobalExceptionHandler(Throwable ex){
        log.error("产生未捕获的异常, request_id: {}", RequestUtil.getRequestId());
        ex.printStackTrace();
        return RestResponse.errorMsg(500, "服务器产生未捕获的异常\r\n" + ex.getMessage());
    }
}