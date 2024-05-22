package ch.donkeycode.utils.statemachine;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Value
@Builder
public class StateMachine<TData, TTransitionConditionData> {

    @NonNull
    Collection<State<TData, TTransitionConditionData>> states;

    public void updateState(Enum<?> currentStateId, Enum<?> nextStateId, TData data) throws ForbiddenOperationException {
        this.updateState(currentStateId.name(), nextStateId.name(), data);
    }

    public void updateState(Enum<?> currentStateId, String nextStateId, TData data) throws ForbiddenOperationException {
        this.updateState(currentStateId.name(), nextStateId, data);
    }

    public void updateState(String currentStateId, String nextStateId, TData data) throws ForbiddenOperationException {
        val currentState = this.getState(currentStateId);

        val allowedTransition = currentState.tryGetTransitionTo(nextStateId)
                .filter(transition -> transition.getCondition().check(currentStateId, nextStateId, data).getTransitionAllowed())
                .orElseThrow(() -> new ForbiddenOperationException(String.format(
                        "It is not allowed to update from status %s to status %s",
                        currentStateId,
                        nextStateId)));

        currentState.getOnExit().accept(currentStateId, data);
        allowedTransition.getOnTransition().handle(currentStateId, nextStateId, data);
        this.getState(nextStateId).getOnEntry().accept(nextStateId, data);
    }

    public Collection<TransitionResult<TTransitionConditionData>> getTransitions(Enum<?> stateId, TData data) {
        return this.getTransitions(stateId.name(), data);
    }

    public Collection<TransitionResult<TTransitionConditionData>> getTransitions(String stateId, TData data) {
        return this.getState(stateId).getTransitions().stream()
                .map(transition -> {
                    val conditionResult = transition.getCondition().check(stateId, transition.getTo(), data);

                    return TransitionResult.<TTransitionConditionData>builder()
                            .fromState(stateId)
                            .toState(transition.getTo())
                            .transitionAllowed(conditionResult.getTransitionAllowed())
                            .data(conditionResult.getData())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private State<TData, TTransitionConditionData> getState(Enum<?> e) {
        return this.getState(e.name());
    }

    private State<TData, TTransitionConditionData> getState(String id) {
        return this.states.stream()
                .filter(state -> state.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No state %s in project phase state machine", id)));
    }

    public static class StateMachineBuilder<TData, TTransitionConditionData> {
        public StateMachineBuilder<TData, TTransitionConditionData> state(@NonNull UnaryOperator<State.StateBuilder<TData, TTransitionConditionData>> builderOperator) {
            val state = builderOperator.apply(State.builder()).build();


            if (this.states == null) {
                this.states = new ArrayList<>();
            }

            this.states.add(state);

            return this;
        }
    }
}
