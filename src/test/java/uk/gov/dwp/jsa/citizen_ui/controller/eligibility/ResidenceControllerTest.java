package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

@RunWith(MockitoJUnitRunner.class)
public class ResidenceControllerTest {
    public static final String IDENTIFIER = "/form/eligibility/residence";

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private RoutingService mockRoutingService;

    private String mockClaimId = "123e4567-e89b-12d3-a456-426655440000";

    @Mock
    StepInstance mockStepInstance;

    @Mock
    private Step mockStep;

    @Mock
    private Claim mockClaim;

    private ResidenceController sut;

    @Mock
    private ResidenceForm mockResidenceForm;

    @Before
    public void setUp() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockRoutingService.getLastGuard(any(), any())).thenReturn(Optional.of(mockStepInstance));
        sut = new ResidenceController(mockClaimRepository, mockCookieLocaleResolver, mockRoutingService);
    }

    @Test
    public void GetResidenceCookieIsNull_thenCreateNewClaimAndReturnForm() {
        String path = sut.getResidence(mockModel, null, mockRequest);

        assertThat(path, is("form/common/boolean"));
        assertEquals(sut.getStep().getIdentifier(), "form/eligibility/residence");
        verify(mockModel).addAttribute(eq("form"), any(ResidenceForm.class));
    }

    @Test
    public void GetResidenceCookieIsInvalid_thenCreateNewClaimAndReturnForm() {
        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.empty());
        String path = sut.getResidence(mockModel, mockClaimId, mockRequest);

        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(ResidenceForm.class));
        verify(mockClaimRepository).findById(mockClaimId);
        verify(mockClaimRepository).save(any());

    }

    @Test
    public void GetResidenceCookieIsValid_thenLoadClaimAndReturnForm() {
        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        String path = sut.getResidence(mockModel, mockClaimId, mockRequest);

        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(ResidenceForm.class));
        verify(mockClaimRepository).findById(mockClaimId);
    }

    @Test
    public void submitResidenceWithError_returnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String path = sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, mockClaimId, mockRequest, mockResponse);
        assertThat(path, is("form/common/boolean"));
        assertEquals(sut.getStep().getNextStepIdentifier(), "");
    }

    @Test
    public void submitResidenceWithFalse_returnsIneligible() {
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(false);

        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);
        sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, mockClaimId, mockRequest, mockResponse);
        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/residence/ineligible");
    }

    @Test
    public void submitResidenceWithTrue_returnsEligible() {
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(true);

        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);
        sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, mockClaimId, mockRequest, mockResponse);
        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/working");
    }

    @Test
    public void checkNextStepBasedOn_withTrueUkResidence() {
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(true);
        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);

        String isUkResidentPath = sut.nextStepBasedOn(mockResidenceForm, mockClaim);
        assertThat(isUkResidentPath, is("/form/eligibility/working"));
    }

    @Test
    public void checkNextStepBasedOn_withFalseUkResidence() {
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(false);
        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);

        String isUkResidentPath = sut.nextStepBasedOn(mockResidenceForm, mockClaim);
        assertThat(isUkResidentPath, is("/form/eligibility/residence/ineligible"));
    }

    @Test
    public void submitResidenceWithError_returnsResidencePage() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(true);
        String path = sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, "", mockRequest, mockResponse);

        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockResidenceForm, times(1)).setTranslationKey(any());
        verify(mockModel).addAttribute(eq("form"), any(ResidenceForm.class));
        assertThat(path, is("form/common/boolean"));
        assertThat(sut.getStep().getIdentifier(), is("form/eligibility/residence"));
    }

    @Test
    public void submitResidenceWithEmptyClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(true);
        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);
        sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, "", mockRequest, mockResponse);

        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockResidenceForm, times(3)).getQuestion();
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void submitResidenceWithValidClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(true);
        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);

        String path = sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, mockClaimId, mockRequest, mockResponse);

        assertThat(sut.getStep().getNextStepIdentifier(), is("/form/eligibility/working"));
        verify(mockClaimRepository).findById(eq(mockClaimId));
        verify(mockResidenceForm, times(3)).getQuestion();
        verify(mockClaim).setResidenceQuestion(any(ResidenceQuestion.class));
        verify(mockClaimRepository).save(mockClaim);
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void submitResidenceWithFalseUkResidence_UpdatesExistingClaimObj() {
        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(false);
        when(mockResidenceForm.getQuestion()).thenReturn(residenceQuestion);


        String path = sut.submitResidence(mockModel, mockResidenceForm, mockBindingResult, mockClaimId, mockRequest, mockResponse);

        assertThat(sut.getStep().getNextStepIdentifier(), is("/form/eligibility/residence/ineligible"));
        verify(mockClaimRepository).findById(eq(mockClaimId));
        verify(mockResidenceForm, times(3)).getQuestion();
        verify(mockClaim).setResidenceQuestion(any(ResidenceQuestion.class));
        verify(mockClaimRepository).save(mockClaim);
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    private void thenBackUrlIsSetOnLeaveAsExpected(final String stepIdentifier) {
        Step step = new Step(stepIdentifier, NO_ALTERNATIVE_IDENTIFIER, NO_ALTERNATIVE_IDENTIFIER, Section.NONE);
        StepInstance stepInstance = new StepInstance(step, 0, false, false, false);
        verify(mockRoutingService).leavePage(any(), eq(stepInstance));
    }
}
