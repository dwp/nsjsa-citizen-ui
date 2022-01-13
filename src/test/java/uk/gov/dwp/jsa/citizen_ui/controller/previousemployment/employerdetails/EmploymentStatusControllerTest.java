package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.junit.Assert;
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
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmploymentStatusControllerTest {
    public static final String IDENTIFIER = "form/previous-employment/employer-details/status";
    private static final String IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE = "alternativeWelshText";
    @Mock private Model mockModel;
    @Mock private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BooleanForm mockForm;
    @Mock
    BooleanQuestion mockQuestion;
    @Mock private BindingResult mockBindingResult;
    @Mock private HttpServletResponse mockResponse;
    @Mock private RoutingService routingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private EmploymentStatusController sut;

    @Before
    public void setUp() {
        when(mockForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");

        sut = new EmploymentStatusController(mockClaimRepository, routingService, mockCookieLocaleResolver);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewForm_throwsException() {
        sut.createNewForm(mockClaim);
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        BooleanForm actual = sut.createNewForm(mockClaim,1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        BooleanForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }


    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void GetEmploymentStatusForm_ReturnsEmploymentStatusForm() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        String result = sut.getView(2, mockModel, "claimId", mockRequest);
        assertEquals("form/common/boolean", result);
    }

    @Test
    public void SubmitEmploymentStatusForm_WithTrue_ReturnsAddWorkForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/%s/add-work");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));

        String result = sut.submitStatus(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        assertEquals("redirect:/form/previous-employment/1/add-work", result);
    }

    @Test
    public void SubmitEmploymentStatusForm_WithFalse_ReturnsAddWorkForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/%s/add-work");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));

        String result = sut.submitStatus(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        assertEquals("redirect:/form/previous-employment/1/add-work", result);
    }

    @Test
    public void SubmitEmploymentStatusForm_WithErrors_ReturnsEmploymentStatusForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));

        String result = sut.submitStatus(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        assertEquals("form/common/boolean", result);
    }

    @Test
    public void GetEmploymentStatusFormInWelsh_ReturnsEmploymentStatusForm() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn(IDENTIFIER);
        String result = sut.getView(2, mockModel, "claimId", mockRequest);
        verify(mockModel, times(2)).addAttribute(any(), any());
        assertEquals("form/common/boolean", result);
    }

    @Test
    public void SubmitEmploymentStatusFormInWelsh_WithTrue_ReturnsAddWorkForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/%s/add-work");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn(IDENTIFIER);

        String result = sut.submitStatus(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        verify(mockModel, times(2)).addAttribute(any(), any());
        assertEquals("redirect:/form/previous-employment/1/add-work", result);
    }

    @Test
    public void SubmitEmploymentStatusFormInWelsh_WithErrors_ReturnsEmploymentStatusForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn(IDENTIFIER);

        String result = sut.submitStatus(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        verify(mockModel, times(2)).addAttribute(any(), any());
        assertEquals("form/common/boolean", result);
    }

}
