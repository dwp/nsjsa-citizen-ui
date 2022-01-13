package uk.gov.dwp.jsa.citizen_ui.validation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.BooleanAndDateFieldQuestions;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.util.DateValidationUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BooleanWithHiddenDateConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DateRangeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

/**
 * This is a shared validation component that validates boolean pages with hidden date fields specifically.
 * Because validation needs to be done on the boolean section first, we are unable to put constraints
 * on the the questions fields directly because we need to perform validations step by step (validate the
 * boolean section, then the date section) in order to avoid a citizen getting invalid errors regarding them
 * not filling in the date fields when they select no to the question. This component take advantage of
 * already existing date validation components and acts as kind of a wrapper class to go through the validation
 * step by step and add the appropriate error messaging at the correct time.
 *
 * To use this component, you first need to add a new enum field into {@link QuestionIdentifier}, then set your
 * dynamic minimum and maximum dates specific to your page inside {@link #setPagesDynamicHighAndLowDateRanges()}.
 * This is because we are unable to pass dynamic dates into java annotations at runtime, so instead this class
 * takes in a {@link QuestionIdentifier} and maps pages dynamic minimum and maximum dates.
 */
@Component
public class BooleanWithHiddenDateValidator implements
        ConstraintValidator<BooleanWithHiddenDateConstraint, BooleanAndDateFieldQuestions>, Validator {

    @Autowired
    private DateValidator dateValidator;

    @Autowired
    private DateRangeValidator dateRangeValidator;

    @Autowired
    private DateValidationUtils dateValidationUtils;

    @Autowired
    private ClaimRepository claimRepository;

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

    private QuestionIdentifier questionIdentifier;
    private LocalDate minimumLowDate;
    private LocalDate maximumHighDate;

    public enum QuestionIdentifier {
        UNABLE_TO_WORK_DUE_TO_ILLNESS,
        TRAVELED_OUTSIDE_UK
    }

    @Autowired
    public BooleanWithHiddenDateValidator(final DateValidator dateValidator,
                                          final DateRangeValidator dateRangeValidator,
                                          final DateValidationUtils dateValidationUtils,
                                          final ClaimRepository claimRepository) {
        this.dateValidator = dateValidator;
        this.dateRangeValidator = dateRangeValidator;
        this.dateValidationUtils = dateValidationUtils;
        this.claimRepository = claimRepository;
    }

    @Override
    public void initialize(final BooleanWithHiddenDateConstraint constraint) {
        this.missingYesNoAnswerLocal = constraint.missingYesNoAnswerLocal();
        this.missingStartDateLocal = constraint.missingStartDateLocal();
        this.missingEndDateLocal = constraint.missingEndDateLocal();
        this.startDateMustBeAfterMinimumAllowedDate = constraint.startDateMustBeAfterMinimumAllowedDateLocal();
        this.endDateMustBeAfterMinimumAllowedDate = constraint.endDateMustBeAfterMinimumAllowedDateLocal();
        this.startDateIsAfterEndDateLocal = constraint.startDateIsAfterEndDateLocal();
        this.startDateMustBeInThePastLocal = constraint.startDateMustBeInThePastLocal();
        this.endDateMustBeInThePastLocal = constraint.endDateMustBeInThePastLocal();
        this.questionIdentifier = constraint.questionIdentifier();
        this.startDateMustBeReal = constraint.startDateMustBeReal();
        this.dateCantBeAlpha = constraint.dateCantBeAlpha();
    }

    private LocalDate setPagesDynamicHighAndLowDateRanges() {
        if (questionIdentifier.equals(QuestionIdentifier.UNABLE_TO_WORK_DUE_TO_ILLNESS) || questionIdentifier.equals(QuestionIdentifier.TRAVELED_OUTSIDE_UK)) {
            ClaimStartDateQuestion question = getCitizensClaimStartQuestion();
            LocalDate yesterday = LocalDate.now().minusDays(1);
            if (question == null) {
                this.minimumLowDate = LocalDate.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
                this.maximumHighDate = LocalDate.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
            } else {
                this.minimumLowDate = LocalDate.of(question.getYear(), question.getMonth(), question.getDay());
                this.maximumHighDate = LocalDate.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
            }
        }
        return null;
    }

    @Override
    public boolean isValid(final BooleanAndDateFieldQuestions question, final ConstraintValidatorContext context) {
        setPagesDynamicHighAndLowDateRanges();
        if (hasNotAnsweredQuestion(question)) {
            return addInvalidMessage(context, missingYesNoAnswerLocal);
        } else if (hasAnsweredNoToQuestion(question)) {
            return true;
        } else if (hasAnsweredYesToQuestion(question)) {
            return hasValidDateFormats(question, context)
                    && hasValidDateRanges(question, context);
        }
        return true;
    }

    private Boolean hasNotAnsweredQuestion(final BooleanAndDateFieldQuestions booleanAndDateFieldQuestions) {
        return booleanAndDateFieldQuestions.getHasProvidedAnswer() == null;
    }

    private Boolean hasAnsweredNoToQuestion(final BooleanAndDateFieldQuestions booleanAndDateFieldQuestions) {
        return booleanAndDateFieldQuestions.getHasProvidedAnswer().equals(Boolean.FALSE);
    }

    private Boolean hasAnsweredYesToQuestion(final BooleanAndDateFieldQuestions booleanAndDateFieldQuestions) {
        return booleanAndDateFieldQuestions.getHasProvidedAnswer();
    }

    private boolean hasValidDateFormats(final BooleanAndDateFieldQuestions question,
                                        final ConstraintValidatorContext context) {
        DateQuestion startDate = question.getDateRangeQuestion().getStartDate();
        DateQuestion endDate = question.getDateRangeQuestion().getEndDate();
        boolean isValidStartDateFormat = isValidDate(context, startDate, true, missingStartDateLocal);
        boolean isValidEndDateFormat = isValidDate(context, endDate, false, missingEndDateLocal);
        return isValidStartDateFormat && isValidEndDateFormat;
    }

    private Boolean isValidDate(final ConstraintValidatorContext context, final DateQuestion date, final boolean isStartDate, final String local) {
        ValidDateConstraint constraintForStartDate = generateInstanceOfValidDateConstraint(isStartDate);
        dateValidator.initialize(constraintForStartDate);
        if (dateValidator.hasZeroOrOneDateFieldsSubmitted(date)) {
            return addInvalidMessage(context, local);
        }
        return dateValidator.isValid(date, context);
    }

    private Boolean hasValidDateRanges(final BooleanAndDateFieldQuestions question,
                                       final ConstraintValidatorContext context) {

        DateQuestion startDate = question.getDateRangeQuestion().getStartDate();
        DateQuestion endDate = question.getDateRangeQuestion().getEndDate();
        boolean hasValidDateRanges = hasValidDateRanges(context, startDate, endDate);
        return hasValidDateRanges;
    }

    private Boolean hasValidDateRanges(final ConstraintValidatorContext context,
                                      final DateQuestion startDate,
                                      final DateQuestion endDate) {

        DateRangeConstraintWrapper startDateConstraints = new DateRangeConstraintWrapper(
                startDateMustBeAfterMinimumAllowedDate,
                startDateMustBeInThePastLocal,
                true, true, this.minimumLowDate.getDayOfMonth(),
                minimumLowDate.getMonthValue(), minimumLowDate.getYear(),
                maximumHighDate.getDayOfMonth(), maximumHighDate.getMonthValue(), maximumHighDate.getYear(),
                new Class[] {ValidationSequence.BusinessValidationGroup.class});
        DateRangeConstraintWrapper endDateConstrains = new DateRangeConstraintWrapper(
                endDateMustBeAfterMinimumAllowedDate,
                endDateMustBeInThePastLocal,
                true, true, minimumLowDate.getDayOfMonth(),
                minimumLowDate.getMonthValue(), minimumLowDate.getYear(),
                maximumHighDate.getDayOfMonth(), maximumHighDate.getMonthValue(), maximumHighDate.getYear(),
                new Class[] {ValidationSequence.BusinessValidationGroup.class});

        dateRangeValidator.initialize(generateDateRangeConstraint(startDateConstraints));
        boolean isValidStartDateRanges = dateRangeValidator.isValid(startDate, context);
        dateRangeValidator.initialize(generateDateRangeConstraint(endDateConstrains));
        boolean isValidEndDateRanges = dateRangeValidator.isValid(endDate, context);

        if (!isValidStartDateRanges || !isValidEndDateRanges) {
            return false;
        }
        return isStartDateBeforeOrEqualToEndDate(startDate, endDate, context);
    }

    private Boolean isStartDateBeforeOrEqualToEndDate(final DateQuestion start, final DateQuestion end,
                                                      final ConstraintValidatorContext context) {
        LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth(), start.getDay());
        LocalDate endDate = LocalDate.of(end.getYear(), end.getMonth(), end.getDay());
        if (startDate.isAfter(endDate)) {
            return addInvalidMessage(context, startDateIsAfterEndDateLocal);
        }

        return true;
    }

    private ClaimStartDateQuestion getCitizensClaimStartQuestion() {
        HttpServletRequest currentHttpRequest = dateValidationUtils.getCurrentHttpRequest();
        String currentCitizensClaimID = getCitizensClaimId(currentHttpRequest);
        Optional<Claim> claim = claimRepository.findById(currentCitizensClaimID);
        if (claim.isPresent()) {
            Optional<ClaimStartDateQuestion> claimStartDateQuestion = claim.get().getClaimStartDate();
            return claimStartDateQuestion.orElse(null);
        }
        return null;
    }

    private String getCitizensClaimId(final HttpServletRequest currentHttpRequest) {
        return Arrays.stream(currentHttpRequest.getCookies())
                .filter(c -> c.getName().equals(COOKIE_CLAIM_ID))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private ValidDateConstraint generateInstanceOfValidDateConstraint(final boolean isStartDate) {
        return new ValidDateConstraint() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                if (isStartDate) {
                    return missingStartDateLocal;
                }
                return missingEndDateLocal;
            }

            @Override
            public DateValidator.DateQuestionIdentify currentDateQuestion() {
                if (isStartDate) {
                    if (questionIdentifier.equals(QuestionIdentifier.UNABLE_TO_WORK_DUE_TO_ILLNESS)) {
                        return DateValidator.DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS;
                    } else {
                        return DateValidator.DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_TRAVELLED;
                    }
                }
                if (questionIdentifier.equals(QuestionIdentifier.TRAVELED_OUTSIDE_UK)) {
                    return DateValidator.DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_TRAVELLED;
                }
                return DateValidator.DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS;
            }

            @Override
            public String incompleteStartDateLocalePrefix() {
                return "date.error.start";
            }

            @Override
            public String incompleteEndDateLocalePrefix() {
                return "date.error.end";
            }

            @Override
            public String nonExistingDateLocalePrefix() {
                return "date.error.unknown";
            }

            @Override
            public String alphasDateLocalePrefix() {
                if (dateCantBeAlpha != null) {
                    return dateCantBeAlpha;
                }
                return "date.error.alpha";
            }

            @Override
            public String nonExistingSingleDateLocale() {
                if (startDateMustBeReal != null) {
                    return startDateMustBeReal;
                }
                return "date.error.single.unknown.date";
            }

            @Override
            public String dayField() {
                return "day";
            }

            @Override
            public String monthField() {
                return "month";
            }

            @Override
            public String yearField() {
                return "year";
            }

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

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON")
    private DateRangeConstraint generateDateRangeConstraint(
            final DateRangeConstraintWrapper constraintWrapper) {
        return new DateRangeConstraint() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return COMMON_FORM_ERROR_SUMMARY_TITLE;
            }

            @Override
            public String lowLimitMessage() {
                return constraintWrapper.lowLimitMessage;
            }

            @Override
            public String highLimitMessage() {
                return constraintWrapper.highLimitMessage;
            }

            @Override
            public Class<?>[] groups() {
                return constraintWrapper.groups;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public boolean low() {
                return constraintWrapper.low;
            }

            @Override
            public boolean high() {
                return constraintWrapper.high;
            }

            @Override
            public int lowDay() {
                return constraintWrapper.lowDay;
            }
            @Override
            public int lowMonth() {
                return constraintWrapper.lowMonth;
            }

            @Override
            public int lowYear() {
                return constraintWrapper.lowYear;
            }

            @Override
            public int highDay() {
                return constraintWrapper.highDay;
            }

            @Override
            public int highMonth() {
                return constraintWrapper.highMonth;
            }

            @Override
            public int highYear() {
                return constraintWrapper.highYear;
            }
        };
    }

    private static class DateRangeConstraintWrapper {
        private String lowLimitMessage;
        private String highLimitMessage;
        private Class<?>[] groups;
        private boolean low;
        private boolean high;
        private int lowDay;
        private int lowMonth;
        private int lowYear;
        private int highDay;
        private int highMonth;
        private int highYear;

        DateRangeConstraintWrapper(final String lowLimitMessage, final String highLimitMessage,
                                   final boolean isLow, final boolean isHigh, final int lowDay,
                                   final int lowMonth, final int lowYear, final int highDay,
                                   final int highMonth, final int highYear,
                                   final Class<?>[] groups) {
            this.lowLimitMessage = lowLimitMessage;
            this.highLimitMessage = highLimitMessage;
            this.low = isLow;
            this.high = isHigh;
            this.lowDay = lowDay;
            this.lowMonth = lowMonth;
            this.lowYear = lowYear;
            this.highDay = highDay;
            this.highMonth = highMonth;
            this.highYear = highYear;
            this.groups = groups;
        }
    }
}
