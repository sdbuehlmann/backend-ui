package ch.donkeycode.backendui.html.colors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Color {
    @Getter
    private final String cssColor;

    public static Color rgba(int r, int g, int b, double a) {
        return new Color("rgba(" + r + ", " + g + ", " + b + ", " + a + ")");
    }

    public static Color rgba(String rgbaString) {
        if (rgbaString.length() != 8) {
            throw new IllegalArgumentException("Invalid rgba string: " + rgbaString);
        }

        int r = Integer.parseInt(rgbaString.substring(0, 2), 16);
        int g = Integer.parseInt(rgbaString.substring(2, 4), 16);
        int b = Integer.parseInt(rgbaString.substring(4, 6), 16);
        double a = Integer.parseInt(rgbaString.substring(6, 8), 16) / 255.0;

        return rgba(r, g, b, a);
    }
}
