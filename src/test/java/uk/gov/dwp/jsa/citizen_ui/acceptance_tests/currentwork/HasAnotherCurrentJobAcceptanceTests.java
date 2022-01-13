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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
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
public class HasAnotherCurrentJobAcceptanceTests {

    private static final String GET_URL = "/form/current-work/%s/has-another-job";
    private static final String POST_URL = "/form/current-work/1/has-another-job";
    private static final String ALT_NEXT_URL = "/form/current-work/details/%s/is-work-paid";
    private static final String NEXT_URL = "/form/previous-employment/has-previous-work";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    private int count = 1;
    private static final String CLAIM_ID = "1234";

    @Test
    public void get_ReturnsForm() throws Exception {
        int count = 1;
        mockMvc.perform(get(String.format(GET_URL, count))
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("question.choice")));
    }

    @Test
    public void post_WithYesSelected_ReturnsAltNext() throws Exception {
        mockMvc.perform(post(POST_URL)
                .param("question.choice", "true")
                .param("count", String.valueOf(count))
                .cookie(new Cookie(COOKIE_CLAIM_ID, CLAIM_ID))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(String.format(ALT_NEXT_URL, count + 1))));
    }

    @Test
    public void post_WithNoSelected_ReturnsNext() throws Exception {
        mockMvc.perform(post(POST_URL)
                .param("question.choice", "false")
                .param("count", String.valueOf(count))
                .cookie(new Cookie(COOKIE_CLAIM_ID, CLAIM_ID))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(String.format(NEXT_URL, count))));
    }

    @Test
    public void post_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(POST_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void post_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post(POST_URL)
                .param("question.choice", "test")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void getMaxJobUrl_ReturnsExpectedView() throws Exception {
        mockMvc.perform(get("/form/current-work/max-jobs")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You have entered a maximum of 4 jobs")));
    }

    @Test
    public void get_RequestInWelsh_ReturnsFormWithAlternativeWelshText() throws Exception {
        int count = 1;
        mockMvc.perform(get(String.format(GET_URL, count))
                .with(csrf()).cookie(new Cookie("jsa_lang", "cy"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Oes")));
    }

    @Test
    public void postInWelsh_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(POST_URL)
                .with(csrf()).cookie(new Cookie("jsa_lang", "cy")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oes")));
    }
}
