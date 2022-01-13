package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.backdating;

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

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class WhyNotApplySoonerAcceptanceTests {
    private static final String WHY_NOT_APPLY_SOONER_URL = "/form/backdating/why-not-apply-sooner";
    private static final String NEXT_PAGE = "/form/backdating/have-you-been-in-paid-work-since";
    private static final String PAGE_TITLE =
            "Tell us why you did not apply for New Style Jobseeker’s Allowance before today";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

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
    public void getWhyNotApplySoonerPage_ReturnsForm() throws Exception {
        mockMvc.perform(get(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(PAGE_TITLE)));
    }

    @Test
    public void submitWhyNotApplySoonerPage_WithNoErrors_redirectsToNextPage() throws Exception {
        mockMvc.perform(post(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf())
                .param("stringQuestion.value", "Was unaware of the service"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", NEXT_PAGE));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void submitWhyNotApplySoonerPage_WithEmptyValues_returnsError() throws Exception {
        mockMvc.perform(post(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf())
                .param("stringQuestion.value", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.whynow.details.error.empty")));
    }

    @Test
    public void submitWhyNotApplySoonerPage_WithSpecialCharacters_isAccepted() throws Exception {
        mockMvc.perform(post(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf())
                .param("stringQuestion.value", "!£$%^&&"))
                .andExpect(status().isFound());
    }

    @Test
    public void submitWhyNotApplySoonerPage_WithGreaterThan600CharacterInDetailedReason_returnsError() throws Exception {
        mockMvc.perform(post(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf())
                .param("stringQuestion.value", repeat("a", 601)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.whynow.details.error.max.chars")));
    }

    @Test
    public void submitWhyNotApplySoonerPage_WithValidSpecialCharacters_returnsNextPage() throws Exception {
        mockMvc.perform(post(WHY_NOT_APPLY_SOONER_URL)
                .with(csrf())
                .param("stringQuestion.value", "First, I was unaware of the service, then..."))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", NEXT_PAGE));
    }
}
