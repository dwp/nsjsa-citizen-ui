package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.services.NinoSanitiser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NinoControllerTests {
    public static final String IDENTIFIER = "form/nino";
    private NinoController sut;
    @Mock private BindingResult mockBindingResult;
    @Mock private StringForm<NinoQuestion> mockNinoForm;
    @Mock private NinoQuestion mockNinoQuestion;
    @Mock private Model mockModel;
    @Mock private ClaimRepository mockClaimRepository;
    @Mock private RoutingService routingService;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpServletRequest mockRequest;
    @Mock private Step step;
    @Mock private NinoSanitiser mockNinoSanitiser;

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createControllerUnderTest() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        sut = new NinoController(mockClaimRepository, routingService, mockNinoSanitiser);
    }

    @Test()
    public void GetNinoForm_ReturnsCorrectView() {
        String expected = "form/common/text";
        String actual = sut.nino(mockModel, COOKIE, mockRequest);
        assertEquals(expected, actual);
        verify(mockClaimRepository).findById(anyString());
    }

    @Test()
    public void GetNinoFormWithANullClaimId_CreatesNewClaimObj() {
        String expected = "form/common/text";
        String actual = sut.nino(mockModel, null, mockRequest);
        assertEquals(expected, actual);
        verify(mockClaimRepository, times(0)).findById(anyString());
    }

    @Test
    public void GivenValidNino_PostNino_SavesNinoAnd_ReturnsRedirectToDob() {
        String expected = "redirect:/form/personal-details";
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockNinoForm.getQuestion()).thenReturn(mockNinoQuestion);
        when(mockNinoSanitiser.sanitise(anyString())).thenReturn("AA123456A");
        when(routingService.getNext(any())).thenReturn("/form/personal-details");

        String actual = sut.submitNino(COOKIE, mockNinoForm, mockBindingResult, mockResponse, mockModel);
        assertThat(actual, is(expected));
        verify(mockClaimRepository).findById(anyString());
        verify(mockNinoForm, atLeastOnce()).getQuestion();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
    }

    @Test
    public void GivenInValidNino_PostNino_ReturnsNinoFormWithErrors() {
        String expected = "form/common/text";
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String actual = sut.submitNino(COOKIE, mockNinoForm, mockBindingResult, mockResponse, mockModel);
        assertEquals(expected, actual);
    }

    @Test
    public void SubmitNinoWithNullClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockNinoForm.getQuestion()).thenReturn(mockNinoQuestion);
        when(mockNinoQuestion.getValue()).thenReturn("AA 12 34 56 A");
        when(mockNinoSanitiser.sanitise(eq("AA 12 34 56 A"))).thenReturn("AA123456A");
        when(routingService.getNext(any())).thenReturn("/form/personal-details");

        String path = sut.submitNino(null, mockNinoForm, mockBindingResult, mockResponse, mockModel);

        MatcherAssert.assertThat(path, is("redirect:/form/personal-details"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockNinoForm, atLeastOnce()).getQuestion();
        verify(mockResponse).addCookie(any(Cookie.class));
    }
}
