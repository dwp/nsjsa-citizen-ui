package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment.employerdetails;

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
public class EmployersNameAcceptanceTests {

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
    public void getEmployersName_returnsFrom() throws Exception {
        mockMvc.perform(get("/form/previous-employment/employer-details/1/name")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"string-form\"")));
    }

    @Test
    public void submitEmployersNameWithNoErrors_redirectsToNextPage() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/2/name")
                .with(csrf())
                .param("stringQuestion.value", "Charlie & the Chocolate '.-&")
                .param("count", "2"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/previous-employment/employer-details/2/address"));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void submitEmployersNameEmpty_showError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/name")
                .with(csrf())
                .param("stringQuestion.value", "")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("previousemployment.employerdetails.name.empty.error")));
        assertThat(claimRepository.count(), is(0L));
    }

    @Test
    public void submitEmployersNameWithInvalidSpecialCharacter_showError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/name")
                .with(csrf())
                .param("stringQuestion.value", "Charlie & Chocolate!")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("previousemployment.employerdetails.name.invalid.error")));
        assertThat(claimRepository.count(), is(0L));
    }

    @Test
    public void submitEmployersNameGreaterThan81Chars_showError() throws Exception {
        mockMvc.perform(post("/form/previous-employment/employer-details/1/name")
                .with(csrf())
                .param("stringQuestion.value", "Charlie & Chocolate Factory Charlie & Chocolate Factory  is 82 characters in total")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("previousemployment.employerdetails.name.too.many.char.error")));
        assertThat(claimRepository.count(), is(0L));
    }
}
