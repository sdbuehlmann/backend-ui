package ch.donkeycode.utils.statemachine;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Consumer;
import java.util.function.Function;

@Value
@Builder
public class Transition<TData, TConditionResultData> {
    @NonNull
    String to;

    @NonNull
    @Builder.Default
    TransitionCondition<TData, TConditionResultData> condition =
            (fromState, toState, data) -> TransitionConditionResult.<TConditionResultData>builder()
                    .transitionAllowed(true)
                    .build();

    @NonNull
    @Builder.Default
    OnTransitionHandler<TData> onTransition = (fromState, toState, projectDto) -> {
    };

    public static class TransitionBuilder<TData, TConditionResultData> {
        public TransitionBuilder<TData, TConditionResultData> to(Enum<?> e) {
            this.to = e.name();
            return this;
        }

        public TransitionBuilder<TData, TConditionResultData> to(String id) {
            this.to = id;
            return this;
        }

        public TransitionBuilder<TData, TConditionResultData> condition(TransitionCondition<TData, TConditionResultData> condition) {
            this.condition$value = condition;
            this.condition$set = true;
            return this;
        }

        public TransitionBuilder<TData, TConditionResultData> condition(Function<TData, TransitionConditionResult<TConditionResultData>> condition) {
            this.condition((fromState, toState, data) -> condition.apply(data));
            return this;
        }

        public TransitionBuilder<TData, TConditionResultData> onTransition(OnTransitionHandler<TData> onTransition) {
            this.onTransition$value = onTransition;
            this.onTransition$set = true;
            return this;
        }

        public TransitionBuilder<TData, TConditionResultData> onTransition(Consumer<TData> onTransition) {
            this.onTransition((fromState, toState, projectDto) -> onTransition.accept(projectDto));
            return this;
        }
    }

    @FunctionalInterface
    interface OnTransitionHandler<TData> {
        void handle(String fromState, String toState, TData statemachineData);
    }

    @FunctionalInterface
    interface TransitionCondition<TData, TConditionResultData> {
        TransitionConditionResult<TConditionResultData> check(String fromState, String toState, TData statemachineData);
    }
}
