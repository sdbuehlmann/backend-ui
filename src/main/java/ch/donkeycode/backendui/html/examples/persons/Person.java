package ch.donkeycode.backendui.html.examples.persons;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    String name;
    String prename;
}
