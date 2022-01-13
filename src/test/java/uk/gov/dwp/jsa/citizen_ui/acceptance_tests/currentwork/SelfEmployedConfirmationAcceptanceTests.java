package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.currentwork;

import org.junit.After;
import org.junit.Before;
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
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
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
public class SelfEmployedConfirmationAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ClaimRepository claimRepository;
    @Before
    public void setUp() {
        claimRepository.deleteAll();
        assertThat(claimRepository.count(), is(0L));
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void Get_ReturnsForm() throws Exception {
        mockMvc.perform(get("/form/current-work/details/1/self-employed-confirmation")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"boolean-form\"")));
    }

    @Test
    public void SubmitForm_WithYesSelected_ReturnsHasAnotherJobForm() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/self-employed-confirmation")
                .param("count", "1")
                .param("question.choice", "true")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/current-work/1/has-another-job")));
    }

    @Test
    public void SubmitForm_WithNoSelected_ReturnsHasAnotherJobForm() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/self-employed-confirmation")
                .param("count", "1")
                .param("question.choice", "false")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/current-work/1/has-another-job")));
    }

    @Test
    public void SubmitForm_WithEmptySelection_ReturnsToSameForm() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/self-employed-confirmation")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void SubmitForm_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/self-employed-confirmation")
                .param("count", "1")
                .param("question.choice", "yolo")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

}
