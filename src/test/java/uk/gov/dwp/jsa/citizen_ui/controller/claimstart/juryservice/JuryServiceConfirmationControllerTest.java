package uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.JuryService;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
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
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JuryServiceConfirmationControllerTest {

    public static final String IDENTIFIER = "form/claim-start/jury-service/have-you-been";
    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock
    private BooleanQuestion mockQuestion;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GuardForm mockForm;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private JuryServiceConfirmationController sut;

    @Mock
    private RoutingService routingService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    @Before
    public void setUp() {
        sut = new JuryServiceConfirmationController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void getJuryForm() {
        String viewName = sut.getJuryForm(mockModel, claimId, mockRequest);
        assertEquals("form/common/boolean", viewName);
    }

    @Test
    public void submitJuryFormHaveBeenTrue_returnJuryServiceStartDate() {

        when(routingService.getNext(any())).thenReturn("/form/claim-start/jury-service/start-date");
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        String result = sut.submitJuryForm(mockForm, mockBindingResult, claimId, mockResponse, mockRequest, mockModel);

        assertEquals("redirect:/form/claim-start/jury-service/start-date", result);
    }

    @Test
    public void submitJuryFormHasErrors_returnJuryService() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitJuryForm(mockForm, mockBindingResult, claimId, mockResponse, mockRequest, mockModel);

        assertEquals("form/common/boolean", result);
    }

    @Test
    public void getNextPathWhenHaveYouBeenFalse_returnJuryServiceStartDate() {

        when(routingService.getNext(any())).thenReturn("/form/claim-start/jury-service/start-date");
        when(mockForm.getQuestion().getChoice()).thenReturn(true);

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/claim-start/jury-service/start-date", result);
    }

    @Test
    public void getNextPathWhenHaveYouBeenTrueAndWorking_thenReturnCurrentEmployment() {

        when(routingService.getNext(any())).thenReturn("/form/current-work/are-you-working");
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(mockClaim.getAreYouWorkingQuestion().getAreYouWorking()).thenReturn(true);

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/current-work/are-you-working", result);
    }

    @Test
    public void getNextPathWhenHaveYouBeenTrueAndNotWorking_thenReturnPreviousEmployment() {

        when(routingService.getNext(any())).thenReturn("/form/current-work/are-you-working");
        when(mockForm.getQuestion().getChoice()).thenReturn(false);
        when(mockClaim.getAreYouWorkingQuestion().getAreYouWorking()).thenReturn(false);

        String result = sut.getNextPath(mockClaim, mockForm, null);

        assertEquals("redirect:/form/current-work/are-you-working", result);
    }

    @Test
    public void updateClaim() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockForm).getQuestion();
        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        JuryService juryService = new JuryService();
        final Circumstances circumstances = new Circumstances();
        circumstances.setJuryService(juryService);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final GuardForm form = new GuardForm(new GuardQuestion());
        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignFalseWhenNoJuryService() {
        final Circumstances circumstances = new Circumstances();
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final GuardForm form = new GuardForm(new GuardQuestion());
        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }
}
