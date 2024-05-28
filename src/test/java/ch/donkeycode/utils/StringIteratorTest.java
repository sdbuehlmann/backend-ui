package ch.donkeycode.utils;


import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

class StringIteratorTest {

    private static final StringIterator.CharSelector IS_ALPHA_NUMERIC = StringIterator.CharSelector.isAlphaNumeric();
    private static final StringIterator.CharSelector IS_NOT_ALPHA_NUMERIC = StringIterator.CharSelector.isNotAlphaNumeric();
    private static final StringIterator.CharSelector IS_WHITE_SPACE = StringIterator.CharSelector.isWhiteSpace();
    private static final StringIterator.CharSelector IS_NO_WHITE_SPACE = value -> !value.equals(" ");
    private static final StringIterator.CharSelector IS_EQUAL_SIGN = value -> value.equals("=");
    private static final StringIterator.CharSelector IS_QUOTATION_MARK = value -> value.equals("\"");
    private static final StringIterator.CharSelector IS_SLASH = value -> value.equals("/");
    private static final StringIterator.CharSelector IS_GREATER_THAN = value -> value.equals(">");
    private static final StringIterator.CharSelector IS_LESS_THAN = value -> value.equals("<");


    @Test
    public void parse_withClosedTag_expectClosedTag() {
        val tag = parse("<tag attributeA=\"valueA\" attributeC attributeB = \"valueB\"/>");

        Assertions.assertThat(tag).isEqualTo(Tag.builder()
                .name("tag")
                .type(TagType.CLOSED)
                .attribute(new Attribute("attributeA", Optional.of("valueA")))
                .attribute(new Attribute("attributeB", Optional.of("valueB")))
                .attribute(new Attribute("attributeC", Optional.empty()))
                .build());
    }

    @Test
    public void parse_withOpeningTag_expectOpeningTag() {
        val tag = parse("<anotherTag attributeA   attributeC =  \"valueC\" attributeB>");

        Assertions.assertThat(tag).isEqualTo(Tag.builder()
                .name("anotherTag")
                .type(TagType.OPENING)
                .attribute(new Attribute("attributeA", Optional.empty()))
                .attribute(new Attribute("attributeB", Optional.empty()))
                .attribute(new Attribute("attributeC", Optional.of("valueC")))
                .build());
    }

    @Test
    public void parse_withClosingTag_expectClosingTag() {
        val tag = parse("</anotherTag>");

        Assertions.assertThat(tag).isEqualTo(Tag.builder()
                .name("anotherTag")
                .type(TagType.CLOSING)
                .attributes(Set.of())
                .build());
    }

    @Test
    public void test() {
        // given
        val iterator = new StringIterator("<tag attributeA=\"valueA\" attributeC attributeB = \"valueB\">");

        // when then
        Assertions.assertThat(iterator.continueUntil(StringIterator.CharSelector.isAlphaNumeric())).isEqualTo("<");
        Assertions.assertThat(iterator.continueUntil(StringIterator.CharSelector.isWhiteSpace())).isEqualTo("tag");

        Assertions.assertThat(findNextAttribute(iterator))
                .isEqualTo(new Attribute("attributeA", Optional.of("valueA")));
        Assertions.assertThat(findNextAttribute(iterator))
                .isEqualTo(new Attribute("attributeC", Optional.empty()));
        Assertions.assertThat(findNextAttribute(iterator))
                .isEqualTo(new Attribute("attributeB", Optional.of("valueB")));

//        Assertions.assertThat(iterator.continueUntil(StringIterator.CharSelector.isAlphaNumeric())).isEqualTo(" ");
//        Assertions.assertThat(iterator.continueUntil(StringIterator.CharSelector.isNotAlphaNumeric())).isEqualTo("attributeA");

//        Assertions
//                .assertThat(iterator.tryContinueUntilOneOf(IS_QUOTATION_MARK, IS_ALPHA_NUMERIC))
//                .contains(StringIterator.Result.builder()
//                        .matchingSelector(IS_QUOTATION_MARK)
//                        .passed("=")
//                .build());
//
//        Assertions
//                .assertThat(iterator
//                        .skipNext() // is quotation mark
//                        .tryContinueUntilOneOf(IS_QUOTATION_MARK))
//                .contains(StringIterator.Result.builder()
//                        .matchingSelector(IS_QUOTATION_MARK)
//                        .passed("valueA")
//                        .build());
//
//        Assertions
//                .assertThat(iterator
//                        .skipNext() // is quotation mark
//                        .tryContinueUntilOneOf(IS_ALPHA_NUMERIC))
//                .contains(StringIterator.Result.builder()
//                        .matchingSelector(IS_ALPHA_NUMERIC)
//                        .passed(" ")
//                        .build());
//
//        Assertions
//                .assertThat(iterator
//                        .tryContinueUntilOneOf(IS_NOT_ALPHA_NUMERIC))
//                .contains(StringIterator.Result.builder()
//                        .matchingSelector(IS_NOT_ALPHA_NUMERIC)
//                        .passed("attributeC")
//                        .build());
//
//        Assertions
//                .assertThat(iterator.tryContinueUntilOneOf(IS_EQUAL_SIGN, IS_ALPHA_NUMERIC))
//                .contains(StringIterator.Result.builder()
//                        .matchingSelector(IS_ALPHA_NUMERIC)
//                        .passed(" ")
//                        .build());
    }

