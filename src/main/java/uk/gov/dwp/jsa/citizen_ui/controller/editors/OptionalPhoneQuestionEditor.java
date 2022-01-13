package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.OptionalPhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.beans.PropertyEditorSupport;

public class OptionalPhoneQuestionEditor extends PropertyEditorSupport {
    @Override
    public void setValue(final Object value) {
        if (value instanceof StringQuestion) {
            OptionalPhoneQuestion question = new OptionalPhoneQuestion();
            question.setValue(((StringQuestion) value).getValue());
            super.setValue(question);
        } else {
            super.setValue(value);
        }
    }
}
