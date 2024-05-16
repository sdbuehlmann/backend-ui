package ch.donkeycode.examples.persons;

import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.backendui.navigation.NavigationTarget;

import java.util.List;

public class NavigationTargetRegistry {

    public static NavigationTarget<Void> MAIN = new NavigationTarget<>();

    public static NavigationTarget<List<Person>> LIST_PEOPLE = new NavigationTarget<>();
    public static NavigationTarget<Person> EDIT_PERSON = new NavigationTarget<>();
    public static NavigationTarget<Void> SHOW_DONKEY_IMAGE = new NavigationTarget<>();
}
