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
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DateRangeConstraint;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.time.DateTimeException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DateRangeValidatorTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private DateRangeValidator sut;

    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

    private String lowLimitMessage = "lowLimitMessage";
    private String highLimitMessage = "highLimitMessage";

    @Before
    public void setUp() {
        when(mockContext.buildConstraintViolationWithTemplate(any())).thenReturn(mockBuilder);
        sut = new DateRangeValidator();
    }

    @Test
    public void initialize() {
        int lowDay = 1;
        int lowMonth = 2;
        int lowYear = 1980;
        int highDay = 3;
        int highMonth = 5;
        int highYear = 2018;
        boolean low = true;
        boolean high = true;

        sut.initialize(getConstraint(lowDay, lowMonth, lowYear, highDay, highMonth, highYear, low, high));

        assertEquals(LocalDate.of(highYear, highMonth, highDay), sut.getHighLimit());
        assertEquals(LocalDate.of(lowYear, lowMonth, lowDay), sut.getLowLimit());
        assertEquals(lowLimitMessage, sut.getLowLimitMessage());
        assertEquals(highLimitMessage, sut.getHighLimitMessage());

        assertTrue(sut.isHigh());
        assertTrue(sut.isLow());
    }

    @Test
    public void initializeWhenLimitsAreEqual() {
        int lowDay = 1;
        int lowMonth = 2;
        int lowYear = 1980;
        int highDay = 1;
        int highMonth = 2;
        int highYear = 1980;
        boolean low = true;
        boolean high = true;

        sut.initialize(getConstraint(lowDay, lowMonth, lowYear, highDay, highMonth, highYear, low, high));
    }

    @Test(expected = IllegalStateException.class)
    public void whenLowLimitGreaterThanHighLimit_thenThrowsException() {
        int lowDay = 2;
        int lowMonth = 2;
        int lowYear = 2018;
        int highDay = 1;
        int highMonth = 2;
        int highYear = 2018;
        boolean low = true;
        boolean high = true;

        sut.initialize(getConstraint(lowDay, lowMonth, lowYear, highDay, highMonth, highYear, low, high));
    }

    @Test(expected = DateTimeException.class)
    public void whenInvalidDate_thenThrowsException() {
        int lowDay = 2;
        int lowMonth = 13;
        int lowYear = 2018;
        int highDay = 1;
        int highMonth = 2;
        int highYear = 2018;
        boolean low = true;
        boolean high = true;

        sut.initialize(getConstraint(lowDay, lowMonth, lowYear, highDay, highMonth, highYear, low, high));
    }

    @Parameters({"2018-01-20, 2018-01-21, 2018-01-25", // out of low bound
                        "2018-01-26, 2018-01-21, 2018-01-25", // out of high bound
                })
    @Test
    public void isNotValid(String testDate, String lowDate, String highDate) {
        LocalDate test = LocalDate.parse(testDate);
        LocalDate low = LocalDate.parse(lowDate);
        LocalDate high = LocalDate.parse(highDate);

        sut.initialize(getConstraint(low.getDayOfMonth(), low.getMonthValue(), low.getYear(), high.getDayOfMonth(),
                high.getMonthValue(), high.getYear(), true, true));

        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setDay(test.getDayOfMonth());
        dateQuestion.setMonth(test.getMonthValue());
        dateQuestion.setYear(test.getYear());

        assertFalse(sut.isValid(dateQuestion, mockContext));
    }

    @Parameters({"2018-01-21, 2018-01-21, 2018-01-25, true, true", // equals low bound
                        "2018-01-25, 2018-01-21, 2018-01-25, true, true", // equals high bound
                        "2018-01-20, 2018-01-21, 2018-01-25, false, true", // out of low bound but low false
                        "2018-01-26, 2018-01-21, 2018-01-25, true, false", // out of high bound but high false
                        "2018-01-23, 2018-01-21, 2018-01-25, true, true",
                })
    @Test
    public void isValid(String testDate, String lowDate, String highDate, boolean validLow, boolean validHigh) {
        LocalDate test = LocalDate.parse(testDate);
        LocalDate low = LocalDate.parse(lowDate);
        LocalDate high = LocalDate.parse(highDate);

        sut.initialize(getConstraint(low.getDayOfMonth(), low.getMonthValue(), low.getYear(), high.getDayOfMonth(),
                high.getMonthValue(), high.getYear(), validLow, validHigh));

        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setDay(test.getDayOfMonth());
        dateQuestion.setMonth(test.getMonthValue());
        dateQuestion.setYear(test.getYear());

        assertTrue(sut.isValid(dateQuestion, mockContext));
    }

    private DateRangeConstraint getConstraint(int lowDay, int lowMonth, int lowYear, int highDay,
                                              int highMonth, int highYear, boolean low, boolean high) {

        return new DateRangeConstraint() {
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
                return low;
            }

            @Override
            public boolean high() {
                return high;
            }

            @Override
            public int lowDay() {
                return lowDay;
            }

            @Override
            public int lowMonth() {
                return lowMonth;
            }

            @Override
            public int lowYear() {
                return lowYear;
            }

            @Override
            public int highDay() {
                return highDay;
            }

            @Override
            public int highMonth() {
                return highMonth;
            }

            @Override
            public int highYear() {
                return highYear;
            }
        };
    }
}
