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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.ALLOWED_LANG_CHANGE_URL;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class StartPageAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GivenStartPage_ShouldHaveExpectedRelatedLinks() throws Exception {

        mockMvc.perform(
                get("/claimant"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/jobseekers-allowance/your-jsa-interview\"")))
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/jobseekers-allowance/further-information\"")))
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/national-insurance/national-insurance-classes\"")))
                .andExpect(content().string(containsString("href=\"https://www.gov.uk/contact-jobcentre-plus/new-benefit-claims\"")));
    }

    @Test
    public void GetEnglishLocaleByDefault() throws Exception {

        mockMvc.perform(get(ALLOWED_LANG_CHANGE_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Apply for New Style Jobseeker’s Allowance")));
    }

}
