package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

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
public class AboutAddressControllerTest {

    public static final String IDENTIFIER = "form/personal-details/address";
    private AboutAddressController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private AddressForm mockAddressForm;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;
    @Mock
    private AddressQuestion mockAddressQuestion;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private AddressQuestion addressQuestion = new AddressQuestion();

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createSut() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockAddressForm.getQuestion()).thenReturn(addressQuestion);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        sut = new AboutAddressController(mockClaimRepository, routingService);
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GetAddressAndClaimIsNull_returnsCorrectView() {
        String path = sut.getAddress(mockModel, null, mockRequest);

        assertThat(path, is("form/personal-details/address"));
        verify(mockModel).addAttribute(eq("addressForm"), any(AddressForm.class));
    }

    @Test
    public void GetAddressAndClaimDoesNotExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        String path = sut.getAddress(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/address"));
        verify(mockModel).addAttribute(eq("addressForm"), any(AddressForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetAddressAndClaimExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getAddress(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/address"));
        verify(mockModel).addAttribute(eq("addressForm"), any(AddressForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void SubmitAddressWithError_returnsErrorForAllFields() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        AddressQuestion addressQuestion = new AddressQuestion();
        addressQuestion.setPostCode("SY7 5UH");

        AddressForm addressForm = new AddressForm(addressQuestion);
        String path = sut
                .submitAddress(COOKIE, addressForm, mockBindingResult, mockResponse, mockModel, mockRequest);

        assertThat(path, is("form/personal-details/address"));
    }

    @Test
    public void SubmitAddressWithEmptyClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/personal-details/address-is-it-postal");
        when(mockAddressQuestion.getPostCode()).thenReturn("SY7 5UH");
        when(mockAddressForm.getAddressQuestion()).thenReturn(mockAddressQuestion);

        String path = sut.submitAddress("", mockAddressForm, mockBindingResult, mockResponse, mockModel, mockRequest);

        assertThat(path, is("redirect:/form/personal-details/address-is-it-postal"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockAddressForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));

        verify(routingService).deregisterStep(any());
        verify(routingService, times(2)).registerStep(any());
    }

    @Test
    public void SubmitAddressWithClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/personal-details/address-is-it-postal");
        when(mockAddressQuestion.getPostCode()).thenReturn("SY7 5UH");
        when(mockAddressForm.getAddressQuestion()).thenReturn(mockAddressQuestion);

        String path = sut
                .submitAddress(COOKIE, mockAddressForm, mockBindingResult, mockResponse, mockModel, mockRequest);

        assertThat(path, is("redirect:/form/personal-details/address-is-it-postal"));
        verify(mockClaimRepository, times(2)).findById(eq(COOKIE));
        verify(mockAddressForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }
}
