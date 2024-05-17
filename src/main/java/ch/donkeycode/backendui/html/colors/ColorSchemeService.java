package ch.donkeycode.backendui.html.colors;

import org.springframework.stereotype.Service;

@Service
public class ColorSchemeService {

    public ColorScheme getActiveColorScheme() {
        return ColorSchemes.Green.INSTANCE;
    }
}
