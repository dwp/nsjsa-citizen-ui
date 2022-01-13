package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseDurationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseHoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationPlaceController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EducationSummaryMappingServiceTest {
    @Mock
    private Claim claim;

    private EducationSummaryMappingService sut = new EducationSummaryMappingService();

    @Test
    public void map() {

        ArrayList<ViewQuestion> questions = new ArrayList<>();
        sut.map(claim, questions);
        verify(claim).get(EducationConfirmationController.IDENTIFIER);
        verify(claim).get(EducationCourseNameController.IDENTIFIER);
        verify(claim).get(EducationPlaceController.IDENTIFIER);
        verify(claim).get(EducationCourseHoursController.IDENTIFIER);
        verify(claim).get(EducationCourseDurationController.IDENTIFIER);

        assertEquals(5, questions.size());

    }
}
