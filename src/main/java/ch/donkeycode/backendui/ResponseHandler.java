package ch.donkeycode.backendui;

import java.util.UUID;

public interface ResponseHandler<T> {
    UUID getResponseId();

    UUID getRelatedElementId();

    Class<T> getHandledType();

    void handleResponse(T response);

    default void handleResponseUnchecked(Object response) {
        handleResponse((T)response);
    }
}
