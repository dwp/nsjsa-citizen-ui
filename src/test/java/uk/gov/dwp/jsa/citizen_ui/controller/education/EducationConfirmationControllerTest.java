package uk.gov.dwp.jsa.citizen_ui.controller.education;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.education.EducationTestHelper.addEducation;

@RunWith(MockitoJUnitRunner.class)
public class EducationConfirmationControllerTest {
    public static final String IDENTIFIER = "form/education/have-you-been";
    @Mock private Model mockModel;

    @Mock private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GuardForm mockForm;

    @Mock private BindingResult mockBindingResult;

    @Mock private HttpServletResponse mockResponse;
    @Mock
    private RoutingService routingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private EducationConfirmationController sut;

    @Before
    public void setUp() {
        sut = new EducationConfirmationController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void getEducationConfirmationForm() {
        String viewName = sut.getView(mockModel, claimId, mockRequest);
        assertEquals("form/common/boolean", viewName);
    }

    @Test
    public void SubmitEducationConfirmationForm_WithTrue_ReturnsEducationCourseNameForm() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/form/education/course-name");

        String result = sut.submitEducationConfirmationForm(mockForm, mockBindingResult, claimId, mockResponse, mockRequest, mockModel);

        assertEquals("redirect:/form/education/course-name", result);
    }

    @Test
    public void SubmitEducationConfirmationForm_WithErrors_ReturnsEducationConfirmationForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitEducationConfirmationForm(mockForm, mockBindingResult, claimId, mockResponse, mockRequest, mockModel);

        assertEquals("form/common/boolean", result);
    }

    @Test
    public void GetNextPath_WhenHaveYouBeenFalse_ReturnsEducationStartDateForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/form/education/course-name");

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/education/course-name", result);
    }

    @Test
    public void GetNextPath_WhenHaveYouBeenFalse_thenReturnAvailability() {
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/availability/available-for-interview");

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/availability/available-for-interview", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        addEducation(claimDB);

        final GuardForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final GuardForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }
}
