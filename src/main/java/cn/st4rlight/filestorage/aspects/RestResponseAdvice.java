package cn.st4rlight.filestorage.aspects;

import cn.st4rlight.filestorage.util.RequestUtil;
import cn.st4rlight.filestorage.util.RestResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<RestResponse<? extends Object>> {

    @Override
    public RestResponse<? extends Object> beforeBodyWrite(RestResponse<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        body.setTimeCost(System.currentTimeMillis() - RequestUtil.getStartTime());
        body.setRequestId(RequestUtil.getRequestId().toString());

        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
