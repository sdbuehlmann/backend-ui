package ch.donkeycode.utils.statemachine;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@Value
@Builder
public class State<TData, TTransitionConditionData> {
    @NonNull
    String id;

    @NonNull
    @Builder.Default
    Collection<Transition<TData, TTransitionConditionData>> transitions = List.of();

    @NonNull
    @Builder.Default
    BiConsumer<String, TData> onEntry = (id, data) -> {
    };

    @NonNull
    @Builder.Default
    BiConsumer<String, TData> onExit = (id, data) -> {
    };

    public Optional<Transition<TData, TTransitionConditionData>> tryGetTransitionTo(String targetStateId) {
        return this.transitions.stream()
                .filter(transition -> transition.getTo().equals(targetStateId))
                .findAny();
    }

    public static class StateBuilder<TData, TTransitionConditionData> {
        public StateBuilder<TData, TTransitionConditionData> id(Enum<?> e) {
            this.id = e.name();
            return this;
        }

        public StateBuilder<TData, TTransitionConditionData> id(String id) {
            this.id = id;
            return this;
        }

        public StateBuilder<TData, TTransitionConditionData> transition(@NonNull UnaryOperator<Transition.TransitionBuilder<TData, TTransitionConditionData>> builderOperator) {
            val transition = builderOperator.apply(Transition.builder()).build();

            if (this.transitions$value == null) {
                this.transitions$value = new ArrayList<>();
                this.transitions$set = true;
            }

            this.transitions$value.add(transition);
            return this;
        }
    }
}
