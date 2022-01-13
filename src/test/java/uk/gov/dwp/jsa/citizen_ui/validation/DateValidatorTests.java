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
import org.springframework.beans.BeanWrapper;
import uk.gov.dwp.jsa.citizen_ui.util.DateValidationUtils;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class DateValidatorTests {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    protected static final String DayField = "dayField";
    protected static final String MonthField = "monthField";
    protected static final String YearField = "yearField";
    protected static final String IncompleteStartDateLocalePrefix = "date.error.start";
    protected static final String IncompleteEndDateLocalePrefix = "date.error.end";
    protected static final String UnknownDateLocalePrefix = "date.error.unknown";
    protected static final String UnknownSingleDateLocalePrefix = "date.error.single.unknown.date";
    protected static final DateValidator.DateQuestionIdentify dateQuestionIdentify =
            DateValidator.DateQuestionIdentify.DATE_RANGE_START_DATE;
    protected DateValidator sut;
    @Mock
    protected PropertyAccessorFactoryWrapper mockPropertyAccessorFactoryWrapper;
    @Mock
    protected BeanWrapper mockBeanWrapper;
    @Mock
    protected BeanWrapperHelper mockBeanWrapperHelper;
    @Mock
    protected ValidDateConstraint mockAnnotation;
    @Mock
    protected DateValidationUtils mockDateValidationUtils;
    @Mock
    protected Object mockValue;
    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext mockNodeContext;

    @Before
    public void before() {
        sut = createSut();
        when(mockContext.buildConstraintViolationWithTemplate(any())).thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(any())).thenReturn(mockNodeContext);
        when(mockPropertyAccessorFactoryWrapper.forBeanPropertyAccess(any(Object.class))).thenReturn(mockBeanWrapper);

        when(mockAnnotation.currentDateQuestion()).thenReturn(dateQuestionIdentify);
        when(mockAnnotation.incompleteStartDateLocalePrefix()).thenReturn(IncompleteStartDateLocalePrefix);
        when(mockAnnotation.incompleteEndDateLocalePrefix()).thenReturn(IncompleteEndDateLocalePrefix);
        when(mockAnnotation.nonExistingDateLocalePrefix()).thenReturn(UnknownDateLocalePrefix);
        when(mockAnnotation.nonExistingSingleDateLocale()).thenReturn(UnknownSingleDateLocalePrefix);
        when(mockAnnotation.dayField()).thenReturn(DayField);
        when(mockAnnotation.monthField()).thenReturn(MonthField);
        when(mockAnnotation.yearField()).thenReturn(YearField);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialise_doesNotAcceptNullAnnotation() {
        sut.initialize(null);
    }

    @Test(expected = IllegalStateException.class)
    public void isValid_requiresInitialisation() {
        sut.isValid(mockValue, mockContext);
    }

    @Test
    public void isValid_doesNotAcceptNullValue() {
        DateValidationUtils.ParsableDateQuestion validParsableDateQuestion = generateValidParsableDateQuestion();
        doReturn(validParsableDateQuestion).when(mockDateValidationUtils).getUsersSubmittedDateQuestionFieldsBeforeParsed(any());
        sut.initialize(mockAnnotation);
        boolean result = sut.isValid(null, mockContext);
        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isValid_doesNotAcceptNullContext() {
        sut.initialize(mockAnnotation);
        sut.isValid(mockValue, null);
    }

    @Test
    @Parameters({
                "0, 10, 1981",    // 0 day
                "-1, 10, 1981",   // negative day
                "32, 10, 2018",   // day too high
                "8, 0, 1981",     // 0 month
                "8, -1, 1981",    // negative month
                "1, 13, 2019",    // month too high
                "29, 02, 2018"})   // 29 Feb non leap year
    public void isValid_invalidDates_returnsFalse(int day, int month, int year) {
        DateValidationUtils.ParsableDateQuestion validParsableDateQuestion = generateValidParsableDateQuestion(day, month, year);
        doReturn(validParsableDateQuestion).when(mockDateValidationUtils).getUsersSubmittedDateQuestionFieldsBeforeParsed(any());

        when(mockBeanWrapperHelper.getIntPropertyValue(eq(DayField), any())).thenReturn(day);
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(MonthField), any())).thenReturn(month);
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(YearField), any())).thenReturn(year);

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(mockValue, mockContext);

        assertFalse(actual);
    }

    @Test
    @Parameters({
                "20, 10, 1981",
                "28, 02, 2018",
                "29, 02, 2016",
                "01, 01, 2010",
                "31, 12, 2019",
                "31, 12, 2019"})
    public void isValid_validDates_returnsTrue(int day, int month, int year) {
        DateValidationUtils.ParsableDateQuestion validParsableDateQuestion = generateValidParsableDateQuestion(day, month, year);
        doReturn(validParsableDateQuestion).when(mockDateValidationUtils).getUsersSubmittedDateQuestionFieldsBeforeParsed(any());

        when(mockBeanWrapperHelper.getIntPropertyValue(eq(DayField), any())).thenReturn(day);
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(MonthField), any())).thenReturn(month);
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(YearField), any())).thenReturn(year);

        sut.initialize(mockAnnotation);
        boolean actual = sut.isValid(mockValue, mockContext);

        assertTrue(actual);
    }

    protected DateValidator createSut() {
        return new DateValidator(mockPropertyAccessorFactoryWrapper, mockBeanWrapperHelper, mockDateValidationUtils);
    }

    private DateValidationUtils.ParsableDateQuestion generateValidParsableDateQuestion() {
        LocalDateTime now = LocalDateTime.now();
        DateValidationUtils.ParsableDateQuestion
                parsableDateQuestion = new DateValidationUtils.ParsableDateQuestion();
        parsableDateQuestion.setDay(String.valueOf(now.getDayOfMonth()));
        parsableDateQuestion.setMonth(String.valueOf(now.getMonthValue()));
        parsableDateQuestion.setYear(String.valueOf(now.getYear()));
        return parsableDateQuestion;
    }

    private DateValidationUtils.ParsableDateQuestion generateValidParsableDateQuestion(int day, int month, int year) {
        DateValidationUtils.ParsableDateQuestion
                parsableDateQuestion = new DateValidationUtils.ParsableDateQuestion();
        parsableDateQuestion.setDay(String.valueOf(day));
        parsableDateQuestion.setMonth(String.valueOf(month));
        parsableDateQuestion.setYear(String.valueOf(year));
        return parsableDateQuestion;
    }
}
