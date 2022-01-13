package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.AvailableForInterview;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AvailableForInterviewConfirmationControllerTest {

    private static final String IDENTIFIER = "form/availability/available-for-interview";
    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GuardForm mockForm;

    @Mock
    private BooleanQuestion mockQuestion;

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
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private Locale mockLocale;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private AvailableForInterviewConfirmationController sut;

    @Before
    public void setUp() {
        mockLocale = new Locale("en");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(mockLocale);
        sut = new AvailableForInterviewConfirmationController(mockClaimRepository, routingService, mockCookieLocaleResolver);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));

    }

    @Test
    public void getsForm() {
        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        String viewName = sut.getView(mockModel, claimId, mockRequest);
        assertEquals("form/common/boolean", viewName);
    }

    @Test
    public void getsFormInWelsh_modelContainsCorrectWelshYesText() {
        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/pensions/deferred/has-another-pension");
        String viewName = sut.getView(mockModel, claimId, mockRequest);
        assertEquals("form/common/boolean", viewName);
    }

    @Test
    public void SubmitConfirmationFormInWelsh_WithErrors_ReturnsSameConfirmationFormWithCorrectWelshYesText() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/pensions/deferred/has-another-pension");
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result =
                sut.submitAvailabilityConfirmationForm(mockForm, mockBindingResult, claimId, mockRequest, mockResponse, mockModel);

        assertEquals("form/common/boolean", result);
    }

    @Test
    public void SubmitConfirmationForm_WithTrue_ReturnsAvailabilityForm() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(routingService.getNext(any())).thenReturn("/form/availability/availability");
        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        String result =
                sut.submitAvailabilityConfirmationForm(mockForm, mockBindingResult, claimId, mockRequest, mockResponse, mockModel);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
        assertEquals("redirect:/form/availability/availability", result);
    }


    @Test
    public void SubmitConfirmationForm_WithFalse_ReturnsSummaryForm() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/summary");
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        String result =
                sut.submitAvailabilityConfirmationForm(mockForm, mockBindingResult, claimId, mockRequest, mockResponse, mockModel);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
        assertEquals("redirect:/form/summary", result);
    }


    @Test
    public void SubmitConfirmationForm_WithErrors_ReturnsSameConfirmationForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String result =
                sut.submitAvailabilityConfirmationForm(mockForm, mockBindingResult, claimId, mockRequest, mockResponse, mockModel);

        assertEquals("form/common/boolean", result);
    }

    @Test
    public void GetNextPath_WhenAvailabilityFalse_ReturnsSummaryForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/summary");

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/summary", result);
    }


    @Test
    public void testLoadDataShouldAssignDataToForm() {
        final Day day = new Day();
        AvailableForInterview availability = new AvailableForInterview();
        availability.setDaysNotAvailable(Arrays.asList(day));
        final Circumstances circumstances = new Circumstances();
        circumstances.setAvailableForInterview(availability);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final GuardForm form = new GuardForm(new GuardQuestion());
        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignFalseWhenNoAvailability() {
        AvailableForInterview availability = new AvailableForInterview();
        final Circumstances circumstances = new Circumstances();
        circumstances.setAvailableForInterview(availability);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final GuardForm form = new GuardForm(new GuardQuestion());
        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }
}
