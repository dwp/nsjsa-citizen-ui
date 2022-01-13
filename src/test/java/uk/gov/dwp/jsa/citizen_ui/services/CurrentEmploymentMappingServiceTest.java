package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;

@RunWith(MockitoJUnitRunner.class)
public class CurrentEmploymentMappingServiceTest {

    @Mock
    private Claim claim;

    private CurrentEmploymentMappingService sut = new CurrentEmploymentMappingService();

    @Test
    public void map() {
        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);

        verify(claim).get(HasCurrentWorkController.IDENTIFIER);
        verify(claim).get(HasAnotherCurrentJobController.IDENTIFIER);
        verify(claim).get(WorkPaidOrVoluntaryController.IDENTIFIER, 1);
        verify(claim).get(EmployersNameController.IDENTIFIER, 1);
        verify(claim).get(VoluntaryPaidController.IDENTIFIER, 1);
        verify(claim).get(ChoosePaymentController.IDENTIFIER, 1);
        verify(claim).get(PaymentFrequencyController.IDENTIFIER, 1);
        verify(claim).get(HoursController.IDENTIFIER, 1);
        verify(claim).get(SelfEmployedConfirmationController.IDENTIFIER, 1);
        verify(claim).get(CurrentWorkAddressController.IDENTIFIER, 1);
        verify(claim).count(EmployersNameController.IDENTIFIER, MAX_JOBS_ALLOWED);

        // 4x 8question + 2x  guard
        assertEquals(34, questions.size());
    }



}
