package ch.donkeycode.backendui.frontend.dto.ui2be;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class ElementValuesDto {
    @NonNull
    Set<ElementValueDto> values;
}
