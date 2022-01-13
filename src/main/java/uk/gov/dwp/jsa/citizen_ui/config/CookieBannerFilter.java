package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SEEN_COOKIE_MESSAGE;
import static uk.gov.dwp.jsa.citizen_ui.Constants.SEEN_COOKIE_MESSAGE_VALUE;

@Component
public class CookieBannerFilter extends GenericFilterBean {

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpRequest.setAttribute(SEEN_COOKIE_MESSAGE, null);

            Cookie cookieSeenMsg = WebUtils.getCookie(httpRequest, SEEN_COOKIE_MESSAGE);
            if (cookieSeenMsg == null && methodIsGet(httpRequest)) {
                addNewCookie(httpRequest, httpResponse);
            }
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    private boolean methodIsGet(final HttpServletRequest httpRequest) {
        return httpRequest != null && HttpMethod.GET.name().equalsIgnoreCase(httpRequest.getMethod());
    }

    private void addNewCookie(final HttpServletRequest httpServletRequest,
                              final HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(SEEN_COOKIE_MESSAGE, SEEN_COOKIE_MESSAGE_VALUE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(getMonthDiffInSeconds());
        httpServletResponse.addCookie(cookie);
        httpServletRequest.setAttribute(SEEN_COOKIE_MESSAGE, SEEN_COOKIE_MESSAGE_VALUE);
    }

    private int getMonthDiffInSeconds() {
        return (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
    }
}
