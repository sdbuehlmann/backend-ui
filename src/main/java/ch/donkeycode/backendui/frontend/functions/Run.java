package ch.donkeycode.backendui.frontend.functions;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Run implements ResponseHandler<Void>, JsFunctionWrapper {

    @NonNull
    UUID responseId = UUID.randomUUID();

    @NonNull
    Runnable runnable;

    @Override
    public UUID getResponseId() {
        return responseId;
    }

    @Override
    public Class<Void> getHandledType() {
        return Void.class;
    }

    @Override
    public void handleResponse(Void response) {
        runnable.run();
    }

    @Override
    public String asJsFunction() {
        return String.format(
                "fireAction('%s')",
                responseId);
    }
}
