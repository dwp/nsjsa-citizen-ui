package uk.gov.dwp.jsa.citizen_ui.validation;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum.BETWEEN_16_17;
import static uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum.GREATER_THAN_18;
import static uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum.LESS_THAN_16;

@RunWith(MockitoJUnitRunner.class)
public class DateOfBirthValidatorTest {

    private DateOfBirthValidator dateOfBirthValidator = new DateOfBirthValidator();
    private DateOfBirthQuestion dateOfBirthQuestion;
    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Before
    public void setUp() {
        dateOfBirthQuestion = new DateOfBirthQuestion();
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
    }

    @Test
    public void isValidReturnsTrueFor19YearsOld() {
        givenDateOfBirthQuestionIsSet(LocalDate.now().minusYears(19));

        boolean valid = dateOfBirthValidator.isValid(dateOfBirthQuestion, mockContext);

        assertThat(valid, is(true));
        assertThat(dateOfBirthQuestion.getDateofBirthConditionsEnum(), is(GREATER_THAN_18));
    }

    @Test
    public void isValidReturnsFalseFor15YearsOld() {
        givenDateOfBirthQuestionIsSet(LocalDate.now().minusYears(15));

        boolean valid = dateOfBirthValidator.isValid(dateOfBirthQuestion, mockContext);

        assertThat(valid, is(false));
        assertThat(dateOfBirthQuestion.getDateofBirthConditionsEnum(), is(LESS_THAN_16));
    }

    @Test
    public void isValidReturnsFalseFor17YearsOld() {
        givenDateOfBirthQuestionIsSet(LocalDate.now().minusYears(17));

        boolean valid = dateOfBirthValidator.isValid(dateOfBirthQuestion, mockContext);

        assertThat(valid, is(true));
        assertThat(dateOfBirthQuestion.getDateofBirthConditionsEnum(), is(BETWEEN_16_17));
    }

    @Test
    public void isValidReturnsTrueFor80YearsOld() {
        givenDateOfBirthQuestionIsSet(LocalDate.now().minusYears(80));

        boolean valid = dateOfBirthValidator.isValid(dateOfBirthQuestion, mockContext);

        assertThat(valid, is(true));
        assertThat(dateOfBirthQuestion.getDateofBirthConditionsEnum(), is(GREATER_THAN_18));
    }
    @Test
    public void twoDigitDateParser() {
        ArrayList<Integer> results = new ArrayList<>();
        List<Integer> twoDigitDates = Arrays.asList(90,95,98,99,00,01,02,03,04,05,06,07,8,9,10);
        List<Integer> expectedDates =
          Arrays.asList(1990,1995,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010);

        for (Integer date : twoDigitDates) {
            results.add(ReflectionTestUtils.invokeMethod(dateOfBirthValidator,"parseTwoDigitYear", date));
        }

        MatcherAssert.assertThat(results, is(expectedDates));
    }

    private void givenDateOfBirthQuestionIsSet(final LocalDate dateFor19yrsOld) {
        dateOfBirthQuestion.setDay(dateFor19yrsOld.getDayOfMonth());
        dateOfBirthQuestion.setMonth(dateFor19yrsOld.getMonthValue());
        dateOfBirthQuestion.setYear(dateFor19yrsOld.getYear());
    }
}
