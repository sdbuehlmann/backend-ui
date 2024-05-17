package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.ResponseHandler;

public interface ResponseHandlerRegisterer {
    default <T extends ResponseHandler<?>> T registerAndReturn(T responseHandler) {
        register(responseHandler);
        return responseHandler;
    }

    void register(ResponseHandler<?> responseHandler);
}
