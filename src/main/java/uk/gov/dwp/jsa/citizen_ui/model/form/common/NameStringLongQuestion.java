package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.Constants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NameStringLongQuestion extends StringQuestion {

    private static final int STRING_FIELD_MAX_SIZE = 60;

    @Size(max = STRING_FIELD_MAX_SIZE, message = "too.many.char.error")
    @NotEmpty(message = "empty.error")
    @Pattern(regexp = Constants.STRING_FIELD_REGEX, message = "invalid.error")
    @Override
    public String getValue() {
        return super.getValue();
    }
}
