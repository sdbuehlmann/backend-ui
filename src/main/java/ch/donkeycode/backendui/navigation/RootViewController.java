package ch.donkeycode.backendui.navigation;

/**
 * The root view controller is the first view controller that is shown when the application is started.
 * An application needs to have exactly one root view controller.
 */
public interface RootViewController extends ViewController<Void> {
    NavigationTarget<Void> ROOT_VIEW = new NavigationTarget<>();

    @Override
    default NavigationTarget<Void> getHandledNavigationTarget() {
        return ROOT_VIEW;
    }
}
