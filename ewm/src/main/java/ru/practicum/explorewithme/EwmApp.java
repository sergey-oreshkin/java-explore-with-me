package ru.practicum.explorewithme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EwmApp {
    public static void main(String[] args) {
        SpringApplication.run(EwmApp.class, args);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        JavaTimeModule module = new JavaTimeModule();
//        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
//                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        );
//        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(
//                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        );
//        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
//        module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
//        ObjectMapper objectMapperObj = Jackson2ObjectMapperBuilder.json()
//                .modules(module)
//                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                .build();
//        return objectMapperObj;
//    }
}