    private Tag parse(String tagText) {
        val iterator = new StringIterator(tagText);

        iterator.continueUntilOneOf(IS_LESS_THAN);
        val isClosing = iterator
                .continueUntilOneOf(IS_SLASH, IS_ALPHA_NUMERIC)
                .getMatchingSelector() == IS_SLASH;

        if (isClosing) {
            iterator.skipNext(); // is slash
        }

        val tagName = iterator.continueUntilOneOf(IS_NOT_ALPHA_NUMERIC);

        val tagBuilder = Tag.builder();
        while (iterator.doesFindResult(IS_ALPHA_NUMERIC)) {
            tagBuilder.attribute(findNextAttribute(iterator));
        }

        val isClosed = iterator
                .continueUntilOneOf(IS_SLASH, IS_GREATER_THAN)
                .getMatchingSelector() == IS_SLASH;

        return tagBuilder
                .type(getType(isClosing, isClosed))
                .name(tagName.getPassed())
                .build();
    }

    public TagType getType(boolean isClosing, boolean isClosed) {
        if (isClosing && isClosed) {
            throw new IllegalArgumentException("Can not be a closing and a closed tag at the same time");
        }

        if (isClosing) {
            return TagType.CLOSING;
        }

        if (isClosed) {
            return TagType.CLOSED;
        }

        return TagType.OPENING;
    }

    private Attribute findNextAttribute(StringIterator iterator) {
        val startTest = iterator.continueUntilOneOf(IS_ALPHA_NUMERIC);
        val attributeName = iterator
                .continueUntilOneOf(IS_NOT_ALPHA_NUMERIC)
                .getPassed();

//        val attributeType = iterator.continueUntilOneOf(
//                IS_EQUAL_SIGN, IS_ALPHA_NUMERIC);

        val attributeType = iterator.continueUntilOneOf(IS_NO_WHITE_SPACE);
        if (iterator.getCurrent() == '=') {
            // attribute has value
            iterator.continueUntilOneOf(IS_QUOTATION_MARK);
            val attributeValue = iterator.skipNext() // is quotation mark
                    .continueUntilOneOf(IS_QUOTATION_MARK).getPassed();

            return new Attribute(attributeName, Optional.of(attributeValue));
        } else {
            // attribute has no value
            return new Attribute(attributeName, Optional.empty());
        }

//        if (attributeType.getMatchingSelector() == IS_EQUAL_SIGN) {
//            // attribute has value
//
//            iterator.continueUntilOneOf(IS_QUOTATION_MARK);
//            val attributeValue = iterator.skipNext() // is quotation mark
//                    .continueUntilOneOf(IS_QUOTATION_MARK).getPassed();
//
//            return new Attribute(attributeName, Optional.of(attributeValue));
//
//
//        } else if (attributeType.getMatchingSelector() == IS_ALPHA_NUMERIC) {
//            // attribute has no value
//            return new Attribute(attributeName
//            , Optional.empty());
//        }

//        throw new IllegalStateException();
    }

    @Value
    public static class Attribute {
        String name;
        Optional<String> value;
    }

    @Value
    @Builder
    public static class Tag {
        @NonNull
        String name;

        @NonNull
        TagType type;

        @NonNull
        @Singular
        Set<Attribute> attributes;
    }

    public enum TagType {
        OPENING,
        CLOSING,
        CLOSED
    }
}