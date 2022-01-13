package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AboutPostalControllerTest {

    public static final String IDENTIFIER = "form/personal-details/address-is-it-postal";
    private AboutPostalController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BooleanQuestion mockQuestion;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private GuardForm postalForm;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createSut() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        sut = new AboutPostalController(mockClaimRepository, routingService);
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void GetPostalAndClaimIsNull_returnsCorrectView() {
        String path = sut.getPostal(mockModel, null, mockRequest);

        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
    }

    @Test
    public void GetPostalAndClaimDoesNotExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        String path = sut.getPostal(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetPostalAndClaimExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getPostal(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void SubmitPostalWithError_returnsErrorForAllFields() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        GuardForm postalForm = new GuardForm(new BooleanQuestion());
        String path = sut
                .submitPostal(COOKIE, postalForm, mockBindingResult, mockResponse, mockRequest, mockModel);

        assertThat(path, is("form/common/boolean"));
    }

    @Test
    public void SubmitPostalWithEmptyClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/bank-account");
        when(postalForm.getQuestion()).thenReturn(mockQuestion);

        String path = sut.submitPostal("", postalForm, mockBindingResult, mockResponse, mockRequest, mockModel);

        assertThat(path, is("redirect:/form/bank-account"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(postalForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void SubmitPostalWithClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/bank-account");
        when(postalForm.getQuestion()).thenReturn(mockQuestion);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut
                .submitPostal(COOKIE, postalForm, mockBindingResult, mockResponse, mockRequest, mockModel);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);

        assertThat(path, is("redirect:/form/bank-account"));
        verify(mockClaimRepository).findById(eq(COOKIE));
        verify(postalForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }
}
