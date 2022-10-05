package ru.practicum.explorewithme.ewm.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ru.practicum.explorewithme.ewm.aop.annotation.ControllerLog;
import ru.practicum.explorewithme.ewm.exception.MethodArgumentMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AopAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        Arrays.stream(methods).filter(m -> Objects.nonNull(m.getAnnotation(ControllerLog.class)))
                .forEach(m -> {
                    if (!List.of(m.getParameterTypes()).contains(HttpServletRequest.class)) {
                        throw new MethodArgumentMismatchException(
                                String.format(
                                        "Method annotated @ControllerLog required HttpServletRequest parameter. In class %s",
                                        m.getDeclaringClass().getName()
                                )
                        );
                    }
                });
        return bean;
    }
}
