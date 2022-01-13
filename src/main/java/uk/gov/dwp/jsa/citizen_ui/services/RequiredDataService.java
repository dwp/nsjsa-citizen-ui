package uk.gov.dwp.jsa.citizen_ui.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasCurrentWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitsController;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.NinoController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.DateOfBirthFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.PersonalDetailsFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutPostalController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.ClaimantPhoneController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.EmailController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;

import java.util.stream.Collectors;

@Service
public class RequiredDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequiredDataService.class);

    public String getQuestionUrl(final Claim claim) {
        String[] toValidate = new String[]{
                ClaimStartDateController.IDENTIFIER,
                NinoController.IDENTIFIER,
                DateOfBirthFormController.IDENTIFIER,
                PersonalDetailsFormController.IDENTIFIER,
                AboutAddressController.IDENTIFIER,
                AboutPostalController.IDENTIFIER,
                ClaimantPhoneController.IDENTIFIER,
                EmailController.IDENTIFIER,
                OtherBenefitsController.IDENTIFIER,
                JuryServiceConfirmationController.IDENTIFIER,
                HasCurrentWorkController.IDENTIFIER,
                HasPreviousWorkController.IDENTIFIER,
                HasOutsideWorkController.IDENTIFIER,
                HasCurrentPensionController.IDENTIFIER,
                EducationConfirmationController.IDENTIFIER
        };

        for (String controller : toValidate) {
            if (!claim.get(controller).isPresent()) {
                LOGGER.warn(String.format("{\"error-message\":\"%s\", \"claim-id\":\"%s\", \"claim-answers\":\"%s\"}",
                        String.format("Missing %s controller", controller),
                        claim.getId(),
                        claim.getAnswers().values().stream()
                                .map(q -> org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString(q))
                                .map(answer -> answer.replace("uk.gov.dwp.jsa.citizen_ui.model.", ""))
                                .collect(Collectors.toList())));
                return "error/500";
            }
        }

        return null;
    }

}
