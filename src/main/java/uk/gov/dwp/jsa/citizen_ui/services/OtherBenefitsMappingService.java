package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitDetailsController;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitsController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class OtherBenefitsMappingService implements MappingService {
    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(ViewQuestion.guardQuestion(OtherBenefitsController.IDENTIFIER, claim));
        questions.add(singleQuestion(OtherBenefitDetailsController.IDENTIFIER, claim));
    }
}
