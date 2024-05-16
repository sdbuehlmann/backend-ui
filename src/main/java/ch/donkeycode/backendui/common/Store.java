/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2023.
 */

package ch.donkeycode.backendui.common;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Store-Class for values with unique identifier. Thread safe.
 *
 * @param <I> Type of unique entity identifier
 * @param <V> Type of entity
 */
public class Store<I, V> {
    private final Function<V, I> keyExtractor;
    private final ConcurrentHashMap<I, V> valuesById = new ConcurrentHashMap<>();

    private final Consumer<Integer> onSizeChanged;

    public Store(UnaryOperator<StoreParams.StoreParamsBuilder<I, V>> operator) {
        val params = operator.apply(StoreParams.builder()).build();
        keyExtractor = params.getKeyExtractor();
        onSizeChanged = params.getOnSizeChanged();
        addAll(params.getInitValues());
    }

    /**
     * Adds a value to this store. If there is already a value with the same id,
     * then it will be replaced.
     */
    public void add(final V value) {
        valuesById.put(keyExtractor.apply(value), value);
        onSizeChanged.accept(valuesById.size());
    }

    /**
     * Does add values to this store. Existing values with same ID will be replaced.
     */
    public void addAll(final Collection<V> values) {
        if (values.isEmpty()) {
            return;
        }

        val newValuesById = values.stream()
                .collect(Collectors.toMap(keyExtractor, value -> value));
        valuesById.putAll(newValuesById);
        onSizeChanged.accept(valuesById.size());
    }

    /**
     * Returns the stored value with the specified id. If no value is found, then an empty optional
     * will be returned.
     */
    public Optional<V> findById(final I id) {
        return Optional.ofNullable(valuesById.get(id));
    }

    /**
     * Returns all known id's of this store.
     */
    public Set<I> getAllIds() {
        return Set.copyOf(valuesById.keySet());
    }

    /**
     * Returns all currently stored values.
     */
    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(valuesById.values()));
    }

    /**
     * Removes a value by its id.
     */
    public Optional<V> remove(final I id) {
        val removedValue = Optional.ofNullable(valuesById.remove(id));
        removedValue.ifPresent(v -> onSizeChanged.accept(valuesById.size()));

        return removedValue;
    }

    /**
     * Removes all values whose IDs are contained in the provided set
     */
    public Set<V> removeAll(final Set<I> ids) {
        return selectAndRemove(v -> ids.contains(keyExtractor.apply(v)));
    }

    /**
     * Selects values by the defined selector.
     *
     * @param selector Selection function
     * @return The selected values
     */
    public Set<V> select(Predicate<V> selector) {
        val selectedValues = new HashSet<V>();

        computeAll((id, valueOptional) -> valueOptional
                .map(value -> {
                    if (selector.test(value)) {
                        selectedValues.add(value);
                    }
                    return value;
                })
                .orElse(null));

        return Collections.unmodifiableSet(selectedValues);
    }

    /**
     * Selects values by the defined selector and updates them by the defined updater.
     * Selection and update are processed atomically.
     *
     * @param selector Selection function
     * @param updater  Update function which will be processed for all selected entities
     * @return The updated values
     */
    public Set<V> selectAndUpdate(Predicate<V> selector, UnaryOperator<V> updater) {
        val updatedValues = new HashSet<V>();

        computeAll((id, valueOptional) -> valueOptional
                .map(value -> {
                    if (selector.test(value)) {
                        val updatedValue = updater.apply(value);
                        updatedValues.add(updatedValue);

                        return updatedValue;
                    }
                    return value;
                })
                .orElse(null));

        return Collections.unmodifiableSet(updatedValues);
    }

    /**
     * Selects value by id and updates it by the defined updater.
     * Selection and update are processed atomically.
     *
     * @param id      ID of selected value
     * @param updater Update function which will be processed for selected value (if present)
     * @return The updated value
     */
    public UpdateIfPresentResult<V> updateIfPresent(I id, UnaryOperator<V> updater) {
        val oldValue = new AtomicReference<Optional<V>>(Optional.empty());
        val updatedValue = new AtomicReference<Optional<V>>(Optional.empty());

        valuesById.compute(id, (sameId, nullableValue) -> {
            val optionalValue = Optional.ofNullable(nullableValue);
            oldValue.set(optionalValue);

            val updated = optionalValue.map(updater);
            updatedValue.set(updated);
            return updated.orElse(null);
        });

        return new UpdateIfPresentResult<>(oldValue.get(), updatedValue.get());
    }

    /**
     * Tries to select value by its id and updates it with the defined updater. If no value is present, then
     * the creator will be called instead of the updater.
     * <p>
     * Selection and update/create are processed atomically.
     *
     * @param id      ID of selected value
     * @param updater Update function which will be processed for selected value
     * @param creator Create function which will be called, if no value is present
     * @return The update/create result (old and new value)
     */
    public UpdateOrCreateResult<V> updateOrCreate(I id, UnaryOperator<V> updater, Supplier<V> creator) {
        val oldValue = new AtomicReference<Optional<V>>(Optional.empty());
        val updatedOrCreatedValue = new AtomicReference<V>();

        valuesById.compute(id, (sameId, nullableValue) -> {
            val optionalValue = Optional.ofNullable(nullableValue);
            oldValue.set(optionalValue);

            val updatedOrCreated = optionalValue
                    .map(updater)
                    .orElseGet(creator);

            updatedOrCreatedValue.set(updatedOrCreated);
            return updatedOrCreated;
        });

        if (oldValue.get().isEmpty()) {
            onSizeChanged.accept(valuesById.size());
        }

        return new UpdateOrCreateResult<>(oldValue.get(), updatedOrCreatedValue.get());
    }

    /**
     * Selects values by the defined selector and removes them from the store.
     * Selection and removing are processed atomically.
     *
     * @param selector Selection function for selecting entities
     * @return All removed values
     */
    public Set<V> selectAndRemove(Predicate<V> selector) {
        val deletedValues = new HashSet<V>();

        computeAll((id, valueOptional) -> valueOptional
                .map(value -> {
                    if (selector.test(value)) {
                        deletedValues.add(value);
                        return null;
                    }
                    return value;
                })
                .orElse(null));

        if (!deletedValues.isEmpty()) {
            onSizeChanged.accept(valuesById.size());
        }

        return Collections.unmodifiableSet(deletedValues);
    }

    private void computeAll(BiFunction<I, Optional<V>, V> remappingFunction) {
        val currentIds = getAllIds();

        currentIds.forEach(trainRunId ->
                valuesById.compute(trainRunId, (id, nullableDeltaTime) -> remappingFunction
                        .apply(id, Optional.ofNullable(nullableDeltaTime))));
    }


    @Value
    @Builder
    public static class StoreParams<I, V> {
        @NonNull
        Function<V, I> keyExtractor;

        @NonNull
        @Builder.Default
        Consumer<Integer> onSizeChanged = integer -> {
        };

        @NonNull
        @Builder.Default
        Set<V> initValues = Set.of();
    }

    @Value
    public static class UpdateOrCreateResult<T> {
        Optional<T> oldValue;
        T newValue;
    }

    @Value
    public static class UpdateIfPresentResult<T> {
        Optional<T> oldValue;
        Optional<T> newValue;
    }
}
