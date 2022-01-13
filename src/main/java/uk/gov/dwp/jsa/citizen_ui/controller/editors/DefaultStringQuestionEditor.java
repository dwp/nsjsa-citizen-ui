package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.DefaultStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.beans.PropertyEditorSupport;

public class DefaultStringQuestionEditor extends PropertyEditorSupport {
    @Override
    public void setValue(final Object value) {
        if (value instanceof StringQuestion) {
            DefaultStringQuestion question = new DefaultStringQuestion();
            question.setValue(((StringQuestion) value).getValue());
            super.setValue(question);
        } else {
            super.setValue(value);
        }
    }
}
