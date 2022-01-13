package uk.gov.dwp.jsa.citizen_ui.util;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WelshTextUtilsTest {

    private static final String WELSH_IDENTIFY                                   = "cy";
    private static final String ENGLISH_IDENTIFY                                 = "en";
    private static final String PATH_WITH_ONE_ALTERNATIVE_TEXT                   = "/form/current-work/has-another-job";
    private static final String PATH_WITH_TWO_ALTERNATIVE_TEXT                   = "/form/backdating/have-you-been-in-paid-work-since";
    private static final String IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_YES = "alternativeWelshTextYES";
    private static final String IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_NO  = "alternativeWelshTextNO";

    @Mock
    private CookieLocaleResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @Test
    public void givenRequestIsWelsh_isPageWithAlternativeTextWithOneAlternativeText_setsAlternativeWelshText() {
        this.resolver = mock(CookieLocaleResolver.class);
        this.request = mock(HttpServletRequest.class);
        this.model = mock(Model.class);
        when(resolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(new Locale(WELSH_IDENTIFY));
        when(request.getServletPath()).thenReturn(PATH_WITH_ONE_ALTERNATIVE_TEXT);

        WelshTextUtils.useAlternativeWelshTextBooleanPage(resolver, request, model);
        verify(model, times(1)).addAttribute(
                IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_YES, "common.question.yesno.choice.true.alternative.oes");
        verify(model, times(1)).addAttribute(
                IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_NO, null);
    }

    @Test
    public void givenRequestIsWelsh_isPageWithAlternativeTextWitTwoAlternativeTexts_setsAlternativeWelshText() {
        this.resolver = mock(CookieLocaleResolver.class);
        this.request = mock(HttpServletRequest.class);
        this.model = mock(Model.class);
        when(resolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(new Locale(WELSH_IDENTIFY));
        when(request.getServletPath()).thenReturn(PATH_WITH_TWO_ALTERNATIVE_TEXT);

        WelshTextUtils.useAlternativeWelshTextBooleanPage(resolver, request, model);
        verify(model, times(1)).addAttribute(
                IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_YES, "common.question.yesno.choice.true.alternative.ydw");
        verify(model, times(1)).addAttribute(
                IS_ALT_WELSH_TEXT_REQUIRED_TEMPLATE_VARIABLE_NO, "common.question.yesno.choice.false.alternative.na");
    }

    @Test
    public void givenRequestIsWelsh_isNotPageWithAlternativeText_DoesNotSetAlternativeWelshText() {
        this.resolver = mock(CookieLocaleResolver.class);
        this.request = mock(HttpServletRequest.class);
        this.model = mock(Model.class);
        when(resolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(new Locale(WELSH_IDENTIFY));
        when(request.getServletPath()).thenReturn("/path-without-alt-text");

        WelshTextUtils.useAlternativeWelshTextBooleanPage(resolver, request, model);
        verify(model, times(0)).addAttribute(any(), any());
    }

    @Test
    public void givenRequestIsEnglish_DoesNotSetAlternativeWelshText() {
        this.resolver = mock(CookieLocaleResolver.class);
        this.request = mock(HttpServletRequest.class);
        this.model = mock(Model.class);
        when(resolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(new Locale(ENGLISH_IDENTIFY));

        WelshTextUtils.useAlternativeWelshTextBooleanPage(resolver, request, model);
        verify(model, times(0)).addAttribute(any(), any());
    }

    @Test
    public void AlternativeBooleanPageLocalesHolder_twoArgumentConstructorInitialization_setsProperties() {
        String yesValue = "some.locale";
        String noValue  = "another.locale";
        String classFieldNameYesLocale = "yesLocale";
        String classFieldNameNoLocale = "noLocale";
        WelshTextUtils.AlternativeBooleanPageLocalesHolder sut =
                new WelshTextUtils.AlternativeBooleanPageLocalesHolder(yesValue, noValue);

        assertEquals(ReflectionTestUtils.getField(sut, classFieldNameYesLocale), yesValue);
        assertEquals(ReflectionTestUtils.getField(sut, classFieldNameNoLocale), noValue);
    }
}
