package ch.donkeycode.backendui;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;
import java.util.function.Consumer;

@Value
@Builder
public class GenericResponseHandler<TPayload> implements ResponseHandler<TPayload> {

    @NonNull UUID responseId;
    @NonNull UUID relatedElementId;
    @NonNull Class<TPayload> handledType;
    @NonNull Consumer<TPayload> responseHandler;

    @Override
    public void handleResponse(TPayload response) {
        responseHandler.accept(response);
    }
}
