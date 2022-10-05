package ru.practicum.explorewithme.ewm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explorewithme.ewm.aop.AopAnnotationBeanPostProcessor;

@Configuration
public class AopAnnotationBeanPostProcessorConfig {

    @Bean
    public AopAnnotationBeanPostProcessor aopAnnotationBeanPostProcessor() {
        return new AopAnnotationBeanPostProcessor();
    }
}
