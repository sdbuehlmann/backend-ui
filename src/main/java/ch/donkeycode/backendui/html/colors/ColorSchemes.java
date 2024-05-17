package ch.donkeycode.backendui.html.colors;

import lombok.Getter;

public class ColorSchemes {
    @Getter
    public static class DarkBlue implements ColorScheme {
        public static final ColorScheme INSTANCE = new DarkBlue();

        private final Color primary = Color.rgba("001419ff");
        private final Color lighter = Color.rgba("ffffff37");
        private final Color darker = Color.rgba("00000053");
        private final Color text = Color.rgba("ffffffff");
    }

    @Getter
    public static class Green implements ColorScheme {
        public static final ColorScheme INSTANCE = new Green();

        private final Color primary = Color.rgba("576654ff");
        private final Color lighter = Color.rgba("ffffff33");
        private final Color darker = Color.rgba("00000033");
        private final Color text = Color.rgba("ffffffff");
    }
}
