package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringShortQuestion;

import javax.validation.Validation;
import javax.validation.Validator;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class EmployersNameValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @Parameters({"name",
                        "'",
                        ".",
                        "-",
                        "&",
                        "9",
                        "a b-Z",
                        "A",
                        "abcdefabcdefabcdefabcdefabcdef"})

    public void validNames(String name) {
        NameStringShortQuestion question = new NameStringShortQuestion();
        question.setValue(name);
        assertTrue(validator.validate(question).isEmpty());
    }

    @Test
    public void validNameSpace() {
        NameStringShortQuestion question = new NameStringShortQuestion();
        question.setValue(" ");
        assertTrue(validator.validate(question).isEmpty());
    }

    @Test
    public void validNameNull() {
        NameStringShortQuestion question = new NameStringShortQuestion();
        question.setValue(null);
        assertFalse(validator.validate(question).isEmpty());
    }

    @Test
    @Parameters({"+", //invalid character
                        "", //empty
                        "\\", //invalid character
                        "γ", //non latin letter
                        "abcdefabcdefabcdefabcdefabcdefg"}) //more than 30 characters

    public void invalidNames(String name) {
        NameStringShortQuestion question = new NameStringShortQuestion();
        question.setValue(name);
        assertFalse(validator.validate(question).isEmpty());
    }
}
