package uk.gov.dwp.jsa.citizen_ui.model.form.availability;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;
import uk.gov.dwp.jsa.citizen_ui.util.date.SimpleI8NDateFormat;

import javax.validation.Valid;

public class AttendInterviewForm extends AbstractForm<AttendInterviewQuestion> {

    private SimpleI8NDateFormat dateFormat;

    /**
     * Q88 Why you can't attend the job centre interview.
     */
    @Valid
    private AttendInterviewQuestion attendInterviewQuestion;

    public AttendInterviewForm() {
    }

    public AttendInterviewForm(@Valid final AttendInterviewQuestion attendInterviewQuestion,
                               final SimpleI8NDateFormat dateFormat) {
        this.attendInterviewQuestion = attendInterviewQuestion;
        this.dateFormat = dateFormat;
    }

    public SimpleI8NDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(final SimpleI8NDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public AttendInterviewQuestion getAttendInterviewQuestion() {
        if (attendInterviewQuestion == null) {
            attendInterviewQuestion = new AttendInterviewQuestion();
        }
        return attendInterviewQuestion;
    }

    public void setAttendInterviewQuestion(final AttendInterviewQuestion attendInterviewQuestion) {
        this.attendInterviewQuestion = attendInterviewQuestion;
    }

    @Override
    public AttendInterviewQuestion getQuestion() {
        return getAttendInterviewQuestion();
    }

    @Override
    public void setQuestion(final AttendInterviewQuestion question) {
        this.attendInterviewQuestion = question;
    }
}
