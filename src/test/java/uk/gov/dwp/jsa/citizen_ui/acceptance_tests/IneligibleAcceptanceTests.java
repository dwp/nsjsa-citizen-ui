package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.Ignore;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class IneligibleAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenNotUkResident_shouldHaveExpectedLinks() throws Exception {
        mockMvc.perform(
                get("/form/eligibility/residence/ineligible"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/benefits-calculators\"")))
                .andExpect(content().string(containsString("href=\"/form/eligibility/residence\"")))
                .andExpect(content().string(containsString("href=\"/form/eligibility/working\"")));
    }

    @Test
    public void workingLessThan16Hours_shouldHaveExpectedLinks() throws Exception {
        mockMvc.perform(
                get("/form/eligibility/working-over/ineligible"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/benefits-calculators\"")))
                .andExpect(content().string(containsString("href=\"/form/eligibility/working-over\"")))
                .andExpect(content().string(containsString("href=\"/form/eligibility/ineligible/apply\"")));
    }

    @Ignore
    @Test
    public void notContributedSufficientNI_shouldHaveExpectedLinks() throws Exception {
        mockMvc.perform(
                get("/form/eligibility/contributions/ineligible"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/benefits-calculators\"")))
                .andExpect(content().string(containsString("href=\"/form/eligibility/contributions\"")))
                .andExpect(content().string(containsString("href=\"/form/default-claim-start\"")));
    }
}
