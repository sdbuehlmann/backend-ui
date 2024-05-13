package ch.donkeycode.backendui.frontend.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ResponseDto {
    @NonNull
    UUID id;

    @Nullable
    JsonNode data;
}
