package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HasOutsideWorkMappingServiceTest {
    @Mock
    private Claim claim;

    private HasOutsideWorkMappingService sut = new HasOutsideWorkMappingService();

    @Test
    public void map() {

        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);
        verify(claim).get(HasOutsideWorkController.IDENTIFIER);

        assertEquals(1, questions.size());

    }
}
