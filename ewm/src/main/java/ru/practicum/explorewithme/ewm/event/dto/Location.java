package ru.practicum.explorewithme.ewm.event.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Location {
    Double lat;
    Double lon;
}
