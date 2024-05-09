package ch.donkeycode.backendui.html.examples.persons;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Group {
    List<Person> persons;
}
