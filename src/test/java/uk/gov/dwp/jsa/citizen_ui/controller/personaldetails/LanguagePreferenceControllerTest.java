package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LanguagePreferenceControllerTest {

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private Model mockModel;

    @Mock
    private Step step;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private LanguagePreferenceController sut;

    private LanguagePreferenceForm form;
    private LanguagePreferenceQuestion question;

    @Before
    public void setUp() {
        form = new LanguagePreferenceForm();
        question = new LanguagePreferenceQuestion();
        form.setLanguagePreferenceQuestion(question);

        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new LanguagePreferenceController(mockClaimRepository, mockRoutingService);
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockRoutingService.getStep("form/personal-details/language-preference")).thenReturn(Optional.of(step));

        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void getLanguagePreference() {
        String path = sut.getLanguagePreference(mockModel, null, mockRequest);

        assertThat(path, is("form/personal-details/language-preference"));
    }

    @Test
    public void submitLanguagePreference() {
        when(mockRoutingService.getNext(any())).thenReturn("/next/step");
        String path = sut.submitLanguagePreference("TEST_CLAIM_ID", form, mockBindingResult, mockResponse,
                mockModel, mockRequest);

        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any());
        assertThat(path, is("redirect:/next/step"));
    }

    @Test
    public void submitLanguagePreference_WithError() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String path = sut.submitLanguagePreference("TEST_CLAIM_ID", form, mockBindingResult, mockResponse,
                mockModel, mockRequest);

        verify(mockClaimRepository, never()).save(Mockito.any());
        assertThat(path, is("form/personal-details/language-preference"));
    }
}
