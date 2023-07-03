package com.pocketbudget.common.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MemberSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Configuration
public class SLOAspect {

    @Around(value = "@annotation(TrackLatency)")
    public Object trackLatency(ProceedingJoinPoint pjp, TrackLatency TrackLatency) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = pjp.proceed();
        stopWatch.stop();

        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        MemberSignature signature = (MemberSignature) pjp.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        if (lastTaskTimeMillis > TrackLatency.latency()) {
            log.error("Slow API! Method {} from class {} responded for {} ms, slower than expected! Please check and fix it!", methodName, className, lastTaskTimeMillis);
        }

        return proceed;
    }
}
