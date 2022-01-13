package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.currentwork;

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
public class HasCurrentWorkAcceptanceTests {

    private static final String FORM_URL = "/form/current-work/are-you-working";
    private static final String NEXT_URL = "/form/current-work/details/1/is-work-paid";
    private static final String ALT_NEXT_URL = "/form/previous-employment/has-previous-work";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    public void get_ReturnsForm() throws Exception {
        mockMvc.perform(get(FORM_URL)
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("question.choice")))
                .andExpect(content().string(containsString("govuk-hint")));
    }

    @Test
    public void post_WithYesSelected_ReturnsIsItPaidForm() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .param("question.choice", "true")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(NEXT_URL)));
    }

    @Test
    public void post_WithNoSelected_ReturnsHasPreviousWork() throws Exception {
        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(post(FORM_URL)
                .param("question.choice", "false")
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(ALT_NEXT_URL)));
    }

    @Test
    public void post_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void post_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .param("question.choice", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
