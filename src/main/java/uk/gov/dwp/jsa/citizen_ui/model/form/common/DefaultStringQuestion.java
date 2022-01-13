package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.Constants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class DefaultStringQuestion extends StringQuestion {

    public static final int MAX_VALUE = 100;

    @Size(max = MAX_VALUE, message = "common.too.many.char.error")
    @NotEmpty(message = "common.empty.error")
    @Pattern(regexp = Constants.STRING_FIELD_REGEX, message = "common.empty.error")
    @Override
    public String getValue() {
        return super.getValue();
    }
}
