package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.availability;

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
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class AvailableForInterviewConfirmationAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    public void GetAvailabilityConfirmationForm_ReturnsAvailabileForInterviewConfirmationForm() throws Exception {
        mockMvc.perform(get("/form/availability/available-for-interview")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("question.choice")));
    }

    @Test
    public void SubmitAvailabilityConfirmationForm_WithYesSelected_ReturnsAvailabiltyDatesForm() throws Exception {
        mockMvc.perform(post("/form/availability/available-for-interview")
                .param("question.choice", "true")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/availability/availability")));
    }

    @Test
    public void SubmitAvailabilityConfirmationForm_WithNoSelected_ReturnsSummaryPage() throws Exception {
        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(post("/form/availability/available-for-interview")
                .param("question.choice", "false")
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/summary")));
    }

    @Test
    @Ignore
    public void SubmitAvailabilityConfirmationForm_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/availability/available-for-interview")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You must select an option")));
    }

    @Test
    @Ignore
    public void SubmitAvailabilityConfirmationForm_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/availability/available-for-interview")
                .param("question.choice", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You must select an option")));
    }
}
