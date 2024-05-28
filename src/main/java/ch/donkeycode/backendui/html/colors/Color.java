package ch.donkeycode.backendui.html.colors;

import lombok.Value;

@Value
public class Color {
    int r;
    int g;
    int b;
    int a;

    public String getCssColor() {
        return "rgba(" + r + ", " + g + ", " + b + ", " + (a / 255.0) + ")";
    }

    public String getHexColor() {
        return String.format("#%02x%02x%02x%02x", r, g, b, a).toUpperCase();
    }

    public static Color rgba(String rgbaString) {
        if (rgbaString.length() != 8) {
            throw new IllegalArgumentException("Invalid rgba string: " + rgbaString);
        }

        int r = Integer.parseInt(rgbaString.substring(0, 2), 16);
        int g = Integer.parseInt(rgbaString.substring(2, 4), 16);
        int b = Integer.parseInt(rgbaString.substring(4, 6), 16);
        int a = Integer.parseInt(rgbaString.substring(6, 8), 16);

        return new Color(r, g, b, a);
    }
}
