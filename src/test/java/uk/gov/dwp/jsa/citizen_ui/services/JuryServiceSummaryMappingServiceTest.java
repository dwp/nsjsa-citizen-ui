package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@RunWith(MockitoJUnitRunner.class)
public class JuryServiceSummaryMappingServiceTest {
    @Mock
    private Claim claim;

    private JuryServiceSummaryMappingService sut = new JuryServiceSummaryMappingService();

    @Test
    public void map() {
        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);
        verify(claim).get(JuryServiceConfirmationController.IDENTIFIER);
        verify(claim).get(JuryServiceDatesController.IDENTIFIER);

        assertEquals(2, questions.size());
    }
}
