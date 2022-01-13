package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AvailabilityDetailsTest {

    public static final BooleanQuestion AVAILABLE_FOR_INTERVIEW_CONFIRMATION_QUESTION = new BooleanQuestion();
    public static final AttendInterviewQuestion ATTEND_INTERVIEW_QUESTION = new AttendInterviewQuestion();

    @Test
    public void hasDefaultValues() {
        final AvailabilityDetails availabilityDetails = new AvailabilityDetails();
        assertThat(availabilityDetails.getAvailableForInterviewConfirmationQuestion(), is(AVAILABLE_FOR_INTERVIEW_CONFIRMATION_QUESTION));
        assertThat(availabilityDetails.getAttendInterviewQuestion(), is(ATTEND_INTERVIEW_QUESTION));
    }

    @Test
    public void setAvailableForInterviewConfirmationQuestion() {
        final AvailabilityDetails availabilityDetails = new AvailabilityDetails();
        availabilityDetails.setAvailableForInterviewConfirmationQuestion(AVAILABLE_FOR_INTERVIEW_CONFIRMATION_QUESTION);
        assertThat(availabilityDetails.getAvailableForInterviewConfirmationQuestion(), is(AVAILABLE_FOR_INTERVIEW_CONFIRMATION_QUESTION));
    }

    @Test
    public void setAttendInterviewQuestion() {
        final AvailabilityDetails availabilityDetails = new AvailabilityDetails();
        availabilityDetails.setAttendInterviewQuestion(ATTEND_INTERVIEW_QUESTION);
        assertThat(availabilityDetails.getAttendInterviewQuestion(), is(ATTEND_INTERVIEW_QUESTION));
    }
}
