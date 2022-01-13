package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.util.DateValidationUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DateValidator implements ConstraintValidator<ValidDateConstraint, Object>, Validator {
    private static final String INCOMPLETE_DAY_DATE_LOCALE_POST_FIX = ".day";
    private static final String INCOMPLETE_MONTH_DATE_LOCALE_POST_FIX = ".month";
    private static final String INCOMPLETE_YEAR_DATE_LOCALE_POST_FIX = ".year";
    private static final String UNKNOWN_START_DATE_LOCALE_POST_FIX = ".start";
    private static final String UNKNOWN_END_DATE_LOCALE_POST_FIX = ".end";
    private static final String ALPHAS_START_DATE_LOCALE_POST_FIX = ".start";
    private static final String ALPHAS_END_DATE_LOCALE_POST_FIX = ".end";

    private PropertyAccessorFactoryWrapper propertyAccessorFactoryWrapper;
    private BeanWrapperHelper beanWrapperHelper;
    private DateValidationUtils dateValidationUtils;

    private Map<String, String> incompleteDateQuestionLocalePrefixMap;
    private Map<String, String> nonExistingDateLocalePrefixMap;
    private Map<String, String> alphasDateLocalePrefixMap;
    private DateQuestionIdentify currentDateQuestion;
    private String incompleteStartDateLocalePrefix;
    private String incompleteEndDateLocalePrefix;
    private String nonExistingDateLocalePrefix;
    private String alphasDateLocalePrefix;
    private String nonExistingSingleDateLocale;
    private String dayField;
    private String monthField;
    private String yearField;

    public enum DateQuestionIdentify {
        DATE_RANGE_START_DATE("dateRange.startDate"), DATE_RANGE_END_DATE("dateRange.endDate"),
        DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate"),
        DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate"),
        DATE_RANGE_WITH_BOOLEAN_START_DATE_TRAVELLED("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate"),
        DATE_RANGE_WITH_BOOLEAN_END_DATE_TRAVELLED("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate"),
        NON_DATE_RANGE_DATE_OF_BIRTH("dateOfBirthQuestion"),
        NON_DATE_RANGE_CLAIM_START_DATE("question");
        private String value;
        DateQuestionIdentify(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public DateValidator(final PropertyAccessorFactoryWrapper propertyAccessorFactoryWrapper,
                         final BeanWrapperHelper beanWrapperHelper,
                         final DateValidationUtils dateValidationUtils) {
        this.propertyAccessorFactoryWrapper = propertyAccessorFactoryWrapper;
        this.beanWrapperHelper = beanWrapperHelper;
        this.dateValidationUtils = dateValidationUtils;
    }

    @Override
    public final void initialize(final ValidDateConstraint constraintAnnotation) {
        Assert.notNull(constraintAnnotation, "constraintAnnotation");
        this.currentDateQuestion = constraintAnnotation.currentDateQuestion();
        this.incompleteStartDateLocalePrefix = constraintAnnotation.incompleteStartDateLocalePrefix();
        this.incompleteEndDateLocalePrefix = constraintAnnotation.incompleteEndDateLocalePrefix();
        this.nonExistingDateLocalePrefix = constraintAnnotation.nonExistingDateLocalePrefix();
        this.alphasDateLocalePrefix = constraintAnnotation.alphasDateLocalePrefix();
        this.nonExistingSingleDateLocale = constraintAnnotation.nonExistingSingleDateLocale();
        this.dayField = constraintAnnotation.dayField();
        this.monthField = constraintAnnotation.monthField();
        this.yearField = constraintAnnotation.yearField();

        this.incompleteDateQuestionLocalePrefixMap = createIncompleteDateQuestionLocalePrefixMap();
        this.nonExistingDateLocalePrefixMap = createNonExistingDateLocalePrefixMap();
        this.alphasDateLocalePrefixMap = createAlphasDateLocalePrefixMap();
    }


    private Map<String, String> createIncompleteDateQuestionLocalePrefixMap() {
        Map<String, String> localeMap = new HashMap<>();
        localeMap.put(DateQuestionIdentify.DATE_RANGE_START_DATE.value, this.incompleteStartDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.DATE_RANGE_END_DATE.value, this.incompleteEndDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.NON_DATE_RANGE_DATE_OF_BIRTH.value, this.incompleteStartDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.NON_DATE_RANGE_CLAIM_START_DATE.value, this.incompleteStartDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.value, this.incompleteStartDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.value, this.incompleteEndDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_TRAVELLED.value, this.incompleteStartDateLocalePrefix);
        localeMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_TRAVELLED.value, this.incompleteEndDateLocalePrefix);
        return localeMap;
    }

    private Map<String, String> createNonExistingDateLocalePrefixMap() {
        Map<String, String> localePrefixMap = new HashMap<>();
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_START_DATE.value,
                this.nonExistingDateLocalePrefix + UNKNOWN_START_DATE_LOCALE_POST_FIX);
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_END_DATE.value,
                this.nonExistingDateLocalePrefix + UNKNOWN_END_DATE_LOCALE_POST_FIX);
        localePrefixMap.put(DateQuestionIdentify.NON_DATE_RANGE_DATE_OF_BIRTH.value,
                this.nonExistingSingleDateLocale);
        localePrefixMap.put(DateQuestionIdentify.NON_DATE_RANGE_CLAIM_START_DATE.value,
                this.nonExistingDateLocalePrefix);
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.value,
                this.nonExistingSingleDateLocale + UNKNOWN_START_DATE_LOCALE_POST_FIX);
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.value,
                this.nonExistingSingleDateLocale + UNKNOWN_END_DATE_LOCALE_POST_FIX);
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_TRAVELLED.value,
                this.nonExistingSingleDateLocale + UNKNOWN_START_DATE_LOCALE_POST_FIX);
        localePrefixMap.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_TRAVELLED.value,
                this.nonExistingSingleDateLocale + UNKNOWN_END_DATE_LOCALE_POST_FIX);
        return localePrefixMap;
    }

    private Map<String, String> createAlphasDateLocalePrefixMap() {
        Map<String, String> map = new HashMap<>();
        String startDateKey = this.alphasDateLocalePrefix + ALPHAS_START_DATE_LOCALE_POST_FIX;
        String endDateKey = this.alphasDateLocalePrefix + ALPHAS_END_DATE_LOCALE_POST_FIX;
        map.put(DateQuestionIdentify.DATE_RANGE_START_DATE.value, startDateKey);
        map.put(DateQuestionIdentify.DATE_RANGE_END_DATE.value, endDateKey);

        map.put(DateQuestionIdentify.NON_DATE_RANGE_DATE_OF_BIRTH.value, startDateKey);
        map.put(DateQuestionIdentify.NON_DATE_RANGE_CLAIM_START_DATE.value, startDateKey);
        map.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_ILLNESS.value, startDateKey);
        map.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_ILLNESS.value, endDateKey);
        map.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_START_DATE_TRAVELLED.value, startDateKey);
        map.put(DateQuestionIdentify.DATE_RANGE_WITH_BOOLEAN_END_DATE_TRAVELLED.value, endDateKey);

        return map;
    }

    @Override
    // Ignore the LocalDate.of warning as we using this method to parse
    @SuppressWarnings("squid:S2201")
    public final boolean isValid(final Object value, final ConstraintValidatorContext context) {
        assertArgumentsAreInValidState(context);

        if (isDateQuestionObjectNull(value)) {
            return false;
        }

        String dataRangeFieldsIdentify = this.currentDateQuestion.value;
        DateValidationUtils.ParsableDateQuestion currentDateQuestion =
                dateValidationUtils.getUsersSubmittedDateQuestionFieldsBeforeParsed(dataRangeFieldsIdentify);

        if (hasZeroOrOneDateFieldsSubmitted(currentDateQuestion, context, dataRangeFieldsIdentify)) {
            return false;
        }

        if (hasOnlyTwoDateFieldsSubmitted(currentDateQuestion, context, getIncompleteDateQuestionLocalePrefix())) {
            return false;
        }

        if (hasDateFieldsWithAlphasDate(currentDateQuestion, context, getAlphasDateLocalePrefix())) {
            return false;
        }

        BeanWrapper beanWrapper = propertyAccessorFactoryWrapper.forBeanPropertyAccess(value);
        Integer day = beanWrapperHelper.getIntPropertyValue(dayField, beanWrapper);
        Integer month = beanWrapperHelper.getIntPropertyValue(monthField, beanWrapper);
        Integer year = beanWrapperHelper.getIntPropertyValue(yearField, beanWrapper);

        if (hasDateFieldsWithNonExistingDate(day, month, year, context, getNonExistingDateLocalePrefix())) {
            return false;
        }

        return true;
    }

    private boolean isDateQuestionObjectNull(final Object value) {
        return value == null;
    }

    private String getIncompleteDateQuestionLocalePrefix() {
        return incompleteDateQuestionLocalePrefixMap.get(this.currentDateQuestion.value);
    }

    private String getNonExistingDateLocalePrefix() {
        return nonExistingDateLocalePrefixMap.get(this.currentDateQuestion.value);
    }

    private String getAlphasDateLocalePrefix() {
        return alphasDateLocalePrefixMap.get(this.currentDateQuestion.value);
    }

    private boolean hasOnlyTwoDateFieldsSubmitted(final DateValidationUtils.ParsableDateQuestion parsableDateQuestion,
                                                  final ConstraintValidatorContext context,
                                                  final String incompleteDatePrefixLocale) {
        final String emptyString = "";
        final int numOfIncompleteFieldsThreshold = 2;
        String incompleteDateLocale = incompleteDatePrefixLocale;

        int numOfIncompleteFields = 0;
        if (parsableDateQuestion.getDay().equals(emptyString)) {
            incompleteDateLocale = incompleteDatePrefixLocale + INCOMPLETE_DAY_DATE_LOCALE_POST_FIX;
            numOfIncompleteFields++;
        }
        if (parsableDateQuestion.getMonth().equals(emptyString)) {
            incompleteDateLocale = incompleteDatePrefixLocale + INCOMPLETE_MONTH_DATE_LOCALE_POST_FIX;
            numOfIncompleteFields++;
        }
        if (parsableDateQuestion.getYear().equals(emptyString)) {
            incompleteDateLocale = incompleteDatePrefixLocale + INCOMPLETE_YEAR_DATE_LOCALE_POST_FIX;
            numOfIncompleteFields++;
        }

        if (numOfIncompleteFields >= numOfIncompleteFieldsThreshold || numOfIncompleteFields == 0) {
            return false;
        }

        addInvalidMessage(context, incompleteDateLocale);
        return true;
    }

    private boolean hasZeroOrOneDateFieldsSubmitted(final DateValidationUtils.ParsableDateQuestion dateQuestion,
                                                    final ConstraintValidatorContext context,
                                                    final String dateField) {
        int numberOfEmptyFields = 0;
        if (dateQuestion.getDay().isEmpty()) {
            numberOfEmptyFields++;
        }
        if (dateQuestion.getMonth().isEmpty()) {
            numberOfEmptyFields++;
        }
        if (dateQuestion.getYear().isEmpty()) {
            numberOfEmptyFields++;
        }

        return numberOfEmptyFields > 1;
    }

    public boolean hasZeroOrOneDateFieldsSubmitted(final DateQuestion dateQuestion) {
        int numberOfEmptyFields = 0;
        if (dateQuestion.getDay() == null) {
            numberOfEmptyFields++;
        }
        if (dateQuestion.getMonth() == null) {
            numberOfEmptyFields++;
        }
        if (dateQuestion.getYear() == null) {
            numberOfEmptyFields++;
        }
        return numberOfEmptyFields > 1;
    }

    private boolean hasDateFieldsWithAlphasDate(final DateValidationUtils.ParsableDateQuestion parsableDateQuestion,
                                                  final ConstraintValidatorContext context,
                                                  final String alphasDateLocalePrefix) {
        try {
            Integer.valueOf(parsableDateQuestion.getDay());
            Integer.valueOf(parsableDateQuestion.getMonth());
            Integer.valueOf(parsableDateQuestion.getYear());
        } catch (Exception e) {
            addInvalidMessage(context, alphasDateLocalePrefix);
            return true;
        }
        return false;
    }

    private boolean hasDateFieldsWithNonExistingDate(final Integer day, final Integer month, final Integer year,
                                                     final ConstraintValidatorContext context,
                                                     final String nonExistingDateLocale) {
        try {
            LocalDate.of(year, month, day);
        } catch (Exception e) {
            addInvalidMessage(context, nonExistingDateLocale);
            return true;
        }
        return false;
    }

    private void assertArgumentsAreInValidState(final ConstraintValidatorContext context) {
        Assert.notNull(context, "context");
        Assert.state(this.dayField != null, "dayField");
        Assert.state(this.monthField != null, "monthField");
        Assert.state(this.yearField != null, "yearField");
    }
}
