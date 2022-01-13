package uk.gov.dwp.jsa.citizen_ui.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanWrapper;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.BooleanAndDateFieldQuestions;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.DateRangeQuestionWithBoolean;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.UnableToWorkDueToIllnessQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.util.DateValidationUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BooleanWithHiddenDateConstraint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.validation.BooleanWithHiddenDateValidator.QuestionIdentifier.UNABLE_TO_WORK_DUE_TO_ILLNESS;
import static uk.gov.dwp.jsa.citizen_ui.validation.DateValidator.DateQuestionIdentify.*;

@RunWith(MockitoJUnitRunner.class)
public class BooleanWithHiddenDateValidatorTest {
    private static final String COOKIE_CLAIM_ID = "claim_id";
    private static final String MOCK_CLAIM_ID = "12345678-1234-4321-87654321";
    private static final String dayField = "day";
    private static final String monthField = "month";
    private static final String yearField = "year";

    @Mock
    private DateValidationUtils mockDateValidationUtils;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Claim mockClaim;

    @Mock
    private ClaimStartDateQuestion mockClaimStartDateQuestion;

    @Mock
    private Cookie mockCookie;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Mock
    private PropertyAccessorFactoryWrapper mockFactoryWrapper;

    @Mock
    private BeanWrapperHelper mockBeanWrapperHelper;

    @Mock
    private BeanWrapper mockBeanWrapper;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockConstraintViolationBuilder;

    private DateValidator dateValidator;
    private DateRangeValidator dateRangeValidator;
    private BooleanWithHiddenDateValidator sut;


    // BooleanWithHiddenDate constraint properties
    private String missingYesNoAnswerLocal;
    private String missingStartDateLocal;
    private String missingEndDateLocal;
    private String startDateMustBeAfterMinimumAllowedDate;
    private String endDateMustBeAfterMinimumAllowedDate;
    private String startDateIsAfterEndDateLocal;
    private String startDateMustBeInThePastLocal;
    private String endDateMustBeInThePastLocal;
    private String startDateMustBeReal;
    private String dateCantBeAlpha;
    private LocalDate minimumLowDate;
    private BooleanWithHiddenDateValidator.QuestionIdentifier questionIdentifier;
    private BooleanWithHiddenDateConstraint booleanWithHiddenDateConstraint;

    @Before
    public void setUp() {
        dateValidator = new DateValidator(mockFactoryWrapper, mockBeanWrapperHelper, mockDateValidationUtils);
        dateRangeValidator = new DateRangeValidator();
        this.sut = new BooleanWithHiddenDateValidator(
                dateValidator, dateRangeValidator, mockDateValidationUtils, mockClaimRepository);
    }

