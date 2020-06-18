package cn.st4rlight.filestorage.filters;

import cn.st4rlight.filestorage.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "TimeCostFilter")
public class TimeCostFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();

        // 这里设置一次起始时间，防止出错的时候，不会进行interceptor，从而导致原本应当在interceptor中设置的起始时间为空
        // 而advice中又用到了这个startTime，因此这里提前设置，可以避免空指针异常
        RequestUtil.setStartTime();
        // 提前设置本次请求的uuid
        RequestUtil.getRequestId();

        chain.doFilter(request, response);

        // 统一在filter中销毁，避免由于线程池的原因复用
        RequestUtil.removeRequestId();
        RequestUtil.removeStartTime();


        long end = System.currentTimeMillis();
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        log.info("[   Filter  ] Method: {}, Url: {}, TimeCost: {}ms", servletRequest.getMethod(), servletRequest .getRequestURL(), end - start);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("请求耗时计算过滤器 加载成功");
    }

    @Override
    public void destroy() {
        log.info("请求耗时计算过滤器 已销毁");
    }
}
