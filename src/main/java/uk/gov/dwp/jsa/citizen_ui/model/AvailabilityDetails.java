package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

public class AvailabilityDetails {

    private BooleanQuestion availableForInterviewConfirmationQuestion;
    private AttendInterviewQuestion attendInterviewQuestion;

    public AvailabilityDetails() {
    }

    public AvailabilityDetails(
            final BooleanQuestion availableForInterviewConfirmationQuestion,
            final AttendInterviewQuestion attendInterviewQuestion) {
        this.availableForInterviewConfirmationQuestion = availableForInterviewConfirmationQuestion;
        this.attendInterviewQuestion = attendInterviewQuestion;
    }

    public BooleanQuestion getAvailableForInterviewConfirmationQuestion() {
        if (availableForInterviewConfirmationQuestion == null) {
            this.availableForInterviewConfirmationQuestion =
                    new BooleanQuestion();
        }
        return availableForInterviewConfirmationQuestion;
    }

    public void setAvailableForInterviewConfirmationQuestion(
            final BooleanQuestion availableForInterviewConfirmationQuestion) {
        this.availableForInterviewConfirmationQuestion = availableForInterviewConfirmationQuestion;
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
}
