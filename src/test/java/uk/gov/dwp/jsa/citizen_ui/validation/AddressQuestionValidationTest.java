package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.ADDRESS_MAX_LENGTH;

@RunWith(JUnitParamsRunner.class)
public class AddressQuestionValidationTest {

    public static final String ADDRESS_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH =
            "This is a very long line that goes beyond the max length";

    @Before
    public void setUp() {
    }

    @Test
    @Parameters({
                        "my address line of 99",
                        "my\\,address line 99",
                        "my address'line 99",
                        "my address line.99",
                        "my address line 99-"})
    public void anAddressLineWillBeValidIfItCompliesToTheRuleSet(String line) {
        assertTrue(new AddressCodeQuestionWrapper().validate(line).isEmpty());
    }

    @Test
    @Parameters({
                        "my address line?",
                        "my address$line",
                        "my@address line",
                        "myaddress£line",
                        "my address line#",
                        "my$address line",
                        "my address%line",
                        "my^address line",
                        "my address&line",
                        "my address*line",
                        "my±address line",
                        "§myaddress line"})
    public void anAddressLineShouldNotValidateIfItDoesNotComply(String line) {
        assertFalse(new AddressCodeQuestionWrapper().validate(line).isEmpty());
    }

    @Test
    @Parameters({"my\\,\\,address line 99",
                        "my address''line 99",
                        "my address line..99",
                        "my address line 99--",
                        "my  address line 99"})
    public void anAddressLineShouldNotValidateIfWeHaveMoreThanTwoSpecialConsecutiveCharacters(String line) {
        assertFalse(new AddressCodeQuestionWrapper().validate(line).isEmpty());
    }

    @Test
    public void anAddressLineShouldNotBeNullOrEmpty() {
        assertThat(new AddressCodeQuestionWrapper().validate(null).size(), is(1));
    }

    @Test
    public void anAddressLineShouldBeLimitedToMaximumLength() {
        AddressQuestion addressQuestion = new AddressQuestion();
        addressQuestion.setAddressLine1(ADDRESS_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH);
        assertThat(addressQuestion.getAddressLine1().length(), is(ADDRESS_MAX_LENGTH));
    }

    private class AddressCodeQuestionWrapper {
        private Validator validator;

        public AddressCodeQuestionWrapper() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        public Set<ConstraintViolation<AddressQuestion>> validate(final String line) {
            AddressQuestion addressQuestion = new AddressQuestion();

            addressQuestion.setAddressLine1(line);
            addressQuestion.setAddressLine2("");
            addressQuestion.setTownOrCity("some town or city");
            addressQuestion.setPostCode("A0 0AA");

            return validator.validate(addressQuestion);
        }
    }
}
