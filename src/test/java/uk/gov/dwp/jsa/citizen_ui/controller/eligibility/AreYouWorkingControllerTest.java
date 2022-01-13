package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

@RunWith(MockitoJUnitRunner.class)
public class AreYouWorkingControllerTest {
    public static final String IDENTIFIER = "form/eligibility/working";

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

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Mock
    private Claim mockClaim;

    private AreYouWorkingController sut;

    @Mock
    private AreYouWorkingForm mockAreYouWorkingForm;

    @Mock
    private Step step;

    @Before
    public void setUp() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));

        sut = new AreYouWorkingController(mockClaimRepository, mockRoutingService, mockCookieLocaleResolver);
    }

    @Test
    public void GetWorkingFormAndClaimIsNull_returnsCorrectView() {
        String path = sut.getAreYouWorking(mockModel, COOKIE_CLAIM_ID, mockRequest);

        assertThat(path, is("form/common/boolean"));
        assertEquals(sut.getStep().getIdentifier(), "form/eligibility/working");
        verify(mockModel).addAttribute(eq("form"), any(AreYouWorkingForm.class));
        thenWorkingFormIsAddedToModel();
    }

    @Test
    public void GetWorkingFormAndClaimNotExists_returnsCorrectView() {
        String path = sut.getAreYouWorking(mockModel, null, mockRequest);
        assertEquals(sut.getStep().getIdentifier(), "form/eligibility/working");
        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(AreYouWorkingForm.class));
        thenWorkingFormIsAddedToModel();
    }

    @Test
    public void SubmitWorkingFormWithError_returnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String path = sut.postAreYouWorking(mockModel, COOKIE, mockAreYouWorkingForm,
                mockBindingResult, mockResponse, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(AreYouWorkingForm.class));
        assertThat(path, is("form/common/boolean"));
        assertThat(sut.getStep().getIdentifier(), is(IDENTIFIER));
    }


    @Test
    public void GetWorkingFormAndClaimExists_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getAreYouWorking(mockModel, COOKIE, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(AreYouWorkingForm.class));
        assertThat(path, is("form/common/boolean"));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void SubmitWorkingFormWithClaimIdAndYesResponse_RedirectsToHoursWeekQuestion() {
        setAreYouWorkingAndResidentQuestions(false, true);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        sut.postAreYouWorking(mockModel, COOKIE, mockAreYouWorkingForm,
                mockBindingResult, mockResponse, mockRequest);

        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/eligible");
        verify(mockClaimRepository, times(1)).findById(anyString());
        verify(mockAreYouWorkingForm, times(2)).getQuestion();
        verify(mockClaim, times(1)).getResidenceQuestion();
        thenQuestionAndCookiesIsSavedAsExpected();
    }

    @Test
    public void SubmitWorkingFormWithWorkingResponseAndUkResident_RedirectsToHoursWeekQuestion() {
        setAreYouWorkingAndResidentQuestions(true, true);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        sut.postAreYouWorking(mockModel, COOKIE, mockAreYouWorkingForm,
                mockBindingResult, mockResponse, mockRequest);

        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/working-over");
        verify(mockClaimRepository, times(1)).findById(anyString());
        verify(mockAreYouWorkingForm, times(2)).getQuestion();
        verify(mockClaim, times(0)).getResidenceQuestion();
        thenQuestionAndCookiesIsSavedAsExpected();
    }

    @Test
    public void SubmitWorkingFormWithFalseUkResidence_RedirectsToIneligible() {
        setAreYouWorkingAndResidentQuestions(false, false);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        sut.postAreYouWorking(mockModel, COOKIE, mockAreYouWorkingForm,
                mockBindingResult, mockResponse, mockRequest);

        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/residence/working/ineligible");
        verify(mockClaimRepository, times(1)).findById(anyString());
        verify(mockAreYouWorkingForm, times(2)).getQuestion();
        verify(mockClaim, times(1)).getResidenceQuestion();
        thenQuestionAndCookiesIsSavedAsExpected();
    }

    private void setAreYouWorkingAndResidentQuestions(final boolean isWorking, final boolean isUkResident) {
        AreYouWorkingQuestion areYouWorkingQuestion = new AreYouWorkingQuestion();
        areYouWorkingQuestion.setAreYouWorking(isWorking);
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(isUkResident);
        when(mockAreYouWorkingForm.getQuestion()).thenReturn(areYouWorkingQuestion);
        when(mockClaim.getResidenceQuestion()).thenReturn(residenceQuestion);
    }

    private void thenQuestionAndCookiesIsSavedAsExpected() {
        verify(mockAreYouWorkingForm, times(2)).getQuestion();
        verify(mockClaimRepository).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    private void thenWorkingFormIsAddedToModel() {
        verify(mockModel).addAttribute(eq("form"), any(AreYouWorkingForm.class));
    }
}
