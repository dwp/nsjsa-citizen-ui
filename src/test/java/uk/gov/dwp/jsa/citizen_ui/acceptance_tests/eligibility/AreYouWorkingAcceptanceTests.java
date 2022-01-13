package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.eligibility;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class AreYouWorkingAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GivenGetAreYouWorkingQuestion_ShouldDirectToWorkingQuestionForm() throws Exception {
        mockMvc.perform(
                get("/form/eligibility/working"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/form/eligibility/working\"")))
                .andExpect(content().string(containsString("id=\"boolean-form\"")));
    }

    @Test
    public void GivenSubmitAreYouWorkingQuestion_ShouldShowErrorIfNotFilledIn() throws Exception {
        mockMvc.perform(
                post("/form/eligibility/working")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
