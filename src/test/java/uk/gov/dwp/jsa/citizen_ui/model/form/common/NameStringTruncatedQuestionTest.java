package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class NameStringTruncatedQuestionTest {
    private static final String VALUE_WITH_WHITE_SPACE = "   Firstname   ";
    private static final String VALUE_WITH_NO_WHITE_SPACE = "Firstname";
    public static final String VALUE_WITH_SPACE_BETWEEN_WORDS = "Firstname McLastname";

    @Test
    public void setValue_containsNoBeginningOrLeadingWhiteSpace_setsValue() {
        NameStringTruncatedQuestion pojo = new NameStringTruncatedQuestion();
        pojo.setValue(VALUE_WITH_NO_WHITE_SPACE);
        assertEquals(ReflectionTestUtils.getField(pojo, "value"), VALUE_WITH_NO_WHITE_SPACE);
    }

    @Test
    public void setValue_containsBeginningOrLeadingWhiteSpace_removesWhiteSpaceAndSetsValue() {
        NameStringTruncatedQuestion pojo = new NameStringTruncatedQuestion();
        pojo.setValue(VALUE_WITH_WHITE_SPACE);
        assertEquals(ReflectionTestUtils.getField(pojo, "value"), VALUE_WITH_NO_WHITE_SPACE);
    }

    @Test
    public void setValue_containsWhiteSpaceBetweenWords_ignoresWhiteSpaceAndSetsValue() {
        NameStringTruncatedQuestion pojo = new NameStringTruncatedQuestion();
        pojo.setValue(VALUE_WITH_SPACE_BETWEEN_WORDS);
        assertEquals(ReflectionTestUtils.getField(pojo, "value"), VALUE_WITH_SPACE_BETWEEN_WORDS);
    }
}
