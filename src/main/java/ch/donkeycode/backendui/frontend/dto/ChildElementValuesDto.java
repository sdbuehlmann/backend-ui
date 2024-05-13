package ch.donkeycode.backendui.frontend.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ChildElementValuesDto {
    @NonNull
    UUID triggeringActionId;
    @Nullable
    UUID parentElementId;
    @NonNull
    List<ElementValueDto> elementValues;
}
