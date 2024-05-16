package ch.donkeycode.backendui.common;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StringUtils {
    public static String getCropped(String orig, int maxLength) {
        if (orig.length() <= maxLength) {
            return orig;
        }

        return orig.substring(0, maxLength) + "...";
    }
}
