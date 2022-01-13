package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.Constants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UnlimitedStringQuestion extends StringQuestion {

    @NotEmpty(message = "empty.error")
    @Pattern(regexp = Constants.STRING_FIELD_REGEX, message = "invalid.error")
    @Override
    public String getValue() {
        return super.getValue();
    }
}
