package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursForm.MAX_WORK_HOURS;

/**
 * Q35  Average hours worked a week question.
 */
public class HoursQuestion implements Question {

    @NotNull(message = "currentwork.hours.length.invalid")
    @Max(value = MAX_WORK_HOURS, message = "currentwork.hours.invalid")
    @Positive(message = "currentwork.hours.invalid.zero.hours")
    private Integer hours;

    public HoursQuestion() {
    }

    public HoursQuestion(final Integer hours) {
        this.hours = hours;
    }

    public Integer getHours() {
        return hours;
    }

    public Integer getValue() {
        return hours;
    }

    public void setHours(final Integer hours) {
        this.hours = hours;
    }
}
