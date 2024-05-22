package ch.donkeycode.utils.statemachine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@AllArgsConstructor
@Builder
@Value
public class TransitionResult<TMetadata> {
    @NonNull
    String fromState;

    @NonNull
    String toState;
    
    @NonNull
    Boolean transitionAllowed;

    @NonNull
    @Builder.Default
    Optional<TMetadata> data = Optional.empty();
}
