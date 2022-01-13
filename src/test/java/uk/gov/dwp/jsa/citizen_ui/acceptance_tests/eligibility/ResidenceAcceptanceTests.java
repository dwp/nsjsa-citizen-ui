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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ResidenceAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void submitResidenceFormYesSelected_ReturnsAgeForm() throws Exception {
        mockMvc.perform(post("/form/eligibility/residence")
                .param("question.choice", "true")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/eligibility/working")));
    }

    @Test
    public void submitResidenceFormNoSelected_ReturnsIneligibilityPage() throws Exception {
        mockMvc.perform(post("/form/eligibility/residence")
                .param("question.choice", "false")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", is("/form/eligibility/residence/ineligible")));
    }

    @Test
    public void submitResidenceFormEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/eligibility/residence")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void submitResidenceFormIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/eligibility/residence")
                .param("residenceQuestion.ukResidence", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
