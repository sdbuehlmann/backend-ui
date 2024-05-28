package ch.donkeycode.backendui.common;

import ch.donkeycode.backendui.common.xml.XmlFragmentsParser;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class XmlFragmentsParserTest {
    private static final String SVG_STRING = """
            <svg width="800px" height="800px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
               <path d="M16 10L18.5768 8.45392C19.3699 7.97803 19.7665 7.74009 20.0928 7.77051C20.3773 7.79703 20.6369 7.944 20.806 8.17433C21 8.43848 21 8.90095 21 9.8259V14.1741C21 15.099 21 15.5615 20.806 15.8257C20.6369 16.056 20.3773 16.203 20.0928 16.2295C19.7665 16.2599 19.3699 16.022 18.5768 15.5461L16 14M6.2 18H12.8C13.9201 18 14.4802 18 14.908 17.782C15.2843 17.5903 15.5903 17.2843 15.782 16.908C16 16.4802 16 15.9201 16 14.8V9.2C16 8.0799 16 7.51984 15.782 7.09202C15.5903 6.71569 15.2843 6.40973 14.908 6.21799C14.4802 6 13.9201 6 12.8 6H6.2C5.0799 6 4.51984 6 4.09202 6.21799C3.71569 6.40973 3.40973 6.71569 3.21799 7.09202C3 7.51984 3 8.07989 3 9.2V14.8C3 15.9201 3 16.4802 3.21799 16.908C3.40973 17.2843 3.71569 17.5903 4.09202 17.782C4.51984 18 5.07989 18 6.2 18Z" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            """;

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
        val parts = XmlFragmentsParser.toFragments(SIMPLE_XML_STRING);

        // then
        Assertions.assertThat(parts).isNotEmpty();
    }

}