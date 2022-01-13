package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.*;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PensionCurrentMappingServiceTest {

    @Mock
    private Claim claim;

    private PensionCurrentMappingService sut = new PensionCurrentMappingService();

    @Test
    public void map() {

        final int EXPECTED_QUESTION_COUNT = 46;

        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);

        verify(claim).get(HasCurrentPensionController.IDENTIFIER);
        verify(claim).get(ProviderNameController.IDENTIFIER, 1);
        verify(claim).get(ProviderAddressController.IDENTIFIER, 1);
        verify(claim).get(PensionPaymentFrequencyController.IDENTIFIER, 1);
        verify(claim).get(PensionIncreaseController.IDENTIFIER, 1);
        verify(claim).get(PensionIncreaseDateController.IDENTIFIER, 1);

        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}