    @Test
    public void initialize_unableToWorkDueToIllnessInitialization_setsCorrectValues() throws Exception {
        LocalDate minimumAllowedDate = LocalDate.now().minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);
        thenItSetsTheFieldsWithCorrectValues(sut);
    }

    @Test
    public void unableToWorkDueToIllnessValidation_userHasNotAnsweredQuestion_returnsFalse() {
        LocalDate now = LocalDate.now();
        LocalDate minimumAllowedDate = now.minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(null, null);

        assertFalse(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    @Test
    public void unableToWorkDueToIllnessValidation_answeredNo_returnsTrue() {
        LocalDate now = LocalDate.now();
        LocalDate minimumAllowedDate = now.minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(FALSE, null);

        assertTrue(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    @Test
    public void unableToWorkDueToIllnessValidation_answeredYesWithValidDate_returnsTrue() {
        LocalDate now = LocalDate.now();
        LocalDate minimumAllowedDate = now.minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);

        LocalDate start = minimumAllowedDate.plusMonths(1);
        LocalDate end = minimumAllowedDate.plusMonths(1).plusDays(5);
        DateQuestion citizensInputtedStartDate = new DateQuestion(start.getDayOfMonth(), start.getMonthValue(), start.getYear());
        DateQuestion citizensInputtedEndDate = new DateQuestion(end.getDayOfMonth(), end.getMonthValue(), end.getYear());
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(TRUE, new DateRangeQuestionWithBoolean(citizensInputtedStartDate, citizensInputtedEndDate));

        DateValidationUtils.ParsableDateQuestion startDateParsableQuestion = generateParsableDateQuestion(citizensInputtedStartDate);
        DateValidationUtils.ParsableDateQuestion endDateParsableQuestion = generateParsableDateQuestion(citizensInputtedEndDate);

        mockDateValidatorDependencies(DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.getValue(), DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.getValue(),
                citizensInputtedStartDate, startDateParsableQuestion, endDateParsableQuestion);

        assertTrue(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    @Test
    public void unableToWorkDueToIllnessValidation_answeredYesWithInvalidDateFormats_returnsFalse() {
        int nonExistingMonth = 66;
        LocalDate now = LocalDate.now();
        LocalDate minimumAllowedDate = now.minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);

        LocalDate start = minimumAllowedDate.plusMonths(1);
        LocalDate end = minimumAllowedDate.plusMonths(1).plusDays(5);
        DateQuestion citizensInputtedStartDate = new DateQuestion(start.getDayOfMonth(), nonExistingMonth, start.getYear());
        DateQuestion citizensInputtedEndDate = new DateQuestion(end.getDayOfMonth(), nonExistingMonth, end.getYear());
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(TRUE, new DateRangeQuestionWithBoolean(citizensInputtedStartDate, citizensInputtedEndDate));

        DateValidationUtils.ParsableDateQuestion startDateParsableQuestion = generateParsableDateQuestion(citizensInputtedStartDate);
        DateValidationUtils.ParsableDateQuestion endDateParsableQuestion = generateParsableDateQuestion(citizensInputtedEndDate);

        mockDateValidatorDependencies(DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.getValue(), DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.getValue(),
                citizensInputtedStartDate, startDateParsableQuestion, endDateParsableQuestion);

        assertFalse(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    @Test
    public void unableToWorkDueToIllnessValidation_answeredYesWithInvalidDateRanges_returnsFalse() {
        LocalDate minimumAllowedDate = LocalDate.now().minusMonths(2);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);

        LocalDate start = minimumAllowedDate.minusMonths(3);
        LocalDate end = minimumAllowedDate.minusMonths(3).plusDays(5);
        DateQuestion citizensInputtedStartDate = new DateQuestion(start.getDayOfMonth(), start.getMonthValue(), start.getYear());
        DateQuestion citizensInputtedEndDate = new DateQuestion(end.getDayOfMonth(), start.getMonthValue(), end.getYear());
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(TRUE, new DateRangeQuestionWithBoolean(citizensInputtedStartDate, citizensInputtedEndDate));

        DateValidationUtils.ParsableDateQuestion startDateParsableQuestion = generateParsableDateQuestion(citizensInputtedStartDate);
        DateValidationUtils.ParsableDateQuestion endDateParsableQuestion = generateParsableDateQuestion(citizensInputtedEndDate);

        mockDateValidatorDependencies(DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.getValue(), DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.getValue(),
                citizensInputtedStartDate, startDateParsableQuestion, endDateParsableQuestion);

        assertFalse(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    @Test
    public void unableToWorkDueToIllnessValidation_answeredYesWithStartDateAfterEndDate_returnsFalse() {
        LocalDate minimumAllowedDate = LocalDate.now().minusMonths(1);
        givenBooleanWithHiddenDateConstraints(UNABLE_TO_WORK_DUE_TO_ILLNESS, minimumAllowedDate);
        whenItsUnableToWorkDueToIllnessPageInitialization(minimumAllowedDate);

        LocalDate start = minimumAllowedDate.plusDays(10);
        LocalDate end = minimumAllowedDate.plusDays(5);
        DateQuestion citizensInputtedStartDate = new DateQuestion(start.getDayOfMonth(), start.getMonthValue(), start.getYear());
        DateQuestion citizensInputtedEndDate = new DateQuestion(end.getDayOfMonth(), end.getMonthValue(), end.getYear());
        BooleanAndDateFieldQuestions questions =
                new UnableToWorkDueToIllnessQuestion(TRUE, new DateRangeQuestionWithBoolean(citizensInputtedStartDate, citizensInputtedEndDate));

        DateValidationUtils.ParsableDateQuestion startDateParsableQuestion = generateParsableDateQuestion(citizensInputtedStartDate);
        DateValidationUtils.ParsableDateQuestion endDateParsableQuestion = generateParsableDateQuestion(citizensInputtedEndDate);

        mockDateValidatorDependencies(DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.getValue(), DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.getValue(),
                citizensInputtedStartDate, startDateParsableQuestion, endDateParsableQuestion);

        assertFalse(sut.isValid(questions, mockContext));
        assertEquals(ReflectionTestUtils.getField(sut, "minimumLowDate"), this.minimumLowDate);
        assertEquals(ReflectionTestUtils.getField(sut, "maximumHighDate"), LocalDate.now().minusDays(1));
    }

    private void whenItsUnableToWorkDueToIllnessPageInitialization(final LocalDate claimStartDate) {
        when(mockDateValidationUtils.getCurrentHttpRequest()).thenReturn(mockRequest);
        when(mockRequest.getCookies()).thenReturn(new Cookie[] {mockCookie});
        when(mockCookie.getName()).thenReturn(COOKIE_CLAIM_ID);
        when(mockCookie.getValue()).thenReturn(MOCK_CLAIM_ID);
        when(mockClaim.getClaimStartDate()).thenReturn(Optional.of(mockClaimStartDateQuestion));
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockClaimStartDateQuestion.getDay()).thenReturn(claimStartDate.getDayOfMonth());
        when(mockClaimStartDateQuestion.getMonth()).thenReturn(claimStartDate.getMonthValue());
        when(mockClaimStartDateQuestion.getYear()).thenReturn(claimStartDate.getYear());
        doNothing().when(mockContext).disableDefaultConstraintViolation();
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockConstraintViolationBuilder);
        when(mockConstraintViolationBuilder.addConstraintViolation()).thenReturn(mockContext);
        sut.initialize(booleanWithHiddenDateConstraint);
    }

    private DateValidationUtils.ParsableDateQuestion generateParsableDateQuestion(final DateQuestion dateQuestion) {
        return new DateValidationUtils.ParsableDateQuestion(
            String.valueOf(
                dateQuestion.getDay()), String.valueOf(dateQuestion.getMonth()), String.valueOf(dateQuestion.getYear()));
    }

    private void mockDateValidatorDependencies(
            final String identifierStart, final String identifierEnd, final DateQuestion startDate,
            final DateValidationUtils.ParsableDateQuestion startDateParsableQuestion,
            final DateValidationUtils.ParsableDateQuestion endDateParsableQuestion) {
        when(mockDateValidationUtils.getUsersSubmittedDateQuestionFieldsBeforeParsed(eq(identifierStart)))
                .thenReturn(startDateParsableQuestion);
        when(mockDateValidationUtils.getUsersSubmittedDateQuestionFieldsBeforeParsed(eq(identifierEnd)))
                .thenReturn(endDateParsableQuestion);
        when(mockFactoryWrapper.forBeanPropertyAccess(any())).thenReturn(mockBeanWrapper);
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(this.dayField), any())).thenReturn(startDate.getDay());
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(this.monthField), any())).thenReturn(startDate.getMonth());
        when(mockBeanWrapperHelper.getIntPropertyValue(eq(this.yearField), any())).thenReturn(startDate.getYear());
    }

    private void thenItSetsTheFieldsWithCorrectValues(final BooleanWithHiddenDateValidator sut) throws Exception {
        assertEquals(ReflectionTestUtils.getField(sut, "missingYesNoAnswerLocal"), this.missingYesNoAnswerLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "missingStartDateLocal"), this.missingStartDateLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "missingEndDateLocal"), this.missingEndDateLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "startDateMustBeAfterMinimumAllowedDate"), this.startDateMustBeAfterMinimumAllowedDate);
        assertEquals(ReflectionTestUtils.getField(sut, "endDateMustBeAfterMinimumAllowedDate"), this.endDateMustBeAfterMinimumAllowedDate);
        assertEquals(ReflectionTestUtils.getField(sut, "startDateIsAfterEndDateLocal"), this.startDateIsAfterEndDateLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "startDateMustBeInThePastLocal"), this.startDateMustBeInThePastLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "endDateMustBeInThePastLocal"), this.endDateMustBeInThePastLocal);
        assertEquals(ReflectionTestUtils.getField(sut, "questionIdentifier"), this.questionIdentifier);
    }

    private void givenBooleanWithHiddenDateConstraints(final BooleanWithHiddenDateValidator.QuestionIdentifier questionIdentifier,
                                                       final LocalDate minimumLowDate) {
        this.missingYesNoAnswerLocal = "answer yes or no";
        this.missingStartDateLocal = "enter start date";
        this.missingEndDateLocal = "enter end date";
        this.startDateMustBeAfterMinimumAllowedDate = "start date must be after minimum set date";
        this.endDateMustBeAfterMinimumAllowedDate = "end date must be after minimum set date";
        this.startDateIsAfterEndDateLocal = "start date must be equal or before end date";
        this.startDateMustBeInThePastLocal = "start date must be in the past";
        this.endDateMustBeInThePastLocal = "end date must be in the past";
        this.questionIdentifier = questionIdentifier;
        // field 'minimumLowDate' is not passed in by the constraint object,
        // it's set dynamically depending on the date question being validated
        this.minimumLowDate = minimumLowDate;
        this.booleanWithHiddenDateConstraint = new BooleanWithHiddenDateConstraint() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return "Error";
            }

            @Override
            public String missingYesNoAnswerLocal() {
                return missingYesNoAnswerLocal;
            }

            @Override
            public String missingStartDateLocal() {
                return missingStartDateLocal;
            }

            @Override
            public String missingEndDateLocal() {
                return missingEndDateLocal;
            }

            @Override
            public String startDateMustBeAfterMinimumAllowedDateLocal() {
                return startDateMustBeAfterMinimumAllowedDate;
            }

            @Override
            public String endDateMustBeAfterMinimumAllowedDateLocal() {
                return endDateMustBeAfterMinimumAllowedDate;
            }

            @Override
            public String startDateIsAfterEndDateLocal() {
                return startDateIsAfterEndDateLocal;
            }

            @Override
            public String startDateMustBeInThePastLocal() {
                return startDateMustBeInThePastLocal;
            }

            @Override
            public String endDateMustBeInThePastLocal() {
                return endDateMustBeInThePastLocal;
            }

            @Override
            public BooleanWithHiddenDateValidator.QuestionIdentifier questionIdentifier() {
                return questionIdentifier;
            }

            @Override
            public String startDateMustBeReal() {
                return startDateMustBeReal;
            }

            @Override
            public String dateCantBeAlpha() { return dateCantBeAlpha; }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };
    }

}
