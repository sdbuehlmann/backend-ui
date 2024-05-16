package ch.donkeycode.backendui.frontend.dto.be2ui;

import ch.donkeycode.backendui.common.StringUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class SetInnerHtmlDto {
    @NonNull
    UUID containerId;

    @NonNull
    String html;

    @Override
    public String toString() {
        return "SetInnerHtmlDto{" +
               "containerId=" + containerId +
               ", html='" + StringUtils.getCropped(html, 10) + '\'' +
               '}';
    }
}
