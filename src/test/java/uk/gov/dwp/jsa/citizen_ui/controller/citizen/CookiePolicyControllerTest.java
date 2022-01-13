package uk.gov.dwp.jsa.citizen_ui.controller.citizen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookiePolicyControllerTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Model model;

    private CookiePolicyController sut;

    @Before
    public void setup() {
        sut = new CookiePolicyController();
    }

    @Test
    public void getCookiePolicy() {
        String path = sut.getCookiePolicy(request, model);
        assertThat(path, is("citizen/cookies-policy"));
    }

    @Test
    public void setCookiePolicy() {
        when(request.getHeader("referer")).thenReturn("previouspage");
        String redirect = sut.setCookiePolicy(request, response, model, null, "testyes");
        ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argumentCaptor.capture());
        assertThat(redirect, is("redirect:previouspage"));
        assertThat(argumentCaptor.getValue().getName(), is("allow-analytics-cookies"));
        assertThat(argumentCaptor.getValue().getValue(), is("testyes"));
    }

    @Test
    public void hideCookieBanner() {
        when(request.getHeader("referer")).thenReturn("previouspage");
        String redirect = sut.hideCookieBanner(request, response);
        ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argumentCaptor.capture());
        assertThat(redirect, is("redirect:previouspage"));
        assertThat(argumentCaptor.getValue().getName(), is("hide-cookie-banner"));
        assertThat(argumentCaptor.getValue().getValue(), is("yes"));
    }
}
