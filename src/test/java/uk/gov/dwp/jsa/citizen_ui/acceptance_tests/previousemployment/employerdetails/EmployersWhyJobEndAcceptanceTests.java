package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment.employerdetails;

import org.apache.commons.lang3.StringUtils;
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
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
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
public class EmployersWhyJobEndAcceptanceTests {

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
    public void getWhyJobEnd_ReturnsForm() throws Exception {
        mockMvc.perform(get("/form/previous-employment/employer-details/1/why-end")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"why-end\"")));
    }

    @Test
    public void submitWhyJobEnd_WithNoErrors_redirectsToNextPage() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "REDUNDANCY")
                .param("count", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/previous-employment/employer-details/1/name"));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void submitWhyJobEnd_WithEmptyValues_returnsError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"why-end\"")));
    }

    @Test
    public void submitWhyJobEnd_WithInvalidReason_returnsError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "Dislike")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("There is a problem")));
    }

    @Test
    public void submitWhyJobEnd_WithSpecialCharacterInDetailedReason_isAccepted() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "OTHER")
                .param("whyJobEndQuestion.detailedReason", "Difficult & Manager!")
                .param("count", "1"))
                .andExpect(status().isFound());
    }

    @Test
    public void submitWhyJobEnd_WithGreaterThan200CharacterInDetailedReason_returnsError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "OTHER")
                .param("whyJobEndQuestion.detailedReason", StringUtils.repeat("a", 201))
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("previousemployment.employerdetails.too.many.char.error")));
    }

    @Test
    public void submitWhyJobEndedWithCommaCharacterInDetailedReason_returnsNextPage() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/why-end")
                .with(csrf())
                .param("whyJobEndQuestion.whyJobEndedReason", "OTHER")
                .param("whyJobEndQuestion.detailedReason", "Difficult, Manager")
                .param("count", "1"))
                .andExpect(status().isFound())
                .andExpect(
                        header().string("Location", "/form/previous-employment/employer-details/1/name"));
        assertThat(claimRepository.count(), is(1L));
    }
}
