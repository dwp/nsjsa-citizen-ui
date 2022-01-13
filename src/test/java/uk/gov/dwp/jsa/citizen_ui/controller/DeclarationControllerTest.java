package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.OfficeSearchServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ValidationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.LocalOffice;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.NotificationService;
import uk.gov.dwp.jsa.citizen_ui.services.RequiredDataService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.BankDetailsService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.ClaimantService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Locale.ENGLISH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeclarationControllerTest {
    private static final String FORM_DECLARATION_PATH = "form/declaration";
    private static final String CONFIRMATION_PATH = "/claimant-confirmation";
    private static final String REDIRECT_CONFIRMATION_PATH = "redirect:" + CONFIRMATION_PATH;
    private static final String COOKIE = UUID.randomUUID().toString();
    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    public static final String IDENTIFIER = "form/declaration";
    private static final String POST_CODE = "POST_CODE";
    private static final String OFFICE_ID = "OFFICE_ID";

    private DeclarationController sut;
    @Mock
    private LocalOffice localOffice;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private DeclarationForm mockDeclarationForm;
    @Mock
    private DeclarationQuestion mockQuestion;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Claim mockClaim;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private Claimant claimant;
    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;
    @Mock
    private CookieLocaleResolver mockCookieResolver;
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private ClaimantService claimantService;
    @Mock
    private ClaimantServiceAdaptor claimantServiceAdaptor;
    @Mock
    private BankDetailsService bankDetailsService;
    @Mock
    private BankDetailsServiceAdaptor bankDetailsServiceAdaptor;
    @Mock
    private CircumstancesService circumstancesService;
    @Mock
    private CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    @Mock
    private ValidationServiceAdaptor validationServiceAdaptor;
    @Mock
    private OfficeSearchServiceAdaptor officeSearchAdaptor;
    @Mock
    private RequiredDataService requiredDataService;

    @Before
    public void createSut() {
        sut = new DeclarationController(mockClaimRepository, routingService, claimantService, claimantServiceAdaptor,
                bankDetailsService, bankDetailsServiceAdaptor, circumstancesService, circumstancesServiceAdaptor,
                validationServiceAdaptor, mockNotificationService, mockCookieResolver,
                                        officeSearchAdaptor,true, requiredDataService);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieResolver.resolveLocale(mockRequest)).thenReturn(ENGLISH);
        CompletableFuture<Optional<UUID>> completableFutureClaimant =
                CompletableFuture.completedFuture(Optional.of(CLAIMANT_ID));
        CompletableFuture<Optional<UUID>> completableFutureCode =
                CompletableFuture.completedFuture(Optional.of(UUID.randomUUID()));
        CompletableFuture<Optional<LocalOffice>> completableFutureLocalOffice =
                CompletableFuture.completedFuture(Optional.of(localOffice));

        when(claimant.getAddress().getPostCode()).thenReturn(POST_CODE);
        when(localOffice.getJobCentreId()).thenReturn(OFFICE_ID);
        when(officeSearchAdaptor.getLocalOffice(POST_CODE)).thenReturn(completableFutureLocalOffice);
        when(claimantService.getDataFromClaim(any())).thenReturn(Optional.of(claimant));
        when(claimantServiceAdaptor.postClaimantData(any())).thenReturn(completableFutureClaimant);
        doNothing().when(mockNotificationService).notifyClaimant(any(), any());
        when(validationServiceAdaptor.updateStatus(any(), any())).thenReturn(completableFutureCode);
    }

    @Test
    public void getFormSetsLocaleAsExpected() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());

        sut.getDeclaration(mockModel, null, mockRequest);
        DeclarationForm form = sut.getForm();

        assertThat(form.getQuestion().getLocale(), is(ENGLISH.getLanguage()));
    }

    @Test
    public void GetDeclarationAndClaimIsNull_returnsCorrectView() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());

        String path = sut.get(mockModel, null, mockRequest);

        assertThat(path, is(FORM_DECLARATION_PATH));
        verify(mockModel).addAttribute(eq("form"),
                any(DeclarationForm.class));
        verify(mockClaimRepository).save(Mockito.any(Claim.class));
    }

    @Test
    public void GetDeclarationAndClaimDoesNotExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());

        String path = sut.get(mockModel, COOKIE, mockRequest);

        assertThat(path, is(FORM_DECLARATION_PATH));
        verify(mockModel).addAttribute(eq("form"),
                any(DeclarationForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetDeclarationAndClaimDoesExist_returnsCorrectView() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.get(mockModel, COOKIE, mockRequest);

        assertThat(path, is(FORM_DECLARATION_PATH));
        verify(mockModel).addAttribute(eq("form"),
                any(DeclarationForm.class));
        verify(mockClaimRepository).findById(COOKIE);
        verify(mockClaim).get(any(StepInstance.class));
    }

    @Test
    public void SubmitDeclarationWithError_returnsError() throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String path = sut.submitDeclaration(COOKIE, mockDeclarationForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is(FORM_DECLARATION_PATH));
    }

    @Test
    public void SubmitDeclarationWithEmptyClaimId_CreatesNewClaimObj() throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockDeclarationForm.getQuestion()).thenReturn(mockQuestion);
        when(routingService.getNext(any())).thenReturn(CONFIRMATION_PATH);

        String path = sut.submitDeclaration(UUID.randomUUID().toString(), mockDeclarationForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is(REDIRECT_CONFIRMATION_PATH));
        verify(mockClaimRepository, times(2)).findById(anyString());
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
    }

    @Test
    public void SubmitDeclarationWithClaimId_UpdatesExistingClaimObj() throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn(CONFIRMATION_PATH);
        when(mockDeclarationForm.getQuestion()).thenReturn(mockQuestion);
        when(mockClaimRepository.findById(any())).thenReturn(Optional.of(mockClaim));

        String path = sut.submitDeclaration(COOKIE, mockDeclarationForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is(REDIRECT_CONFIRMATION_PATH));
        verify(mockClaimRepository, times(2)).findById(eq(COOKIE));
        verify(mockClaim).save(any(StepInstance.class), eq(mockQuestion), any(Optional.class));
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        final UUID claimId = UUID.fromString(COOKIE);
        verify(mockNotificationService).notifyClaimant(claimId, CLAIMANT_ID);
        verify(mockClaim).setClaimantId(any());
    }

    @Test
    public void ensureThatICanSuccessfullyCreateTheInitialDateOfContact() {
        Claim claim = new Claim();
        when(mockDeclarationForm.getQuestion()).thenReturn(mockQuestion);
        sut.updateClaim(mockDeclarationForm, claim, stepInstance, Optional.empty());
        assertThat(claim.getInitialDateOfContact(), is(LocalDate.now()));
    }

    @Test
    public void EnsureThatICanSetAgreedToFalse() {
        Claim claim = new Claim();
        mockQuestion.setAgreed(false);
        mockQuestion.setAgreedInError(true);
        claim.setDeclarationQuestion(mockQuestion);
        assertThat(claim.getDeclarationQuestion().isAgreed(), is(false));
    }

    @Test
    public void EnsureThatICanSetAgreedToTrue() {
        Claim claim = new Claim();
        mockQuestion.setAgreed(true);
        claim.setDeclarationQuestion(mockQuestion);
        assertThat(claim.getDeclarationQuestion().isAgreed(), is(true));
    }

    @Test
    public void SubmitDeclarationWithAgreedTermsAndConditions_returnNextPage() throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockDeclarationForm.getQuestion()).thenReturn(mockQuestion);
        when(routingService.getNext(any())).thenReturn(CONFIRMATION_PATH);

        String path = sut.submitDeclaration(COOKIE, mockDeclarationForm,
                mockBindingResult, mockResponse, mockModel);
        assertThat(path, is("redirect:/claimant-confirmation"));
    }

    @Test
    public void SubmitDeclarationWithOutAgreedTermsAndConditions_returnErrorPage() throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String path = sut.submitDeclaration(COOKIE, mockDeclarationForm,
                mockBindingResult, mockResponse, mockModel);
        assertThat(path, is(FORM_DECLARATION_PATH));
    }
}
