package ch.donkeycode.backendui.frontend.functions;

import ch.donkeycode.backendui.ResponseHandler;
import ch.donkeycode.backendui.frontend.dto.ui2be.ElementValuesDto;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Value
@Builder
public class CollectValuesAndRun implements ResponseHandler<ElementValuesDto> {
    @NonNull UUID parentElementId;
    @NonNull UUID responseId = UUID.randomUUID();
    @NonNull Runnable runnable;

    @NonNull
    @Singular
    List<CollectableElement> collectableElements;

    public String asJsFunction() {
        return String.format("""
                        sendResponse('%s',collectElementValues('%s'))
                        """,
                responseId,
                parentElementId
        );
    }

    @Override
    public UUID getRelatedElementId() {
        return parentElementId;
    }

    @Override
    public Class<ElementValuesDto> getHandledType() {
        return ElementValuesDto.class;
    }

    @Override
    public void handleResponse(ElementValuesDto response) {
        response.getValues()
                .forEach(elementValue -> findCollectableElement(elementValue.getElementId())
                        .getValueConsumer()
                        .accept(elementValue.getValue()));

        runnable.run();
    }

    private CollectableElement findCollectableElement(UUID id) {
        return collectableElements.stream()
                .filter(collectableElement -> collectableElement.getElementId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Element with id %s not found.",
                        id
                )));
    }

    @Value
    public static class CollectableElement {
        UUID elementId;
        Consumer<String> valueConsumer;
    }
}