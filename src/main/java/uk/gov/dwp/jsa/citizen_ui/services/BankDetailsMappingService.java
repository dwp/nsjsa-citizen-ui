package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.bankDetailsQuestion;

@Service
public class BankDetailsMappingService implements MappingService {
    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(bankDetailsQuestion(BankAccountFormController.IDENTIFIER, claim));
    }
}
