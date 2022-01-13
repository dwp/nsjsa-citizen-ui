package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.backdating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import static org.apache.commons.lang3.StringUtils.repeat;
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
public class HaveYouAskedForAdviceAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @Test
    public void getAskedForAdvice_returnsForm() throws Exception {
        mockMvc.perform(get("/form/backdating/have-you-asked-for-advice")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"asked-advice-form\"")));
    }

    @Test
    public void submitValidValue_returnsNationalInsurance() throws Exception {
        String value = "I am a valid value that should be sumitted.";

        mockMvc.perform(post("/form/backdating/have-you-asked-for-advice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("askedForAdviceQuestion.hasHadAdvice", "true")
                .param("askedForAdviceQuestion.value", value)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/nino")));
    }

    @Test
    public void submitNoToAskedForAdviceForm_returnsNationalInsurance() throws Exception {
        mockMvc.perform(post("/form/backdating/have-you-asked-for-advice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("askedForAdviceQuestion.hasHadAdvice", "false")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/nino")));
    }

    @Test
    public void submitValueWithSpecialCharacters_isAccepted() throws Exception {
        String value = "I am invalid /''':;90129";

        mockMvc.perform(post("/form/backdating/have-you-asked-for-advice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("askedForAdviceQuestion.hasHadAdvice", "true")
                .param("askedForAdviceQuestion.value", value)
                .with(csrf()))
                .andExpect(status().isFound());
    }

    @Test
    public void submitYesNoNotSelected_returnsErrorPage() throws Exception {

        mockMvc.perform(post("/form/backdating/have-you-asked-for-advice").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("askedForAdviceQuestion.hasHadAdvice", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.asked.for.advice.error.mandatory")));
    }

    @Test
    public void submitHasAskedForAdvicePage_WithGreaterThan600Character_returnsError() throws Exception {
        mockMvc.perform(post("/form/backdating/have-you-asked-for-advice")
                .with(csrf())
                .param("askedForAdviceQuestion.value", repeat("a", 601)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.asked.for.advice.error.max.chars")));
    }
}
