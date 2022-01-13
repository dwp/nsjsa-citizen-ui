package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm.TEXT_INPUT_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class EmailControllerTest {

    public static final String IDENTIFIER = "form/personal-details/contact/email";
    @Mock
    private Model mockModel;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private EmailForm mockEmailForm;
    @Mock
    private EmailStringQuestion mockEmailQuestion;
    @Mock
    private EmailSanitiser emailSanitiser;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private EmailController sut;

    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new EmailController(mockClaimRepository, routingService, emailSanitiser);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void getView_returnsView() {
        String result = sut.getEmail(mockModel, claimId, mockRequest);
        assertEquals(EmailController.EMAIL_INPUT_VIEW_NAME, result);
    }

    @Test
    public void createNewForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.of(mockEmailQuestion));
        EmailForm form = sut.createNewForm(mockClaim);
        assertNotNull(form);
        assertEquals(mockEmailQuestion, form.getQuestion());
    }

    @Test
    public void submitEmailRedirectsToBankDetails() {
        when(mockEmailForm.getQuestion()).thenReturn(mockEmailQuestion);
        when(mockEmailForm.getQuestion()).thenReturn(mockEmailQuestion);
        when(mockEmailQuestion.getEmail()).thenReturn("valid@email.com");
        when(mockBindingResult.hasErrors()).thenReturn(false);

        String result = sut.submitEmail(
                "mockClaimId",
                mockEmailForm,
                mockBindingResult,
                mockResponse,
                mockModel);
    }

    @Test
    public void updateClaim() {
        when(mockEmailForm.getQuestion()).thenReturn(mockEmailQuestion);

        sut.updateClaim(mockEmailForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim, times(1))
                .save(any(StepInstance.class), eq(mockEmailQuestion), any(Optional.class));
    }
}
