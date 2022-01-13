package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.dwp.jsa.adaptors.ServicesProperties;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.ConfirmationForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;
import uk.gov.dwp.jsa.citizen_ui.util.ClaimBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConfirmationControllerTest {

    private static final String SERVER = "server";
    public static final String IDENTIFIER = "claimant-confirmation";
    private static final UUID CLAIM_ID = UUID.randomUUID();
    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private PensionsService mockPensionsService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Claim mockClaim;
    @Mock
    private Step mockStep;
    @Mock
    private ServicesProperties servicesProperties;

    private String agentUiBookingUrl = SERVER + "/claim/created";
    private boolean agentMode = true;

    private ConfirmationController sut;

    @Before
    public void createControllerUnderTest() {
        when(servicesProperties.getAgentUiServer()).thenReturn(SERVER);
        sut = new ConfirmationController(mockClaimRepository, mockRoutingService, mockPensionsService, servicesProperties);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(mockStep));
    }

    @Test
    public void getConfirmationTemplateName() {
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.getClaimantId()).thenReturn(CLAIMANT_ID.toString());

        String result = sut.getView(CLAIM_ID.toString(), mockModel, mockRequest, mockResponse, agentMode);
        verify(mockModel).addAttribute(eq("isAgent"), eq(agentMode));
        verify(mockModel).addAttribute(eq("bookAppointmentUrl"), eq(agentUiBookingUrl));
        verify(mockClaimRepository).deleteById(CLAIM_ID.toString());
        verify(mockResponse, times(4)).addCookie(any(Cookie.class));
        assertThat(result, is("claimant-confirmation"));
    }

    @Test
    public void getConfirmationTemplateNameSetAgentModeFalse() {
        final UUID claimantId = UUID.randomUUID();
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.getClaimantId()).thenReturn(claimantId.toString());

        String result = sut.getView(CLAIM_ID.toString(), mockModel, mockRequest, mockResponse, false);
        verify(mockModel).addAttribute(eq("isAgent"), eq(false));
    }

    @Test
    public void WhenTrueInClaim_hasBeenToJuryServiceIsSet() {
        Claim claim = new ClaimBuilder().withJuryService(true).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isHasBeenToJuryService(), is(true));
    }

    @Test
    public void WhenFalseInClaim_hasBeenToJuryServiceIsSetToFalse() {
        Claim claim = new ClaimBuilder().withJuryService(false).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isHasBeenToJuryService(), is(false));
    }

    @Test
    public void whenFalseInClaim_HasPreviousWorkIsSetToFalse() {
        Claim claim = new ClaimBuilder().withPreviousJob(false).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isHasWorkedInLast6Months(), is(false));
    }

    @Test
    public void whenTrueInClaim_HasPreviousWorkIsSetToTrue() {
        Claim claim = new ClaimBuilder().withPreviousJob(true).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isHasWorkedInLast6Months(), is(true));
    }

    @Test
    public void whenFalseInClaim_expectPreviousPaymentIsSetToFalse() {
        Claim claim = new ClaimBuilder().withPreviousJob(true).withExpectedPayment(false).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isExpectPaymentInLast6Months(), is(false));
    }

    @Test
    public void whenTrueInClaim_expectPreviousPaymentIsSetToTrue() {
        Claim claim = new ClaimBuilder().withPreviousJob(true).withExpectedPayment(true).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isExpectPaymentInLast6Months(), is(true));
    }

    @Test
    public void whenFalseInClaim_expectCurrentWorkSetToFalse() {
        Claim claim = new ClaimBuilder().withCurrentJob(false).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isCurrentlyWorkingWithPay(), is(false));
    }

    @Test
    public void whenTrueInClaim_expectCurrentWorkSetToTrue() {
        Claim claim = new ClaimBuilder().withCurrentJob(true).withFortnightlyPay().build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isCurrentlyWorkingWithPay(), is(true));
    }

    @Test
    public void whenTrueInClaim_expectNeedsBankEvidenceSetToTrue(){
        Claim claim = new ClaimBuilder().build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.getNeedsBankDetailsEvidence(), is(true));
    }

    @Test
    public void whenTrueInClaim_expectHasChangedStateDateSetToTrue(){
        Claim claim = new ClaimBuilder().withBackDatedClaim(new ClaimStartDateQuestion(LocalDate.now().minusDays(2))).build();
        ConfirmationForm form = sut.createNewForm(claim);
        assertThat(form.isHasChangedStartDate(), is(true));
    }
}
