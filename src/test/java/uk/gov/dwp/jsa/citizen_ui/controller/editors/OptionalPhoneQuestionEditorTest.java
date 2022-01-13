package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.OptionalPhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OptionalPhoneQuestionEditorTest {

    private static final String STRING_VALUE = "STRING_VALUE";
    private static final StringQuestion STRING_QUESTION = new StringQuestion(STRING_VALUE);
    private static final Object OBJECT = new Object();

    private OptionalPhoneQuestionEditor optionalEditor;

    @Test
    public void setsPhoneStringQuestionValue() {
        givenAnEditor();
        whenISetTheValueWith(STRING_QUESTION);
        thenThePhoneStringQuestionIsSet();
    }

    @Test
    public void setsNonStringQuestionValue() {
        givenAnEditor();
        whenISetTheValueWith(OBJECT);
        thenTheValueIsSet();
    }

    private void givenAnEditor() {
        optionalEditor = new OptionalPhoneQuestionEditor();
    }

    private void whenISetTheValueWith(final Object object) {
        optionalEditor.setValue(object);
    }

    private void thenTheValueIsSet() {
        assertThat(optionalEditor.getValue(), is(OBJECT));
    }

    private void thenThePhoneStringQuestionIsSet() {
        Object value = optionalEditor.getValue();
        assertThat(value, is(instanceOf(OptionalPhoneQuestion.class)));
        OptionalPhoneQuestion question = (OptionalPhoneQuestion) value;
        assertThat(question.getValue(), is(STRING_VALUE));
    }
}
