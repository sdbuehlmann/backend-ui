package ch.donkeycode.utils;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class StringIterator {
    private final String text;
    private final AtomicInteger index = new AtomicInteger(0);

    public String continueUntil(CharSelector selector) {
        val sb = new StringBuilder();

        index.updateAndGet(currentindex -> {
            for (int index = currentindex; index < text.length(); index++) {
                val currentChar = text.charAt(index);
                if (selector.isSearched(currentChar)) {
                    return index;
                } else {
                    sb.append(currentChar);
                }
            }
            return text.length();
        });

        return sb.toString();
    }

    public boolean doesFindResult(CharSelector selector) {
        val currentIndex = index.get();

        for (int index = currentIndex; index < text.length(); index++) {
            val currentChar = text.charAt(index);
            if (selector.isSearched(currentChar)) {
                return true;
            }
        }

        return false;
    }

    public Result continueUntilOneOf(CharSelector... selectors) {
        return tryContinueUntilOneOf(selectors).orElseThrow(); // TODO Exception
    }

    public Optional<Result> tryContinueUntilOneOf(CharSelector... selectors) {
        val sb = new StringBuilder();
        val resultRef = new AtomicReference<Result>();

        index.updateAndGet(currentindex -> {
            for (int index = currentindex; index < text.length(); index++) {
                val currentChar = text.charAt(index);
                val matchingSelector = Arrays.stream(selectors)
                        .filter(selector -> selector.isSearched(currentChar))
                        .findAny();

                if (matchingSelector.isPresent()) {
                    resultRef.set(new Result(sb.toString(), matchingSelector.get()));
                    return index;
                } else {
                    sb.append(currentChar);
                }
            }
            return text.length();
        });

        return Optional.ofNullable(resultRef.get());
    }

    public StringIterator skipNext() {
        index.updateAndGet(i -> i + 1);
        return this;
    }


    public char getCurrent() {
        return text.charAt(index.get());
    }
    @Value
    @Builder
    public static class Result {
        String passed;
        CharSelector matchingSelector;
    }

    public interface CharSelector {
        CharSelector IS_ALPHA_NUMERIC = StringIterator.CharSelector.isAlphaNumeric();
        CharSelector IS_NOT_ALPHA_NUMERIC = StringIterator.CharSelector.isNotAlphaNumeric();
        CharSelector IS_WHITE_SPACE = StringIterator.CharSelector.isWhiteSpace();
        CharSelector IS_NO_WHITE_SPACE = value -> !value.equals(" ");
        CharSelector IS_EQUAL_SIGN = value -> value.equals("=");
        CharSelector IS_QUOTATION_MARK = value -> value.equals("\"");
        CharSelector IS_SLASH = value -> value.equals("/");
        CharSelector IS_GREATER_THAN = value -> value.equals(">");
        CharSelector IS_LESS_THAN = value -> value.equals("<");

        boolean isSearched(String value);

        default boolean isSearched(char value) {
            return isSearched(value + "");
        }

        static CharSelector isNotAlphaNumeric() {
            return value -> !isAlphaNumeric().isSearched(value);
        }

        static CharSelector isAlphaNumeric() {
            return value -> value.matches("^[a-zA-Z0-9]*$");
        }

        static CharSelector isWhiteSpace() {
            return value -> value.equals(" ");
        }
    }
}
