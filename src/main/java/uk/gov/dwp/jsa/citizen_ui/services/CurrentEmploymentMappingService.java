package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasAnotherCurrentJobController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasCurrentWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.ChoosePaymentController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.CurrentWorkAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.HoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.SelfEmployedConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.VoluntaryPaidController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode.SECTION;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.guardLoopQuestion;

@Service
public class CurrentEmploymentMappingService implements MappingService {

    public static final List<String> CURRENT_EMPLOYMENT_LOOP_IDENTIFIERS =
            Collections.unmodifiableList(Arrays.asList(
                    EmployersNameController.IDENTIFIER,
                    ChoosePaymentController.IDENTIFIER, PaymentFrequencyController.IDENTIFIER,
                    HoursController.IDENTIFIER,
                    CurrentWorkAddressController.IDENTIFIER, SelfEmployedConfirmationController.IDENTIFIER,
                    VoluntaryPaidController.IDENTIFIER,
                    WorkPaidOrVoluntaryController.IDENTIFIER
                    ));

    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {

        questions.add(guardLoopQuestion(
                HasCurrentWorkController.IDENTIFIER, claim,
                claim.count(EmployersNameController.IDENTIFIER,
                            MAX_JOBS_ALLOWED)));
        questions.add(guardLoopQuestion(
                HasAnotherCurrentJobController.IDENTIFIER, claim,
                MAX_JOBS_ALLOWED));

        CURRENT_EMPLOYMENT_LOOP_IDENTIFIERS
                .forEach(identifier -> addLoopQuestion(claim, questions, identifier, MAX_JOBS_ALLOWED,
                        SECTION));


    }
}
