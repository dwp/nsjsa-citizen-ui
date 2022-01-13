package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.UnlimitedStringQuestion;

import javax.validation.Validation;
import javax.validation.Validator;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class EducationPlaceValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @Parameters({"name",
            "'",
            ".",
            "-",
            "&",
            "name and place"})
    public void validatePlaceWhenValid(String name) {
        UnlimitedStringQuestion question = new UnlimitedStringQuestion();
        question.setValue(name);
        assertTrue(validator.validate(question).isEmpty());
    }

    @Test
    @Parameters({" ", ""})
    public void validatePlaceWhenItsEmpty(String name) {
        UnlimitedStringQuestion question = new UnlimitedStringQuestion();
        question.setValue(name);
        assertFalse(validator.validate(question).isEmpty());
    }

    @Test
    @Parameters({" ",
                    "@",
                    "£",
                    "$"})
    public void validatePlaceWhenWeHaveASpecialCharacter(String name) {
        UnlimitedStringQuestion question = new UnlimitedStringQuestion();
        question.setValue(name);
        assertFalse(validator.validate(question).isEmpty());
    }
}
