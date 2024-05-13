package ch.donkeycode.backendui.frontend.functions;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.dto.ChildElementValuesDto;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Value
@Builder
public class CollectValuesAndRun implements ResponseHandler<ChildElementValuesDto>, JsFunctionWrapper {
    @NonNull
    UUID parentElementId;

    @NonNull
    UUID responseId = UUID.randomUUID();

    @NonNull
    @Singular
    List<CollectableElement> collectableElements;

    @NonNull
    Runnable runnable;

    @Override
    public String asJsFunction() {
        return String.format("""
                        collectAllValues('%s','%s')
                        """,
                responseId,
                parentElementId
        );
    }

    @Override
    public Class<ChildElementValuesDto> getHandledType() {
        return ChildElementValuesDto.class;
    }

    @Override
    public void handleResponse(ChildElementValuesDto response) {
        response
                .getElementValues()
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
