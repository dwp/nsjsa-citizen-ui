package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringShortQuestion;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class NameStringShortQuestionValidationTest {

    public static final String NAME_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH =
            "This is a very long line that goes beyond the max length";

    @Test
    @Parameters({
                        "my name string question",
                        "my name string'question",
                        "my name.string question",
                        "my&name string'question",
                        "my name string question-"})
    public void aNameStringQuestionWillBeValidIfItCompliesToTheRuleSet(String name) {
        assertTrue(new NameStringQuestionWrapper().validate(name).isEmpty());
    }

    @Test
    @Parameters({
                        "my name string question?",
                        "my name$string question",
                        "my@name string question",
                        "myname£string question",
                        "my name string question#",
                        "my$name string question",
                        "my name%string question",
                        "my^name string question",
                        "my name*string question",
                        "my±name string question",
                        "§myname string question"})
    public void aNameStringQuestionShouldNotValidateIfItDoesNotComply(String name) {
        assertFalse(new NameStringQuestionWrapper().validate(name).isEmpty());
    }

    @Test
    public void aNameStringQuestionShouldNotBeNullOrEmpty() {
        assertFalse(new NameStringQuestionWrapper().validate(null).isEmpty());
    }

    @Test
    public void aNameStringQuestionShouldNotValidateForAMaximumLength() {
        assertFalse(new NameStringQuestionWrapper().validate(NAME_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH).isEmpty());
    }

    private class NameStringQuestionWrapper {
        private Validator validator;

        public NameStringQuestionWrapper() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        public Set<ConstraintViolation<NameStringShortQuestion>> validate(final String name) {
            NameStringShortQuestion employerStringQuestion = new NameStringShortQuestion();
            employerStringQuestion.setValue(name);
            return validator.validate(employerStringQuestion);
        }
    }
}
