package uk.gov.dwp.jsa.citizen_ui.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class Email {
    private String value;

    public Email(final String value) {
        this.value = value;
    }

    public boolean isValid() {
        return StringUtils.isNoneBlank(value) && new EmailValidator().isValid(value, null);
    }
}
