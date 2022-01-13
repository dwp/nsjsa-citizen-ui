package uk.gov.dwp.jsa.citizen_ui.controller.citizen;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Controller for rendering Cookie Policies.
 */
@Controller
public class CookiePolicyController {

    @GetMapping("/cookies-policy")
    public String getCookiePolicy(final HttpServletRequest request, final Model model) {
        model.addAttribute("redirectUrl", getReferrer(request));
        return "citizen/cookies-policy";
    }

    @PostMapping("/cookies-policy")
    public String setCookiePolicy(final HttpServletRequest request,
        final HttpServletResponse response,
        final Model model,
        @RequestParam(value = "redirectUrl", required = false) final String redirectUrl,
        @RequestParam(name = "allow-analytics-cookies", required = false) final String allowAnalytics) {

        if (allowAnalytics == null) {
            model.addAttribute("hasError", true);
            model.addAttribute("redirectUrl", redirectUrl);
            return "citizen/cookies-policy";
        }

        addCookie(response, "allow-analytics-cookies", allowAnalytics);
        return "redirect:" + (redirectUrl != null ? redirectUrl : getReferrer(request));
    }

    @PostMapping("/cookies-policy/hide")
    public String hideCookieBanner(final HttpServletRequest request,
                                   final HttpServletResponse response) {
        addCookie(response, "hide-cookie-banner", "yes");
        return "redirect:" + getReferrer(request);
    }

    private void addCookie(final HttpServletResponse response, final String cookieName, final String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(getYearDiffInSeconds());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private int getYearDiffInSeconds() {
        return (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.now().plusYears(1));
    }

    private String getReferrer(final HttpServletRequest request) {
        return request.getHeader("referer");
    }

}
