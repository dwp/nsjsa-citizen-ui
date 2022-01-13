package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitDetailsController;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitsController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OtherBenefitsMappingServiceTest {

    @Mock
    private Claim claim;

    private OtherBenefitsMappingService sut = new OtherBenefitsMappingService();

    @Test
    public void map() {
        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);

        verify(claim).get(OtherBenefitsController.IDENTIFIER);
        verify(claim).get(OtherBenefitDetailsController.IDENTIFIER);

        assertEquals(2, questions.size());
    }
}
