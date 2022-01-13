package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController.FORM_SUMMARY;
import static uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController.NEXT_STEP_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController.EDIT_PARAMETER;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BankAccountControllerTest {
    public static final String IDENTIFIER = "form/bank-account";
    private BankAccountFormController sut;
    @Mock private Model mockModel;
    @Mock private ClaimRepository mockClaimRepository;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpServletRequest mockRequest;
    @Mock private Claim mockClaim;
    @Mock private BankAccountQuestion mockQuestion;
    @Mock private BindingResult mockBindingResult;
    @Mock private BankAccountForm mockBankAccountForm;
    @Mock private Step step;
    @Mock private StepInstance mockLastGuardStepInstance;
    @Mock private RoutingService routingService;

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createSut() {
        sut = new BankAccountFormController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(routingService.getLastGuard(eq(COOKIE), any(StepInstance.class))).thenReturn(Optional.of(mockLastGuardStepInstance));
    }

    @Test
    public void GetBankAccount_WhenClaimIsNull_returnsCorrectView() {
        String path = sut.getView(mockModel, null, mockRequest);
        assertThat(path, is("form/bank-account"));
    }

    @Test
    public void GetBankAccount_WhenClaimDoesNotExist_ReturnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        String path = sut.getView(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/bank-account"));
        verify(mockModel).addAttribute(eq("form"), any(BankAccountForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetBankAccount_WhenClaimExists_ReturnsCorrectView() {
        when(mockBankAccountForm.getQuestion()).thenReturn(mockQuestion);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getView(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/bank-account"));
        verify(mockModel).addAttribute(eq("form"), any(BankAccountForm.class));
        verify(mockClaimRepository).findById(COOKIE);
        verify(mockClaim).get(any(StepInstance.class));
    }

    @Test
    public void SubmitBankAccount_WithErrors_CreatesErrorForFields() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        BankAccountForm bankAccountForm = new BankAccountForm(new BankAccountQuestion());
        String path = sut.submitBankAccount(COOKIE, bankAccountForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("form/bank-account"));
    }

    @Test
    public void SubmitBankAccount_WithEmptyClaimId_CreatesClaimObject() {
        when(mockBankAccountForm.getQuestion()).thenReturn(mockQuestion);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/claim-start/jury-service/have-you-been");

        String path = sut.submitBankAccount("", mockBankAccountForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/claim-start/jury-service/have-you-been"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockBankAccountForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void SubmitBankAccount_WithClaimId_UpdatesClaimObject() {
        when(mockBankAccountForm.getQuestion()).thenReturn(mockQuestion);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockClaimRepository.findById(any())).thenReturn(Optional.of(mockClaim));
        when(routingService.getNext(any())).thenReturn("/form/claim-start/jury-service/have-you-been");

        String path = sut
                .submitBankAccount(COOKIE, mockBankAccountForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/claim-start/jury-service/have-you-been"));

        verify(mockClaimRepository).findById(eq(COOKIE));
        verify(mockClaim, times(1))
                .save(any(StepInstance.class), eq(mockQuestion), any(Optional.class));
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void GetRemoveAndRedirect_WhenClaimExists_verifyWeRemoveAndSaveClaim() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.removeAndRedirect(COOKIE, mockRequest);

        verify(routingService, times(1)).leavePage(any(), any(StepInstance.class));
        verify(mockClaim, times(1)).delete(any(StepInstance.class), anyInt());
        verify(mockClaimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    public void GetRemoveAndRedirect_WhenClaimExists_redirectsToNextStep() {
        when(mockRequest.getParameter(EDIT_PARAMETER)).thenReturn(null);

        String path = sut.removeAndRedirect(COOKIE, mockRequest);

        assertThat(path, is("redirect:" + NEXT_STEP_IDENTIFIER));
    }

    @Test
    public void GetRemoveAndRedirect_WhenClaimExists_redirectsToTheSummary() {
        when(mockRequest.getParameter(EDIT_PARAMETER)).thenReturn("SINGLE");

        String path = sut.removeAndRedirect(COOKIE, mockRequest);

        assertThat(path, is("redirect:" + FORM_SUMMARY));
    }
}
