package ch.donkeycode.examples.persons.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Person implements Buildable<Person> {
    @NonNull
    @lombok.Builder.Default
    UUID id = UUID.randomUUID();

    @NonNull
    @lombok.Builder.Default
    LocalDateTime lastUpdatedAt = LocalDateTime.now();

    @NonNull String name;
    @NonNull String prename;

    public static class PersonBuilder implements Buildable.Builder<Person> {

    }
}
