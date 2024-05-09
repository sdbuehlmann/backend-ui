package ch.donkeycode.backendui.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ChildElementValuesDto {
    @NonNull UUID triggeringActionId;
    @NonNull UUID parentElementId;
    @NonNull List<ElementValueDto> elementValues;
}
