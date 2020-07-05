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

    // 给response加上统一的时间消耗和请求id
    @Override
    public RestResponse<? extends Object> beforeBodyWrite(RestResponse<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        assert body != null;
        body.setTimeCost(System.currentTimeMillis() - RequestUtil.getStartTime());
        body.setRequestId(RequestUtil.getRequestId().toString());

        return body;
    }

    // 判断请求的结果是否需要进行统一的处理
    // 如果是api请求则需要
    // 如果是其他请求如swagger-ui请求就不需要
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType() == RestResponse.class;
    }
}
