package ch.donkeycode.backendui.html.utils;

import lombok.NonNull;
import lombok.Value;

@Value
public class KeyValue {
    @NonNull
    String key;
    @NonNull
    String value;
}
