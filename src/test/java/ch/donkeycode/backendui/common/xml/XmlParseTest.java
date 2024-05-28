package ch.donkeycode.backendui.common.xml;


import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class XmlParseTest {

    private static final String SIMPLE_XML_STRING = """
            <tagA attributeA1="test" attributeA2="test">
                <tagB attributeB1="B1"/>
                <tagC attributeC1="C1">text</tagC>
            </tagA>
            """;

    @Test
    public void test() {
        // given

        // when
        val node = XmlTreeBuilder.build(XmlFragmentsParser.toFragments(SIMPLE_XML_STRING));

        // then
        Assertions.assertThat(node.getChilds()).hasSize(2);
    }
}