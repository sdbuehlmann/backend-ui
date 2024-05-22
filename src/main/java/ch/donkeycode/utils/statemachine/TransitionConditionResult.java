package ch.donkeycode.utils.statemachine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;


@AllArgsConstructor
@Builder
@Value
public class TransitionConditionResult<TMetadata> {

    @NonNull
    Boolean transitionAllowed;

    @NonNull
    @Builder.Default
    Optional<TMetadata> data = Optional.empty();

    public static class TransitionConditionResultBuilder<TMetadata> {
        public TransitionConditionResultBuilder<TMetadata> data(TMetadata data) {
            this.data$value = Optional.of(data);
            this.data$set = true;
            return this;
        }

        public TransitionConditionResultBuilder<TMetadata> data(Optional<TMetadata> data) {
            this.data$value = data;
            this.data$set = true;
            return this;
        }
    }
}
