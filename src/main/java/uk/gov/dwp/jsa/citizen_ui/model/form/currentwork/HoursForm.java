package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;

import javax.validation.Valid;

public class HoursForm extends AbstractCounterForm<HoursQuestion> {

    public static final int MAX_WORK_HOURS = 99;

    /**
     * Q35  Average hours worked a week.
     */
    @Valid
    private HoursQuestion hoursQuestion;

    public HoursForm() {
    }

    public HoursForm(final HoursQuestion hoursQuestion) {
        this.hoursQuestion = hoursQuestion;
    }

    public HoursQuestion getHoursQuestion() {
        if (hoursQuestion == null) {
            hoursQuestion = new HoursQuestion();
        }
        return hoursQuestion;
    }

    @Override
    public HoursQuestion getQuestion() {
        return getHoursQuestion();
    }

    @Override
    public void setQuestion(final HoursQuestion question) {
        setHoursQuestion(question);
    }

    public void setHoursQuestion(final HoursQuestion hoursQuestion) {
        this.hoursQuestion = hoursQuestion;
    }
}
