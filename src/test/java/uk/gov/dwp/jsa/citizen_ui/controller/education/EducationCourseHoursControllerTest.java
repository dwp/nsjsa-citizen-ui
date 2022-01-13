package uk.gov.dwp.jsa.citizen_ui.controller.education;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Education;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.education.EducationTestHelper.addEducation;

@RunWith(MockitoJUnitRunner.class)
public class EducationCourseHoursControllerTest {
    public static final String IDENTIFIER = "form/education/course-hours";
    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EducationCourseHoursForm mockForm;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private RoutingService routingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private EducationCourseHoursController sut;

    private BigDecimal courseHours = BigDecimal.valueOf(30);

    @Before
    public void setUp() {
        sut = new EducationCourseHoursController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void getEducationConfirmationForm() {
        String viewName = sut.getView(mockModel, claimId, mockRequest);
        assertEquals("form/education/course-hours", viewName);
    }

    @Test
    public void SubmitEducationCourseHoursForm_WithCourseHours_ReturnsEducationCourseDurationForm() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getEducationCourseHoursQuestion().getCourseHours()).thenReturn(courseHours);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(routingService.getNext(any())).thenReturn("/form/education/course-duration");

        String result = sut.submitCourseHours(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("redirect:/form/education/course-duration", result);

        verify(mockClaim).save(any(StepInstance.class), any(Question.class), any(Optional.class));
        verify(mockClaimRepository, atLeastOnce()).save(mockClaim);
    }

    @Test
    public void SubmitEducationCourseHoursForm_WithoutCourseHours_ReturnsEducationCourseHoursForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockForm.getEducationCourseHoursQuestion().getCourseHours()).thenReturn(null);
        String result = sut.submitCourseHours(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("form/education/course-hours", result);
    }

    @Test
    public void SubmitEducationCourseHoursForm_WithErrors_ReturnsEducationCourseHoursForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitCourseHours(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("form/education/course-hours", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        Education education = addEducation(claimDB);
        final Double hours = 5.5;
        education.setHoursPerWeek(hours);

        final EducationCourseHoursForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertEquals("Should match", hours, Double.valueOf(form.getQuestion().getCourseHours().doubleValue()));
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final EducationCourseHoursForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getCourseHours());
    }
}
