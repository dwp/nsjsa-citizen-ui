package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.pensions.current.details;

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
public class PensionIncreaseAcceptanceTests {

    private static final String GET_URL = "/form/pensions/current/details/%s/pension-increase";
    private static final String POST_URL = "/form/pensions/current/details/%s/pension-increase";
    private static final String NEXT_URL = "/form/pensions/current/%s/has-another-pension";
    private static final String ALT_NEXT_URL = "/form/pensions/current/details/1/increase-date";
    int count = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    public void Get_ReturnsForm() throws Exception {
        mockMvc.perform(get(String.format(GET_URL, count))
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("question.choice")));
    }

    @Test
    public void Post_WithNoSelected_ReturnsAltNextAndChoiceSaved() throws Exception {
        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(post(String.format(POST_URL, count))
                .param("question.choice", "true")
                .param("count", String.valueOf(count))
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(String.format(ALT_NEXT_URL, count))));
    }

    @Test
    public void Post_WithYesSelected_ReturnsNextAndChoiceSaved() throws Exception {
        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(post(String.format(POST_URL, count))
                .param("question.choice", "false")
                .param("count", String.valueOf(count))
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(String.format(NEXT_URL, count))));
    }

    @Test
    public void Post_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format(POST_URL, count))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void Post_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format(POST_URL, count))
                .param("question.choice", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
