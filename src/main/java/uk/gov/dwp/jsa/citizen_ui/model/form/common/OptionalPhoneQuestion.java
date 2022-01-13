package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.OptionalUKPhoneNumberConstraint;

public class OptionalPhoneQuestion extends StringQuestion {

    public static final int FIELD_MAX_LENGTH = 20;

    public OptionalPhoneQuestion(final String value) {
        super(value);
    }

    public OptionalPhoneQuestion() {
    }

    @OptionalUKPhoneNumberConstraint
    @Override
    public String getValue() {
        return super.getValue();
    }
}
