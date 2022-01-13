package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DefaultStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultStringQuestionEditorTest {

    DefaultStringQuestionEditor sut = new DefaultStringQuestionEditor();

    @Test
    public void setDefaultStringQuestionIfValueStringQuestion() {
        String value = "expected";
        StringQuestion stringQuestion = new StringQuestion();
        stringQuestion.setValue(value);
        sut.setValue(stringQuestion);

        Object result = sut.getValue();
        assertTrue(result instanceof DefaultStringQuestion);
        assertEquals(value, ((DefaultStringQuestion) result).getValue());
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
