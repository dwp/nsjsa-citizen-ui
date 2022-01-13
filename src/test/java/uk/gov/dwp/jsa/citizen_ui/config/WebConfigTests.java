package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import uk.gov.dwp.jsa.citizen_ui.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebConfigTests {

    private WebConfig sut = new WebConfig();
    @Mock
    HttpServletRequest mockHttpServletRequest;
    @Mock
    InterceptorRegistry mockInterceptorRegistry;


    @Test
    public void returnLocalResolverWithExpectedSettings() {
        when(mockHttpServletRequest.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME))
                .thenReturn(Locale.ENGLISH);

        CookieLocaleResolver clr = sut.localeResolver();

        assertThat(clr.getCookieName(), is(Constants.LANG_COOKIE_ID));
        assertThat(clr.getCookieMaxAge(), is(Constants.LANG_COOKIE_EXPIRY_SECONDS));
        assertThat(clr.resolveLocale(mockHttpServletRequest), is(Locale.ENGLISH));
    }

    @Test
    public void returnInterceptorWithExpectedSettings() {
        LocaleChangeInterceptor lci = sut.localeInterceptor();

        assertThat(lci.getParamName(), is(Constants.LANG_PARAM_NAME));
    }


    @Test
    public void addProvidedInterceptorToRegistry() {
        sut.addInterceptors(mockInterceptorRegistry);

        verify(mockInterceptorRegistry).addInterceptor(any(LocaleChangeInterceptor.class));
        verify(mockInterceptorRegistry).addInterceptor(any(CacheWebContentInterceptor.class));
    }
}
