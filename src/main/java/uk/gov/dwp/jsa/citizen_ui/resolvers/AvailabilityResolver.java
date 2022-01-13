package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.AvailableForInterview;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AttendInterviewController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;

import static java.util.stream.Collectors.toList;

@Component
public class AvailabilityResolver implements Resolver {

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {

        AvailableForInterview availability = new AvailableForInterview();

        claim.get(AttendInterviewController.IDENTIFIER)
                .ifPresent(question -> fillAnswerToEducation(question, availability));

        circumstances.setAvailableForInterview(availability);

    }

    private void fillAnswerToEducation(final Question question,
                                       final AvailableForInterview availability) {
        if (question instanceof AttendInterviewQuestion) {
            availability.setDaysNotAvailable(((AttendInterviewQuestion) question).getDaysNotToAttend()
                    .stream()
                    .filter(d -> d.getMorning().isSelected() || d.getAfternoon().isSelected())
                    .map(this::getNotAvailableDay).collect(toList()));
        } else {
            throw new UnsupportedOperationException(
                    String.format("Unsupported controller with identifier %s", AttendInterviewController.IDENTIFIER)
            );
        }
    }

    private Day getNotAvailableDay(final uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day d) {
        Day notAvailable = new Day();
        if (d.getMorning().isSelected()) {
            notAvailable.setMorning(d.getMorning().toDto());
        }
        if (d.getAfternoon().isSelected()) {
            notAvailable.setAfternoon(d.getAfternoon().toDto());
        }
        notAvailable.setDate(d.getDate());
        return notAvailable;
    }

}
