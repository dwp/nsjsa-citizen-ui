package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionIncreaseController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionIncreaseDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionPaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderNameController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode.SECTION;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.guardLoopQuestion;

@Service
public class PensionCurrentMappingService implements MappingService {

    public static final List<String> PENSION_CURRENT_LOOP_IDENTIFIERS =
            Collections.unmodifiableList(Arrays.asList(
                    ProviderNameController.IDENTIFIER,
                    ProviderAddressController.IDENTIFIER,
                    PensionPaymentFrequencyController.IDENTIFIER,

                    PensionIncreaseController.IDENTIFIER,
                    PensionIncreaseDateController.IDENTIFIER));

    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(
            guardLoopQuestion(HasCurrentPensionController.IDENTIFIER, claim,
                claim.count(ProviderNameController.IDENTIFIER, MAX_PENSIONS_ALLOWED)));
        PENSION_CURRENT_LOOP_IDENTIFIERS
                .forEach(identifier -> addLoopQuestion(claim, questions, identifier, MAX_PENSIONS_ALLOWED, SECTION));

    }
}
