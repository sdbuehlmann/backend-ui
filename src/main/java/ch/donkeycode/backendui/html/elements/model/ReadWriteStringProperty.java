package ch.donkeycode.backendui.html.elements.model;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class ReadWriteStringProperty<T> implements RenderableProperty<T> {

    @NonNull String title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull Persistor<T> valuePersistor;

    public ReadWriteStringProperty(
            @NonNull String title,
            @NonNull Function<T, String> valueExtractor,
            @NonNull Persistor<T> valuePersistor) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
    }

    public interface Persistor<T> {
        void persist(T valueHolder, String value) throws Exception;

        default void persistUnchecked(Object data, @NonNull String value) {
            try {
                persist((T) data, value);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Data is not of expected type.", e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to persist value.", e);
            }
        }
    }
}
