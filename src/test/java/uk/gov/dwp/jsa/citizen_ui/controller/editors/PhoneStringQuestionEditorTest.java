package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PhoneStringQuestionEditorTest {


    private static final String STRING_VALUE = "STRING_VALUE";
    private static final StringQuestion STRING_QUESTION = new StringQuestion(STRING_VALUE);
    private static final Object OBJECT = new Object();

    private PhoneStringQuestionEditor editor;

    @Test
    public void setsPhoneStringWithQuestionValue() {
        givenAnEditor();
        whenISetTheValueWith(STRING_QUESTION);
        thenThePhoneStringWithQuestionIsSet();
    }

    @Test
    public void setsNonStringQuestionValue() {
        givenAnEditor();
        whenISetTheValueWith(OBJECT);
        thenTheValueIsSet();
    }

    private void givenAnEditor() {
        editor = new PhoneStringQuestionEditor();
    }

    private void whenISetTheValueWith(final Object object) {
        editor.setValue(object);
    }

    private void thenTheValueIsSet() {
        assertThat(editor.getValue(), is(OBJECT));
    }

    private void thenThePhoneStringWithQuestionIsSet() {
        Object value = editor.getValue();
        assertThat(value, is(instanceOf(PhoneStringQuestion.class)));
        PhoneStringQuestion question = (PhoneStringQuestion) value;
        assertThat(question.getValue(), is(STRING_VALUE));
    }
}
