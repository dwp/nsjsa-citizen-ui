package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouAvailableForWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouSearchingForWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WhyNotApplySoonerController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.FullTimeEducationController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouBeenInPaidWorkSinceController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouAskedForAdviceController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouBeenUnableToWorkDueToIllnessController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouTravelledOutsideController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutPostalAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutPostalController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.DateOfBirthFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.LanguagePreferenceController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.NinoController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.PersonalDetailsFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.ClaimantPhoneController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.EmailController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class PersonalDetailsMappingService implements MappingService {
    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(singleQuestion(NinoController.IDENTIFIER, claim));
        questions.add(singleQuestion(DateOfBirthFormController.IDENTIFIER, claim));
        questions.add(singleQuestion(ClaimantPhoneController.IDENTIFIER, claim));
        questions.add(singleQuestion(EmailController.IDENTIFIER, claim));
        questions.add(singleQuestion(AboutAddressController.IDENTIFIER, claim, EditMode.SECTION));
        questions.add(singleQuestion(LanguagePreferenceController.IDENTIFIER, claim));
        questions.add(singleQuestion(AboutPostalAddressController.IDENTIFIER, claim));
        questions.add(singleQuestion(AboutPostalController.IDENTIFIER, claim));
        questions.add(singleQuestion(PersonalDetailsFormController.IDENTIFIER, claim));

        questions.add(singleQuestion(WereYouAvailableForWorkController.IDENTIFIER, claim));
        questions.add(singleQuestion(WereYouSearchingForWorkController.IDENTIFIER, claim));
        questions.add(singleQuestion(WhyNotApplySoonerController.IDENTIFIER, claim));
        questions.add(singleQuestion(FullTimeEducationController.IDENTIFIER, claim));
        questions.add(singleQuestion(HaveYouBeenInPaidWorkSinceController.IDENTIFIER, claim));
        questions.add(singleQuestion(HaveYouAskedForAdviceController.IDENTIFIER, claim));
        questions.add(singleQuestion(HaveYouBeenUnableToWorkDueToIllnessController.IDENTIFIER, claim));
        questions.add(singleQuestion(HaveYouTravelledOutsideController.IDENTIFIER, claim));
    }
}
