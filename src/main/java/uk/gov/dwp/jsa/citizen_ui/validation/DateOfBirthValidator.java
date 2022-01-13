package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.DateofBirthConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static org.springframework.util.Assert.notNull;

public class DateOfBirthValidator
        implements ConstraintValidator<DateofBirthConstraint, DateOfBirthQuestion>, Validator {

    public static final int SIXTEEN = 16;
    public static final int EIGHTEEN = 18;
    public static final int ONE = 1;
    public static final int MAX_AGE = 100;

    @Override
    public boolean isValid(final DateOfBirthQuestion dateOfBirth, final ConstraintValidatorContext context) {
        notNull(dateOfBirth, "dateOfBirthQuestion");

        Integer day = dateOfBirth.getDay();
        Integer month = dateOfBirth.getMonth();
        Integer year = dateOfBirth.getYear();
        if (String.valueOf(year).length() == 2 || String.valueOf(year).length() == 1) {
            Integer parsedYear = parseTwoDigitYear(year);
            year = parsedYear;
        }

        LocalDate date = LocalDate.of(year, month, day);

        LocalDate localDateBefore16Years = LocalDate.now().minusYears(SIXTEEN);
        LocalDate localDateBefore18Years = LocalDate.now().minusYears(EIGHTEEN).plusDays(ONE);
        if (date.isAfter(LocalDate.now())) {
          return addInvalidMessage(context, "dateofbirth.error.future");
        }  else if (date.isAfter(localDateBefore16Years)) {
            dateOfBirth.setDateofBirthConditionsEnum(DateofBirthConditionsEnum.LESS_THAN_16);
            return addInvalidMessage(context, "dateofbirth.error.under16.text");
        } else if (date.isBefore(localDateBefore18Years)) {
            dateOfBirth.setDateofBirthConditionsEnum(DateofBirthConditionsEnum.GREATER_THAN_18);
            return true;
        } else {
            dateOfBirth.setDateofBirthConditionsEnum(DateofBirthConditionsEnum.BETWEEN_16_17);
            return true;
        }
    }

    private Integer parseTwoDigitYear(final Integer dateToParse) {
        String result = dateToParse.toString();
        if (result.length() == 1) {
            result =  String.format("%02d", dateToParse);
        }
        DateTimeFormatter twoYearFormat = new DateTimeFormatterBuilder()
                .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 2,  LocalDate.now().minusYears(MAX_AGE))
                .toFormatter();
        Year parsedYear = twoYearFormat.parse(result, Year::from);
        return parsedYear.getValue();
    }
}
