package ch.donkeycode.backendui.navigation;

public interface Navigator {
    <T> void navigate(NavigationTarget<T> target, T data);
}
