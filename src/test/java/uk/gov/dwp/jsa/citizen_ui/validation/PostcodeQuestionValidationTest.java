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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(JUnitParamsRunner.class)
public class PostcodeQuestionValidationTest {

    private static final String VALID_OUTWARD_CODE = "A0";
    private static final String VALID_INWARD_CODE = "0AA";

    private static Validator validator;

    private final PostCodeQuestionWrapper postCodeQuestionWrapper = new PostCodeQuestionWrapper();

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void a_postcode_will_validate_when_we_have_a_simple_example() {
        assertTrue(postCodeQuestionWrapper.validate(VALID_OUTWARD_CODE + " " + VALID_INWARD_CODE));
    }

    @Test
    @Parameters({
                        "A00 " + VALID_INWARD_CODE,
                        "AA0 " + VALID_INWARD_CODE,
                        "A0A " + VALID_INWARD_CODE,
                        "AAA " + VALID_INWARD_CODE,
                        "AA00 " + VALID_INWARD_CODE,
                        "AA0A " + VALID_INWARD_CODE})
    public void a_postcode_outward_code_will_validate_when_we_match_a_character_sequence_of_A0_or_A00_or_AA0_or_A0A_or_AAA_or_AA00_or_AA0A_where_A_is_any_letter_and_0_is_any_numchar(
            String postcode) {
        assertTrue(postCodeQuestionWrapper.validate(postcode));
    }


    @Test
    @Parameters({
                        "!1 " + VALID_INWARD_CODE,
                        "B£ " + VALID_INWARD_CODE,
                        "$23 " + VALID_INWARD_CODE,
                        "C%4 " + VALID_INWARD_CODE,
                        "D5^ " + VALID_INWARD_CODE,
                        "!F67 " + VALID_INWARD_CODE,
                        "G£89 " + VALID_INWARD_CODE,
                        "IJ$0 " + VALID_INWARD_CODE,
                        "KL2% " + VALID_INWARD_CODE})
    public void a_postcode_outward_code_will_not_validate_when_it_deosnt_match_a_character_sequence_of_A0_or_A00_or_AA0_or_A0A_or_AAA_or_AA00_or_AA0A_where_A_is_any_letter_and_0_is_any_numchar(
            String postcode) {
        assertFalse(postCodeQuestionWrapper.validate(postcode));
    }

    @Test
    @Parameters({
                        VALID_OUTWARD_CODE + " !EF",
                        VALID_OUTWARD_CODE + " 2G£",
                        VALID_OUTWARD_CODE + " 3$H",
                        VALID_OUTWARD_CODE + " 4%J"})
    public void a_postcode_inward_code_will_not_validate_if_we_dont_have_a_three_character_sequence_of_number_letter_letter(
            String postcode) {
        assertFalse(postCodeQuestionWrapper.validate(postcode));
    }

    @Test
    @Parameters({
                        VALID_OUTWARD_CODE + " 0CA",
                        VALID_OUTWARD_CODE + " 0AI",
                        VALID_OUTWARD_CODE + " 0KA",
                        VALID_OUTWARD_CODE + " 0AM",
                        VALID_OUTWARD_CODE + " 0OA",
                        VALID_OUTWARD_CODE + " 0AV",
                        VALID_OUTWARD_CODE + " 0cA",
                        VALID_OUTWARD_CODE + " 0Ai",
                        VALID_OUTWARD_CODE + " 0kA",
                        VALID_OUTWARD_CODE + " 0Am",
                        VALID_OUTWARD_CODE + " 0oA",
                        VALID_OUTWARD_CODE + " 0Av"})
    public void a_postcode_inward_code_must_not_contain_the_letters_C_I_K_M_O_V(String postcode) {
        assertFalse(postCodeQuestionWrapper.validate(postcode));
    }

    private static class PostCodeQuestionWrapper {
        boolean validate(String postcode) {
            AddressQuestion addressQuestion = new AddressQuestion();

            addressQuestion.setAddressLine1("some address line 1");
            addressQuestion.setAddressLine2("");
            addressQuestion.setTownOrCity("some town or city");
            addressQuestion.setPostCode(postcode);

            Set<ConstraintViolation<AddressQuestion>> violations = validator.validate(addressQuestion);
            return violations.isEmpty();
        }
    }
}
