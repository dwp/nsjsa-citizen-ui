package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
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
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.TOWN_OR_CITY_MAX_LENGTH;

@RunWith(JUnitParamsRunner.class)
public class TownOrCityQuestionValidationTest {

    public static final String TOWN_OR_CITY_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH =
            "This is a very long line that goes beyond the max length";

    @Test
    @Parameters({
                        "my town or city",
                        "my\\,town or city",
                        "my town or'city",
                        "my town or city-"})
    public void aTownOrCityQuestionWillBeValidIfItCompliesToTheRuleSet(String townOrCity) {
        assertTrue(new TownOrCityQuestionWrapper().validate(townOrCity).isEmpty());
    }

    @Test
    @Parameters({
                        "my town or city?",
                        "my town$or city",
                        "my@town or city",
                        "mytown£or city",
                        "my town or city#",
                        "my$town or city",
                        "my town%or city",
                        "my^town or city",
                        "my town&or city",
                        "my town*or city",
                        "my±town or city",
                        "§mytown or city"})
    public void aTownOrCityQuestionShouldNotValidateIfItDoesNotComply(String townOrCity) {
        assertFalse(new TownOrCityQuestionWrapper().validate(townOrCity).isEmpty());
    }

    @Test
    @Parameters({"my\\,\\,town or city 99",
                        "my town''or city 99",
                        "my town or..city 99",
                        "my town or city  99",
                        "my town or city 99--"})
    public void anAddressLineShouldNotValidateIfWeHaveMoreThanTwoSpecialConsecutiveCharacters(String townOrCity) {
        assertFalse(new TownOrCityQuestionWrapper().validate(townOrCity).isEmpty());
    }

    @Test
    public void aTownOrCityQuestionShouldNotBeNullOrEmpty() {
        assertThat(new TownOrCityQuestionWrapper().validate(null).size(), is(1));
    }

    @Test
    public void aTownOrCityQuestionShouldBeLimitedToMaximumLength() {
        AddressQuestion addressQuestion = new AddressQuestion();
        addressQuestion.setTownOrCity(TOWN_OR_CITY_QUESTION_THAT_EXCEEDS_THE_MAX_LENGTH);

        assertThat(addressQuestion.getTownOrCity().length(), is(TOWN_OR_CITY_MAX_LENGTH));
    }

    private class TownOrCityQuestionWrapper {
        private Validator validator;

        public TownOrCityQuestionWrapper() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        public Set<ConstraintViolation<AddressQuestion>> validate(final String townOrCity) {
            AddressQuestion addressQuestion = new AddressQuestion();

            addressQuestion.setAddressLine1("some address line 1");
            addressQuestion.setAddressLine2("");
            addressQuestion.setTownOrCity(townOrCity);
            addressQuestion.setPostCode("A0 0AA");

            return validator.validate(addressQuestion);
        }
    }
}
