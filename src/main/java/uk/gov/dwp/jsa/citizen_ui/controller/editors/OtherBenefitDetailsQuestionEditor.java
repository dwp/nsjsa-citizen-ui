package uk.gov.dwp.jsa.citizen_ui.controller.editors;

import uk.gov.dwp.jsa.citizen_ui.model.form.OtherBenefitDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.beans.PropertyEditorSupport;

public class OtherBenefitDetailsQuestionEditor extends PropertyEditorSupport {
    @Override
    public void setValue(final Object value) {
        if (value instanceof StringQuestion) {
            OtherBenefitDetailsQuestion question = new OtherBenefitDetailsQuestion();
            question.setValue(((StringQuestion) value).getValue());
            super.setValue(question);
        } else {
            super.setValue(value);
        }
    }
}
