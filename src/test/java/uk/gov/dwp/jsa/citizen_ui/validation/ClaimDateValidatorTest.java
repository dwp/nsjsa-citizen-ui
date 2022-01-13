package uk.gov.dwp.jsa.citizen_ui.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.ClaimDateConstraint;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClaimDateValidatorTest {

    private ClaimDateValidator sut;
    @Mock
    private ClaimDateConstraint mockAnnotation;
    @Mock
    private Object mockValue;
    @Mock
    private ConstraintValidatorContext mockContext;
    private ClaimStartDateQuestion claimStartDateQuestion;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

    @Before
    public void before() {
        claimStartDateQuestion = new ClaimStartDateQuestion();
        sut = createSut();

    }

    @Test
    public void nullValuesResturnFalse() {
        givenDayMonthAndYearAre(null, null, null);

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(claimStartDateQuestion, mockContext);

        assertTrue(actual);

    }

    @Test
    public void givenInvalidDate_isValid_returnsFalse() {

        givenDayMonthAndYearAre(32, 8, 2017);

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(claimStartDateQuestion, mockContext);

        assertTrue(actual);
    }

    @Test
    public void givenFutureDate_isValid_returnsFalse() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);

        LocalDate localDate = LocalDate.now().plusDays(1);
        givenDayMonthAndYearAre(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(claimStartDateQuestion, mockContext);

        assertFalse(actual);
    }

    @Test
    public void givenDateEarlierThan13Weeks_isValid_returnsFalse() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);

        LocalDate localDate = LocalDate.now().minusWeeks(14);
        givenDayMonthAndYearAre(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(claimStartDateQuestion, mockContext);

        assertFalse(actual);
    }

    @Test
    public void givenAcceptableClaimDate_isValid_returnsFalse() {

        LocalDate localDate = LocalDate.now();
        givenDayMonthAndYearAre(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(claimStartDateQuestion, mockContext);

        assertTrue(actual);
    }

    private void givenDayMonthAndYearAre(Integer day, Integer month, Integer year) {
        claimStartDateQuestion.setYear(year);
        claimStartDateQuestion.setMonth(month);
        claimStartDateQuestion.setDay(day);
    }

    private ClaimDateValidator createSut() {
        return new ClaimDateValidator();
    }
}
