package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.error;

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
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class SessionTimeoutAcceptanceTests {

    private static final String SESSION_TIMEOUT_URL = "/error/session-timeout";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetSessionTimeoutPage_ShouldHaveExpectedContent() throws Exception {
        mockMvc.perform(
                get(SESSION_TIMEOUT_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Session time out")))
                .andExpect(content().string(containsString("Your session has ended.")))
                .andExpect(content().string(containsString("Due to inactivity your session has timed out")));
    }

    @Test
    public void GetSessionTimeoutPage_ShouldHaveLinkBackToTheStartPage() throws Exception {
        mockMvc.perform(
                get(SESSION_TIMEOUT_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "<a href=\"/\" role=\"button\" draggable=\"false\" class=\"govuk-button govuk-button--start\">Restart</a>"
                )));
    }


}
