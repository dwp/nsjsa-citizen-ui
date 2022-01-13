package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AttendInterviewController;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AvailableForInterviewConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class AvailabilityMappingService implements MappingService {
    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(ViewQuestion.guardQuestion(AvailableForInterviewConfirmationController.IDENTIFIER, claim));
        questions.add(singleQuestion(AttendInterviewController.IDENTIFIER, claim));
    }
}
