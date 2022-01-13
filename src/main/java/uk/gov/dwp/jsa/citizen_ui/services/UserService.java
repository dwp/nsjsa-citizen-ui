package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;

@Service
@Configuration
public class UserService {

    @Value("${layout.header.phasebanner.href.agent}")
    private String agentUrl;

    @Value("${layout.header.phasebanner.href.citizen}")
    private String citizenUrl;

    @Value("${layout.header.phasebanner.href.citizen.welsh}")
    private String citizenUrlWelsh;

    private final HttpServletRequest request;
    private final CookieLocaleResolver cookieLocaleResolver;
    private final Environment env;

    public UserService(final Environment env, final HttpServletRequest request,
                       final CookieLocaleResolver cookieLocaleResolver) {
        this.env = env;
        this.request = request;
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    public String getSurveyURL() {

        if (Boolean.parseBoolean(env.getProperty("agent.mode"))) {
            return agentUrl;
        } else if (WelshTextUtils.isCurrentRequestInWelsh(cookieLocaleResolver, request)) {
            return citizenUrlWelsh;
        } else {
             return citizenUrl;
        }
    }
}
