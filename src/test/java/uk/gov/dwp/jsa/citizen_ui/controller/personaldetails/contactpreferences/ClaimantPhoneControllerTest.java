package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PhoneSanitiser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClaimantPhoneControllerTest {

    public static final String IDENTIFIER = "form/personal-details/contact/telephone";
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private PhoneForm mockContactPhoneForm;
    @Mock
    private PhoneQuestion mockContactPhoneQuestion;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private RoutingService routingService;
    @Mock
    private PhoneSanitiser mockPhoneSanitiser;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    private ClaimantPhoneController sut;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new ClaimantPhoneController(mockClaimRepository, routingService, mockPhoneSanitiser);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void createNewForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.of(mockContactPhoneQuestion));
        PhoneForm form = sut.createNewForm(mockClaim);
        assertNotNull(form);
        assertEquals(mockContactPhoneQuestion, form.getQuestion());
    }

    @Test
    public void updateClaim() {
        when(mockContactPhoneForm.getQuestion()).thenReturn(mockContactPhoneQuestion);

        sut.updateClaim(mockContactPhoneForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim, times(1))
                .save(any(StepInstance.class), eq(mockContactPhoneQuestion), any(Optional.class));
    }

    @Test
    public void getPhoneReturnsContactPhoneForm() {
        String result = sut.getPhoneView(mockModel, "claimId", mockRequest);
        assertEquals("form/personal-details/phone-number", result);
    }

    @Test
    public void submitPhoneRedirectsToEmailConfirmation() {
        givenRoutingServiceReturnsEmailConfirmation();
        when(mockContactPhoneForm.getQuestion()).thenReturn(mockContactPhoneQuestion);

        String result = sut.submitPhoneForm(
                "mockClaimId",
                mockContactPhoneForm,
                mockBindingResult,
                mockResponse,
                mockModel);

        assertEquals("redirect:/form/personal-details/contact/email-confirmation", result);
    }

    @Test
    public void getLastGuard() {
        Optional<StepInstance> optionalStepInstance = sut.getLastGuard(mockContactPhoneForm, mockClaim, null);
        assertEquals(optionalStepInstance.isPresent(), false);
    }

    private void givenRoutingServiceReturnsEmailConfirmation() {
        when(routingService.getNext(any())).thenReturn("/form/personal-details/contact/email-confirmation");
    }
}
