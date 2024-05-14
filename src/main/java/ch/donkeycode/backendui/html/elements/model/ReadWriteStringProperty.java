package ch.donkeycode.backendui.html.elements.model;

import ch.donkeycode.examples.persons.model.Buildable;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.function.Function;

@Value
public class ReadWriteStringProperty<
        T extends Buildable<T>,
        B extends Buildable.Builder<T>
        > implements RenderableProperty<T> {

    @NonNull String title;
    @NonNull Function<T, String> valueExtractor;
    @NonNull BuilderPersistor<T, B> valuePersistor;

    public ReadWriteStringProperty(
            @NonNull String title,
            @NonNull Function<T, String> valueExtractor,
            @NonNull BuilderPersistor<T, B> valuePersistor) {
        this.title = title;
        this.valueExtractor = valueExtractor;
        this.valuePersistor = valuePersistor;
    }

    public interface Persistor<T> {
        void persist(Buildable.Builder<T> valueHolder, String value) throws Exception;

        default void persistUnchecked(Object data, @NonNull String value) {
            try {
                val buildable = (Buildable<T>) data;
                val builder = buildable.toBuilder();

                persist(builder, value);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Data is not of expected type.", e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to persist value.", e);
            }
        }
    }

    public interface BuilderPersistor<T, B extends Buildable.Builder<T>> {
        void persist(B builder, String value) throws Exception;

        default void persistUnchecked(Object builderObject, @NonNull String value) {
            try {
                val builder = (B)builderObject;

                persist(builder, value);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Data is not of expected type.", e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to persist value.", e);
            }
        }
    }
}
