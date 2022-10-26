package ru.practicum.explorewithme.ewm.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class PropertiesService {

    @Value("${app.format.date-time}")
    private String dateTimeFormat;

    @Value("${app.base-url.event}")
    private String baseEventUrl;
}
