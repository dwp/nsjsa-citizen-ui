package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.AddWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployerWhyJobEndController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersDatesController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmploymentStatusController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.ExpectPaymentController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode.SECTION;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.guardLoopQuestion;

@Service
public class PreviousEmploymentMappingService implements MappingService {

    public static final List<String> PREVIOUS_EMPLOYMENT_LOOP_IDENTIFIERS =
            Collections.unmodifiableList(Arrays.asList(EmployersDatesController.IDENTIFIER,
                    EmployersNameController.IDENTIFIER, EmploymentStatusController.IDENTIFIER,
                    EmployersAddressController.IDENTIFIER, EmployerWhyJobEndController.IDENTIFIER,
                    ExpectPaymentController.IDENTIFIER));

    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(guardLoopQuestion(HasPreviousWorkController.IDENTIFIER, claim,
                claim.count(EmployersDatesController.IDENTIFIER, MAX_JOBS_ALLOWED)));
        questions.add(guardLoopQuestion(AddWorkController.IDENTIFIER, claim, MAX_JOBS_ALLOWED));

        PREVIOUS_EMPLOYMENT_LOOP_IDENTIFIERS
                .forEach(identifier -> addLoopQuestion(claim, questions, identifier, MAX_JOBS_ALLOWED,
                        SECTION));

    }
}
