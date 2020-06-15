package cn.st4rlight.filestorage.filters;

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

        chain.doFilter(request, response);

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
