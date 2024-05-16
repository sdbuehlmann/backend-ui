package ch.donkeycode.backendui.frontend.functions;

import ch.donkeycode.backendui.ResponseHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Run implements ResponseHandler<Void> {

    @NonNull UUID responseId = UUID.randomUUID();
    @NonNull UUID relatedElementId;
    @NonNull Runnable runnable;

    @Override
    public Class<Void> getHandledType() {
        return Void.class;
    }

    @Override
    public void handleResponse(Void response) {
        runnable.run();
    }

    public String asJsFunction() {
        return String.format(
                "sendResponse('%s')",
                responseId);
    }
}
