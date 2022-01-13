package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.otherbenefits;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class OtherBenefitDetailsAcceptanceTests {

    private static final String OTHER_BENEFITS_DETAILS_URL = "/form/other-benefits/details";

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
    public void getOtherBenefitDetails_ReturnsForm() throws Exception {
        mockMvc.perform(get(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"text-area\"")));
    }

    @Test
    public void submitOtherBenefitDetails_WithNoErrors_redirectsToNextPage() throws Exception {
        mockMvc.perform(post(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf())
                .param("stringQuestion.value", "Other Sample Benefits"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/claim-start/jury-service/have-you-been"));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void submitOtherBenefitDetails_WithEmptyValues_returnsError() throws Exception {
        mockMvc.perform(post(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf())
                .param("stringQuestion.value", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("other.benefits.details.error.empty")));
    }

    @Test
    public void submitOtherBenefitDetails_WithSpecialCharacters_isAccepted() throws Exception {
        mockMvc.perform(post(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf())
                .param("stringQuestion.value", "!£$%^&&"))
                .andExpect(status().isFound());
    }

    @Test
    public void submitOtherBenefitDetails_WithGreaterThan100CharacterInDetailedReason_returnsError() throws Exception {
        mockMvc.perform(post(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf())
                .param("stringQuestion.value", repeat("a", 101)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You have entered too many characters")));
    }

    @Test
    public void submitOtherBenefitDetails_WithValidSpecialCharacters_returnsNextPage() throws Exception {
        mockMvc.perform(post(OTHER_BENEFITS_DETAILS_URL)
                .with(csrf())
                .param("stringQuestion.value", "ESA, Carer's Allowance"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/claim-start/jury-service/have-you-been"));
    }

}
