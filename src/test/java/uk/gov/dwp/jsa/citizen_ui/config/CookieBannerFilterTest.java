package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.SEEN_COOKIE_MESSAGE;
import static uk.gov.dwp.jsa.citizen_ui.Constants.SEEN_COOKIE_MESSAGE_VALUE;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
public class CookieBannerFilterTest {

    private CookieBannerFilter cookieBannerFilter = new CookieBannerFilter();

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Mock
    private HttpServletResponse mockHttpResponse;

    @Mock
    private FilterChain mockChain;

    private Cookie cookie = new Cookie(SEEN_COOKIE_MESSAGE, SEEN_COOKIE_MESSAGE_VALUE);

    @Before
    public void setUp() {
        when(mockHttpRequest.getMethod()).thenReturn(HttpMethod.GET.name());
    }

    @Test
    public void doFilterSetsSeenMessageCookieIfNotPresent() throws IOException, ServletException {
        whenDoFilterIsInvoked();

        thenSeenCookieIsSet();
    }

    @Test
    public void doFilterDoesNotSetSeenMessageCookieIfPresent() throws IOException, ServletException {
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{cookie});

        whenDoFilterIsInvoked();

        thenSeenCookieIsNotSet();
    }

    @Test
    public void doFilterDoesNotSetSeenMessageCookieIfMethodIsPost() throws IOException, ServletException {
        when(mockHttpRequest.getMethod()).thenReturn(HttpMethod.POST.name());

        whenDoFilterIsInvoked();

        thenSeenCookieIsNotSet();
    }

    private void whenDoFilterIsInvoked() throws IOException, ServletException {
        cookieBannerFilter.doFilter(mockHttpRequest, mockHttpResponse, mockChain);
    }

    private void thenSeenCookieIsSet() throws IOException, ServletException {
        thenDefaultOperationsAreInvoked();
        verify(mockHttpResponse).addCookie(any(Cookie.class));
        verify(mockHttpRequest).setAttribute(SEEN_COOKIE_MESSAGE, SEEN_COOKIE_MESSAGE_VALUE);
    }

    private void thenSeenCookieIsNotSet() throws IOException, ServletException {
        thenDefaultOperationsAreInvoked();
        verify(mockHttpResponse, never()).addCookie(any(Cookie.class));
        verify(mockHttpRequest, never()).setAttribute(SEEN_COOKIE_MESSAGE, SEEN_COOKIE_MESSAGE_VALUE);
    }

    private void thenDefaultOperationsAreInvoked() throws IOException, ServletException {
        verify(mockChain).doFilter(mockHttpRequest, mockHttpResponse);
        verify(mockHttpRequest).setAttribute(SEEN_COOKIE_MESSAGE, null);
    }
}
