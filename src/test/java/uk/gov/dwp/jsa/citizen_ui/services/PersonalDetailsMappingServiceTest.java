package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.*;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.*;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.ClaimantPhoneController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.EmailController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@RunWith(MockitoJUnitRunner.class)
public class PersonalDetailsMappingServiceTest {

    @Mock
    private Claim claim;

    private PersonalDetailsMappingService sut = new PersonalDetailsMappingService();

    @Test
    public void map() {
        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);

        verify(claim).get(NinoController.IDENTIFIER);
        verify(claim).get(DateOfBirthFormController.IDENTIFIER);
        verify(claim).get(ClaimantPhoneController.IDENTIFIER);
        verify(claim).get(EmailController.IDENTIFIER);
        verify(claim).get(AboutAddressController.IDENTIFIER);
        verify(claim).get(AboutPostalAddressController.IDENTIFIER);
        verify(claim).get(AboutPostalController.IDENTIFIER);
        verify(claim).get(PersonalDetailsFormController.IDENTIFIER);
        verify(claim).get(LanguagePreferenceController.IDENTIFIER);

        verify(claim).get(WereYouAvailableForWorkController.IDENTIFIER);
        verify(claim).get(WereYouSearchingForWorkController.IDENTIFIER);
        verify(claim).get(WhyNotApplySoonerController.IDENTIFIER);
        verify(claim).get(FullTimeEducationController.IDENTIFIER);
        verify(claim).get(HaveYouBeenInPaidWorkSinceController.IDENTIFIER);
        verify(claim).get(HaveYouAskedForAdviceController.IDENTIFIER);
        verify(claim).get(HaveYouBeenUnableToWorkDueToIllnessController.IDENTIFIER);
        verify(claim).get(HaveYouTravelledOutsideController.IDENTIFIER);

        //Guard and 4 x 6 questions
        assertEquals(17, questions.size());
    }
}
