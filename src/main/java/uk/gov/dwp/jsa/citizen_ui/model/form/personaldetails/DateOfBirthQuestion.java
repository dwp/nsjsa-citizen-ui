package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Q8 Captures the users response to the date of birth question.
 */
public class DateOfBirthQuestion implements Question {

    /**
     * The day of the month the claimant was born.
     */
    private Integer day;
    /**
     * The month of the year the claimant was born.
     */
    private Integer month;

    /**
     * The year the claimant was born.
     */
    private Integer year;

    private DateofBirthConditionsEnum dateofBirthConditionsEnum;

    public DateOfBirthQuestion() {
    }

    public DateOfBirthQuestion(final LocalDate localDate) {
        Assert.notNull(localDate, "Date should not be null");
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

    public DateofBirthConditionsEnum getDateofBirthConditionsEnum() {
        return dateofBirthConditionsEnum;
    }

    public void setDateofBirthConditionsEnum(final DateofBirthConditionsEnum dateofBirthConditionsEnum) {
        this.dateofBirthConditionsEnum = dateofBirthConditionsEnum;
    }

    @Override
    public boolean isADealBreaker() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DateOfBirthQuestion that = (DateOfBirthQuestion) o;
        return Objects.equals(day, that.day)
                && Objects.equals(month, that.month)
                && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    public String getFormattedValue() {
        return getYear() != null && getMonth() != null && getDay() != null ? DateTimeFormatter
                .ofPattern(Constants.SUMMARY_DATE_FORMAT)
                .format(LocalDate.of(getYear(), getMonth(), getDay())) : "";
    }
}
