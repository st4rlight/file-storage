package cn.st4rlight.filestorage.aspects;

import cn.st4rlight.filestorage.util.RequestIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestIdAspect {

    @Pointcut("execution(public * cn.st4rlight.filestorage.error.ErrorCodes.*(..))")
    public void pt(){};


    @Around(value = "pt()")
    public Object doAddRequestIdOutput(ProceedingJoinPoint pjp){
        try {
            System.out.println("+-------------------------------------------------------+");
            System.out.println("request_id: " + RequestIdUtil.getRequestId().toString());

            Object[] args = pjp.getArgs();
            Object returnVal = pjp.proceed(args);

            System.out.println("+-------------------------------------------------------+");
            return returnVal;

        } catch (Throwable throwable) {
            log.error("动态代理出错", throwable);
            throw new RuntimeException(throwable);
        }
    }
}
