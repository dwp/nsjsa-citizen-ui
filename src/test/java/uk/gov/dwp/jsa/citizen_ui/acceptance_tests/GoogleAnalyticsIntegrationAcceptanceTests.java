package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;

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
@ActiveProfiles({"local_test", "mockredis"})
public class GoogleAnalyticsIntegrationAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    private final Cookie cookieAllowAnalytics = new Cookie("allow-analytics-cookies", "yes");

    @Test
    public void GetAnyUrl_ReturnsFormWithGoogleAnalyticsIntegrated() throws Exception {
        mockMvc.perform(get("/form/declaration").with(csrf()).cookie(cookieAllowAnalytics))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("https://www.googletagmanager.com/gtm.js?id=")));
    }

}
