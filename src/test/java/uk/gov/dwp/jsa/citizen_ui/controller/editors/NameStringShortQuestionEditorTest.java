package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringShortQuestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NameStringShortQuestionEditorTest {
    NameStringShortQuestionEditor sut = new NameStringShortQuestionEditor();

    @Test
    public void setNameStringShortQuestionIfValueStringQuestion() {
        String value = "expected";
        NameStringShortQuestion stringQuestion = new NameStringShortQuestion();
        stringQuestion.setValue(value);
        sut.setValue(stringQuestion);

        Object result = sut.getValue();
        assertTrue(result instanceof NameStringShortQuestion);
        assertEquals(value, ((NameStringShortQuestion) result).getValue());
    }

    @Test
    public void setStringIfNotStringQuestion() {
        String value = "expected";
        sut.setValue(value);

        Object result = sut.getValue();
        assertTrue(result instanceof String);
        assertEquals(value, result);
    }
}
