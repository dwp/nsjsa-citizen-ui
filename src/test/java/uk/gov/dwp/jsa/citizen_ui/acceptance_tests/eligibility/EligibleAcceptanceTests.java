package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.eligibility;

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
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.DefaultStartDateController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class EligibleAcceptanceTests {

    private static final String WARNING_MESSAGE =
            "It usually takes 20 minutes to complete the form. " +
                    "You cannot save your answers and come back to the form later. " +
                    "But you’ll get the chance to check and update your answers before you send it.";

    private static final String INFO_MESSAGE = "The Department for Work and Pensions will treat your personal information carefully. We may use it for any of " +
            "our purposes. To learn about your information rights and how we use information, see our";

    private static final String DATA_USE_LINK = "https://www.gov.uk/government/organisations/" +
            "department-for-work-pensions/about/personal-information-charter";

    private static final String FORWARD_LINK = "href=\"/" + DefaultStartDateController.IDENTIFIER + "\"";


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getEligiblePage_returnsEligiblePage() throws Exception {

        mockMvc.perform(
                get("/form/eligibility/eligible"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(WARNING_MESSAGE)))
                .andExpect(content().string(containsString(INFO_MESSAGE)))
                .andExpect(content().string(containsString(DATA_USE_LINK)))
                .andExpect(content().string(containsString(FORWARD_LINK)));
    }
}
