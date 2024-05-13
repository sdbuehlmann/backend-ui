package ch.donkeycode.examples.persons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    String name;
    String prename;
}
