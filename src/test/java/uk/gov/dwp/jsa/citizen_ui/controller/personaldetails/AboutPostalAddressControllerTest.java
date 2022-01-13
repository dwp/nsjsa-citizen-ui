package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class AboutPostalAddressControllerTest {

    public static final String IDENTIFIER = "form/personal-details/postal-address";
    private AboutPostalAddressController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private PostalAddressForm mockPostalAddressForm;
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
    private PostalAddressQuestion postalAddressQuestion = new PostalAddressQuestion();

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createSut() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockPostalAddressForm.getQuestion()).thenReturn(postalAddressQuestion);
        sut = new AboutPostalAddressController(mockClaimRepository, routingService);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GetPostalAddressAndClaimIsNull_returnsCorrectView() {
        String path = sut.getAddress(mockModel, null, mockRequest);

        assertThat(path, is("form/personal-details/postal-address"));
        verify(mockModel).addAttribute(eq("postalAddressForm"), any(PostalAddressForm.class));
    }

    @Test
    public void GetPostalAddressAndClaimDoesNotExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        String path = sut.getAddress(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/postal-address"));
        verify(mockModel).addAttribute(eq("postalAddressForm"), any(PostalAddressForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetPostalAddressAndClaimExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getAddress(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/postal-address"));
        verify(mockModel).addAttribute(eq("postalAddressForm"), any(PostalAddressForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void SubmitPostalAddressWithError_returnsErrorForAllFields() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        PostalAddressForm postalAddressForm = new PostalAddressForm(new PostalAddressQuestion());
        String path = sut
                .submitAddress(COOKIE, postalAddressForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("form/personal-details/postal-address"));
    }

    @Test
    public void SubmitPostalAddressWithEmptyClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/bank-account");

        String path = sut.submitAddress("", mockPostalAddressForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/bank-account"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockPostalAddressForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void SubmitPostalAddressWithEmptyClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/bank-account");

        String path = sut
                .submitAddress(COOKIE, mockPostalAddressForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/bank-account"));
        verify(mockClaimRepository).findById(eq(COOKIE));
        verify(mockPostalAddressForm, times(1)).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }
}
