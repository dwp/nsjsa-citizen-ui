package uk.gov.dwp.jsa.citizen_ui.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.SortCodeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm.SORT_CODE_REGEX;

@Component
public class SortCodeValidator implements ConstraintValidator<SortCodeConstraint, SortCode>, Validator {

    private static final int SORT_CODE_MIN_LENGTH = 6;
    private static final int SORT_CODE_MAX_LENGTH = 8;

    private static final Pattern VALID_PATTERN = Pattern.compile(SORT_CODE_REGEX);

    private static final String BLANK_SORT_CODE_MSG = "bankaccount.sortcode.blank";

    private static final String INVALID_SORT_CODE_MSG = "bankaccount.sortcode.invalid";

    @Override
    public boolean isValid(final SortCode sortCode, final ConstraintValidatorContext context) {
        String code = sortCode.getCode();

        if (StringUtils.isBlank(code)) {
            return addInvalidMessage(context, BLANK_SORT_CODE_MSG);
        }
        if (!VALID_PATTERN.matcher(code).matches()) {
            return addInvalidMessage(context, INVALID_SORT_CODE_MSG);
        }

        return code.length() == SORT_CODE_MIN_LENGTH || code.length() == SORT_CODE_MAX_LENGTH;
    }
}
