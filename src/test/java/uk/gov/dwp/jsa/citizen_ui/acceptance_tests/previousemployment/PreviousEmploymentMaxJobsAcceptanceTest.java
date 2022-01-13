package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment;

import org.hamcrest.Matchers;
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class PreviousEmploymentMaxJobsAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenPostToAddWork_WithNull_ReturnsError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/1/add-work")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void givenPostToAddWork_WithValidValues_ReturnsStartOfEmploymentQuestion() throws Exception {
        mockMvc.perform(post("/form/previous-employment/2/add-work")
                .param("question.choice", "true")
                .param("count", "2")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/3/dates")));
    }

    @Test
    public void givenPostToAddWork_WithCounterValue4_ReturnsMaxJobsWarningForm() throws Exception {
        mockMvc.perform(post("/form/previous-employment/4/add-work")
                .param("question.choice", "true")
                .param("count", "4")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/max-jobs")));
    }

    @Test
    public void givenGetToAddWork_ReturnsAddJobForm() throws Exception {
        mockMvc.perform(get("/form/previous-employment/1/add-work")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(
                        "Have you had another job that has ended in the past 6 months?")));
    }

    @Test
    public void givenGetToMaxJobsForm_ReturnsMaxJobsWarningForm() throws Exception {
        mockMvc.perform(get("/form/previous-employment/max-jobs")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(
                        "You have entered a maximum of 4 previous jobs"
                )));
    }
}
