package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class EmployersPhoneValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validNumbers() {
        Arrays.asList(
                "0123456789",
                "01234567890",
                "012345678901",
                "0           ",
                "0          ",
                "0         ",
                "0 2 4 6 8 0",
                "0 2 4 6 8 0 ",
                "0    1      ").forEach(this::validate);
    }

    private void validate(String phone) {
        StringQuestion question = new PhoneStringQuestion();
        question.setValue(phone);
        assertTrue(phone, validator.validate(question).isEmpty());
    }

    @Test
    public void invalidNameNull() {
        PhoneStringQuestion question = new PhoneStringQuestion();
        question.setValue(null);
        Set<ConstraintViolation<PhoneStringQuestion>> violations =
                validator.validate(question, ValidationSequence.class);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    @Parameters({"", //empty
                        "023456789a", //text
                        "02345%6789", //invalid chars
                        "1234567890", //not starting with 0
                        "012345678", //less than 10
                        "01234567890123"}) //more than 13

    public void invalidNumbers(String phone) {
        PhoneStringQuestion question = new PhoneStringQuestion();
        question.setValue(phone);
        Set<ConstraintViolation<PhoneStringQuestion>> violations =
                validator.validate(question, ValidationSequence.class);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }


}
