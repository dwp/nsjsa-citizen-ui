package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;

@RunWith(MockitoJUnitRunner.class)
public class PreviousEmploymentMappingServiceTest {

    @Mock
    private Claim claim;

    private PreviousEmploymentMappingService sut = new PreviousEmploymentMappingService();

    @Test
    public void map() {
        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);

        verify(claim).get(HasPreviousWorkController.IDENTIFIER);
        verify(claim).get(EmployersDatesController.IDENTIFIER, 1);
        verify(claim).get(EmployersNameController.IDENTIFIER, 1);
        verify(claim).get(EmploymentStatusController.IDENTIFIER, 1);
        verify(claim).get(EmployersAddressController.IDENTIFIER, 1);
        verify(claim).get(EmployerWhyJobEndController.IDENTIFIER, 1);
        verify(claim).get(ExpectPaymentController.IDENTIFIER, 1);
        verify(claim).get(AddWorkController.IDENTIFIER);
        verify(claim).count(EmployersDatesController.IDENTIFIER, MAX_JOBS_ALLOWED);

        //2 x Guard and 4 times x 6 questions
        assertEquals(26, questions.size());
    }
}
