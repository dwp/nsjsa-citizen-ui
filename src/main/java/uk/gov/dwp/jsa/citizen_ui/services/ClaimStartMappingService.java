package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SLASH;
import static uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimWarningDateController.IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class ClaimStartMappingService implements MappingService {


    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        ViewQuestion question = singleQuestion(ClaimStartDateController.IDENTIFIER, claim);
        question.setUrl(SLASH + IDENTIFIER);
        questions.add(question);
    }
}
