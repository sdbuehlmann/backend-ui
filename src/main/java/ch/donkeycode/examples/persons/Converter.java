package ch.donkeycode.examples.persons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

    final static DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> Function<T, String> localDateTimeToString(Function<T, LocalDateTime> getter) {
        return t -> getter.apply(t).format(CUSTOM_FORMATTER);
    }

    public static <T> Function<T, String> objToString(Function<T, Object> getter) {
        return t -> String.valueOf(getter.apply(t));
    }

    public static <T> Function<T, String> booleanToString(Function<T, Boolean> getter) {
        return t -> String.valueOf(getter.apply(t));
    }

    public static <T> Function<T, String> intToString(Function<T, Integer> intGetter) {
        return t -> String.valueOf(intGetter.apply(t));
    }

    public static <T> Function<T, String> doubleToString(Function<T, Double> getter) {
        return t -> String.valueOf(getter.apply(t));
    }

    public static <T> Function<T, String> longToString(Function<T, Long> longGetter) {
        return t -> String.valueOf(longGetter.apply(t));
    }

//    public static <T> Function<T, String> localDateToString(Function<T, LocalDate> getter) {
//        return t -> I18n.getLocaleDate(getter.apply(t));
//    }

}
