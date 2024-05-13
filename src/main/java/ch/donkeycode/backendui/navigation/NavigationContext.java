package ch.donkeycode.backendui.navigation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NavigationContext {

    @Getter
    private final Navigator navigator;
}
