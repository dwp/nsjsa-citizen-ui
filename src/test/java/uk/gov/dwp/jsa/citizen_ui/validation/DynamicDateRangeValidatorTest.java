package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DynamicDateRangeConstraint;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DynamicDateRangeValidatorTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private DynamicDateRangeValidator sut;

    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

    private String lowLimitMessage = "lowLimitMessage";
    private String highLimitMessage = "highLimitMessage";

    @Before
    public void setUp() {
        when(mockContext.buildConstraintViolationWithTemplate(any())).thenReturn(mockBuilder);
        sut = new DynamicDateRangeValidator();
    }

    @Test
    public void initialize() {
        int daysAgo = 1;
        int monthsAgo = 2;
        int yearsAgo = 3;
        int daysUntil = 4;
        int monthsUntil = 5;
        int yearsUntil = 6;

        sut.initialize(getConstraint(daysAgo, monthsAgo, yearsAgo, daysUntil, monthsUntil, yearsUntil));

        assertEquals(LocalDate.now().plusDays(daysUntil).plusMonths(monthsUntil).plusYears(yearsUntil),
                sut.getHighLimit());
        assertEquals(LocalDate.now().minusDays(daysAgo).minusMonths(monthsAgo).minusYears(yearsAgo),
                sut.getLowLimit());
        assertEquals(lowLimitMessage, sut.getLowLimitMessage());
        assertEquals(highLimitMessage, sut.getHighLimitMessage());

        assertTrue(sut.isValidHigh());
        assertTrue(sut.isValidLow());
    }

    @Test
    public void initializeWhenLimitsAreEqual() {
        int daysAgo = 1;
        int monthsAgo = 1;
        int yearsAgo = 1;
        int daysUntil = -1;
        int monthsUntil = -1;
        int yearsUntil = -1;

        sut.initialize(getConstraint(daysAgo, monthsAgo, yearsAgo, daysUntil, monthsUntil, yearsUntil));
    }

    @Test(expected = IllegalStateException.class)
    public void whenLowLimitGreaterThanHighLimit_thenThrowsException() {
        int daysAgo = 1;
        int monthsAgo = 2;
        int yearsAgo = 3;
        int daysUntil = -4;
        int monthsUntil = -5;
        int yearsUntil = -6;

        sut.initialize(getConstraint(daysAgo, monthsAgo, yearsAgo, daysUntil, monthsUntil, yearsUntil));
    }

    @Parameters({"0, 0, 2, 0, 0, 2", // end date should not be  before
                        "0, 1, 0, 0, 1, 0", // start date should be after
                })
    @Test
    public void isNotValid(int daysAgo, int monthsAgo, int yearsAgo, int daysUntil,
                           int monthsUntil, int yearsUntil) {
        LocalDate date = LocalDate.now().minusYears(3);
        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setDay(date.getDayOfMonth());
        dateQuestion.setMonth(date.getMonthValue());
        dateQuestion.setYear(date.getYear());

        sut.initialize(getConstraint(daysAgo, monthsAgo, yearsAgo, daysUntil, monthsUntil, yearsUntil));

        assertFalse(sut.isValid(dateQuestion, mockContext));
    }

    @Parameters({"0, 0, 2, 0, 0, 2, 0, 0, 2", // end and start limit can be the same
                        "0, -1, 0, 0, 2, 0, 0, -2, 0", // can use negatives for start date
                        "0, 0, 1, 0, -1, 0, 0, 2, 0", // can use negatives for end date
                })
    @Test
    public void isValid(int daysAgo, int monthsAgo, int yearsAgo, int daysUntil,
                        int monthsUntil, int yearsUntil, int days, int months, int years) {
        LocalDate date = LocalDate.now().minusDays(days).minusMonths(months).minusYears(years);
        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setDay(date.getDayOfMonth());
        dateQuestion.setMonth(date.getMonthValue());
        dateQuestion.setYear(date.getYear());

        sut.initialize(getConstraint(daysAgo, monthsAgo, yearsAgo, daysUntil, monthsUntil, yearsUntil));

        assertTrue(sut.isValid(dateQuestion, mockContext));
    }

    private DynamicDateRangeConstraint getConstraint(int daysAgo, int monthsAgo, int yearsAgo, int daysUntil,
                                                     int monthsUntil, int yearsUntil) {

        return new DynamicDateRangeConstraint() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public String lowLimitMessage() {
                return lowLimitMessage;
            }

            @Override
            public String highLimitMessage() {
                return highLimitMessage;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public boolean low() {
                return true;
            }

            @Override
            public boolean high() {
                return true;
            }

            @Override
            public int daysAgo() {
                return daysAgo;
            }

            @Override
            public int monthsAgo() {
                return monthsAgo;
            }

            @Override
            public int yearsAgo() {
                return yearsAgo;
            }

            @Override
            public int daysUntil() {
                return daysUntil;
            }

            @Override
            public int monthsUntil() {
                return monthsUntil;
            }

            @Override
            public int yearsUntil() {
                return yearsUntil;
            }
        };
    }
}
