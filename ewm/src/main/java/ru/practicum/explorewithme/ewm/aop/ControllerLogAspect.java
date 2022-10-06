package ru.practicum.explorewithme.ewm.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.ewm.aop.annotation.ControllerLog;
import ru.practicum.explorewithme.ewm.stats.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerLogAspect {

    private final StatsClient client;

    @Around("@annotation(controllerLog)")
    public Object controllerLogAdvice(ProceedingJoinPoint pjp, ControllerLog controllerLog) throws Throwable {
        Object[] args = pjp.getArgs();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        HttpServletRequest request = (HttpServletRequest) Arrays.stream(args)
                .filter(arg -> arg instanceof HttpServletRequest)
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(request)) {
            log.info("Request {} with arguments {}", request.getRequestURI(), Arrays.toString(args));
            if (controllerLog.sendStats()) {
                try {
                    client.hit(request);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
        } else {
            log.error("@ControllerLog required HttpServletRequest parameter in method {} of class {}",
                    methodSignature.getMethod().getName(),
                    methodSignature.getMethod().getDeclaringClass().getName()
            );
        }
        return pjp.proceed(args);
    }
}
