package ch.donkeycode.backendui.frontend;

import java.util.UUID;

public interface ResponseHandler<T> {
    UUID getResponseId();

    Class<T> getHandledType();

    void handleResponse(T response);

    default void handleResponseUnchecked(Object response) {
        handleResponse((T)response);
    }
}
