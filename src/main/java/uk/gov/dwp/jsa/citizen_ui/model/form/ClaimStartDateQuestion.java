package uk.gov.dwp.jsa.citizen_ui.model.form;

import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.ClaimDateConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

import java.time.LocalDate;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SDT;

/**
 * Q3 Captures the users response to the claim start question.
 */
@ValidDateConstraint(message = "claimstart.error.blank",
        incompleteStartDateLocalePrefix = "claimstart.error.start",
        nonExistingDateLocalePrefix = "claimstart.error.single.unknown.date",
        currentDateQuestion = DateValidator.DateQuestionIdentify.NON_DATE_RANGE_CLAIM_START_DATE,
        alphasDateLocalePrefix = "claimstart.error.single.unknown.date")
@ClaimDateConstraint
public class ClaimStartDateQuestion implements Question {

    /**
     * The day of the month the claimant would like the claim to start.
     */
    private Integer day;

    /**
     * The month of the year the claimant would like the claim to start.
     */
    private Integer month;

    /**
     * The year the claimant would like the claim to start.
     */
    private Integer year;

    private String validationError;

    public ClaimStartDateQuestion() {
    }

    public ClaimStartDateQuestion(final LocalDate localDate) {
        this.day = localDate.getDayOfMonth();
        this.month = localDate.getMonthValue();
        this.year = localDate.getYear();
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(final Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(final Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    public String getValidationError() {
        return validationError;
    }

    public void setValidationError(final String validationError) {
        this.validationError = validationError;
    }

    public String getFormattedDate() {
        return LocalDate.of(year, month, day).format(SDT);
    }
}
