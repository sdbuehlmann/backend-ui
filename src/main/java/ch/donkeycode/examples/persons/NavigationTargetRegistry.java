package ch.donkeycode.examples.persons;

import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.examples.persons.services.WebCamService;

import java.awt.image.BufferedImage;
import java.util.List;

public class NavigationTargetRegistry {

    public static NavigationTarget<Void> MAIN = new NavigationTarget<>();

    public static NavigationTarget<List<Person>> LIST_PEOPLE = new NavigationTarget<>();
    public static NavigationTarget<Person> EDIT_PERSON = new NavigationTarget<>();
    public static NavigationTarget<BufferedImage> SHOW_BUFFERED_IMAGE = new NavigationTarget<>();
    public static NavigationTarget<Void> LIST_WEBCAMS = new NavigationTarget<>();
    public static NavigationTarget<WebCamService.CamInfo> SHOW_WEBCAM_STREAM = new NavigationTarget<>();
}
