package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class BankAccountValidatorTest {

    private static Validator validator;
    private final BankAccountNumberValidatorWrapper wrapper = new BankAccountNumberValidatorWrapper();

    private String validHolder = "John Doe's Kitchen";
    private String validAccountNumber = "12345678";
    private SortCode validSortCode = new SortCode("122334");

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void GivenValidAccountHolder_ReturnTrue() {
        assertTrue(wrapper.validate(validHolder, validAccountNumber, validSortCode));
    }

    @Parameters({
            "",
            "This Is Supposed To Be A Name With 18 Characters Minimum",
            "Speci~~ Chara$£3rs",
            "John D^e"
    })
    @Test
    public void GivenInValidAccountHolder_ReturnFalse(final String accountNumber) {
        assertFalse(wrapper.validate(validHolder, accountNumber, validSortCode));
    }

    @Test
    public void GivenValidAccountNumber_ReturnTrue() {
        assertTrue(wrapper.validate(validHolder, validAccountNumber, validSortCode));
    }

    @Parameters(method = "invalidAccountNumbers")
    @Test
    public void GivenInValidAccountNumber_ReturnFalse(final String accountNumber) {
        assertFalse(wrapper.validate(validHolder, accountNumber, validSortCode));
    }

    @Test
    public void GivenValidSortCode_ReturnTrue() {
        assertTrue(wrapper.validate(validHolder, validAccountNumber, validSortCode));
    }

    @Parameters(method = "invalidSortCodes")
    @Test
    public void GivenInValidAccountNumber_ReturnFalse(final SortCode sortCode) {
        assertFalse(wrapper.validate(validHolder, validAccountNumber, sortCode));
    }

    private Object[] invalidSortCodes() {
        //Reflection to bypass sanitise method on SortCode
        final SortCode codeOne = new SortCode();
        ReflectionTestUtils.setField(codeOne, "code", "");
        final SortCode codeTwo = new SortCode();
        ReflectionTestUtils.setField(codeTwo, "code", "12");
        final SortCode codeThree = new SortCode();
        ReflectionTestUtils.setField(codeThree, "code", "1a1212");
        final SortCode codeFour = new SortCode();
        ReflectionTestUtils.setField(codeFour, "code", "1a1212");
        final SortCode codeFive = new SortCode();
        ReflectionTestUtils.setField(codeFive, "code", "1~2212");
        return new Object[]{
                codeOne, // empty values
                codeTwo, // some missing values
                codeThree, // non digits
                codeFour, // non digits
                codeFive // special chars
        };
    }

    private Object[] invalidAccountNumbers() {
        return new Object[]{
                null,
                "",
                "123",
                "1234567",
                "1234567a",
                "123456789",
                "ab3456789",
        };
    }

    private static class BankAccountNumberValidatorWrapper {
        boolean validate(String accountHolder, String accountNumber, SortCode sortCode) {
            BankAccountQuestion question = new BankAccountQuestion(accountHolder, sortCode, accountNumber);
            Set<ConstraintViolation<BankAccountQuestion>> violations = validator.validate(question);
            return violations.isEmpty();
        }
    }
}
