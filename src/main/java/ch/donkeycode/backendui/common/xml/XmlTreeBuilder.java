package ch.donkeycode.backendui.common.xml;

import ch.donkeycode.backendui.common.xml.model.XmlNode;
import ch.donkeycode.backendui.common.xml.model.XmlTextContent;
import ch.donkeycode.utils.CastHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlTreeBuilder {


    public static XmlNode build(List<XmlFragmentsParser.XmlFragment> fragments) {
        val iterator = new XmlFragmentsIterator(fragments);

        return handleNextNodeLevel(iterator);
    }

    private static XmlNode handleNextNodeLevel(XmlFragmentsIterator iterator) {
        val nodeBuilder = XmlNode.builder();

        val currentFragment = iterator.getNext()
                .flatMap(CastHelper.tryCast(XmlFragmentsParser.Tag.class))
                .orElseThrow(); // TODO

        while (true) {
            val nextFragment = iterator.peekNext().orElseThrow();
            CastHelper
                    .tryCast(nextFragment, XmlFragmentsParser.Content.class)
                    .map(content -> {
                        iterator.getNext(); // remove from iterator
                        return new XmlTextContent(content.getValue());
                    })
                    .ifPresent(nodeBuilder::childElement);

            CastHelper
                    .tryCast(nextFragment, XmlFragmentsParser.Tag.class)
                    .filter(tag -> tag.getType().equals(XmlFragmentsParser.TagType.CLOSED))
                    .ifPresent(closedTag -> {
                        iterator.getNext(); // remove from iterator
                        nodeBuilder.childElement(XmlNode.builder()
                                .name(closedTag.getName())
                                .attributes(closedTag.getAttributes())
                                .childElements(List.of())
                                .build());
                    });

            CastHelper
                    .tryCast(nextFragment, XmlFragmentsParser.Tag.class)
                    .filter(tag -> tag.getType().equals(XmlFragmentsParser.TagType.OPENING))
                    .ifPresent(closedTag -> {
                        nodeBuilder.childElement(handleNextNodeLevel(iterator));
                    });

            val closingTagOptional = CastHelper
                    .tryCast(nextFragment, XmlFragmentsParser.Tag.class)
                    .filter(tag -> tag.getType().equals(XmlFragmentsParser.TagType.CLOSING));

            if (closingTagOptional.isPresent()) {
                val closingTag = closingTagOptional.get();

                return nodeBuilder
                        .name(currentFragment.getName())
                        .attributes(currentFragment.getAttributes())
                        .build();
            }
        }
    }

    @RequiredArgsConstructor
    public static class XmlFragmentsIterator {
        private final List<XmlFragmentsParser.XmlFragment> fragments;
        private final AtomicInteger indexRef = new AtomicInteger(0);

        public Optional<XmlFragmentsParser.XmlFragment> peekNext() {
            val index = indexRef.get();
            if (index >= fragments.size()) {
                return Optional.empty();
            }

            return Optional.of(fragments.get(index));
        }

        public Optional<XmlFragmentsParser.XmlFragment> getNext() {
            val index = indexRef.getAndIncrement();
            if (index >= fragments.size()) {
                return Optional.empty();
            }

            return Optional.of(fragments.get(index));
        }
    }
}
