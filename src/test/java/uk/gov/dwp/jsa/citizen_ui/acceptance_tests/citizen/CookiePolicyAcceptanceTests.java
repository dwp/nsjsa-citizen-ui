package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.citizen;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@Ignore
public class CookiePolicyAcceptanceTests {

    private static final String CITIZEN_COOKIES_POLICY_URL = "/cookies-policy";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetCitizenCookiePolicyPage_ShouldHaveExpectedHeaders() throws Exception {
        mockMvc.perform(
                get(CITIZEN_COOKIES_POLICY_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("How cookies are used on this service")))
                .andExpect(content().string(containsString("If your cookies are switched off")))
                .andExpect(content().string(containsString("Measuring website usage (Google Analytics)")))
                .andExpect(content().string(containsString("Our introductory message")))
                .andExpect(content().string(containsString("Service cookies")));
    }

    @Test
    public void GetCitizenCookiePolicyPage_ShouldHaveExpectedCookieTables() throws Exception {
        mockMvc.perform(
                get(CITIZEN_COOKIES_POLICY_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<table id=\"govuk.google.analytics.cookie.table\" class=\"govuk-table\">")))
                .andExpect(content().string(containsString("<table id=\"govuk.seen_cookie_message.cookie.table\" class=\"govuk-table\">")))
                .andExpect(content().string(containsString("<table id=\"govuk.servicecookies.cookie.table\" class=\"govuk-table\">")));
    }

    @Test
    public void GetEnglishLocaleByDefault() throws Exception {

        mockMvc.perform(get(CITIZEN_COOKIES_POLICY_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Cookies"
                )));
    }

    @Test
    public void GetWelshLocale_whenCookieIsPresent() throws Exception {

        mockMvc.perform(get(CITIZEN_COOKIES_POLICY_URL)
                .cookie(new Cookie(Constants.LANG_COOKIE_ID, "cy"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Cwcis"
                )));
    }

}
