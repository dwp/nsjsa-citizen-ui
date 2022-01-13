package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails;

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
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.NinoController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
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
public class NinoFormAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    public ClaimRepository claimRepository;

    @Test
    public void GetNinoForm_ReturnsStringForm() throws Exception {
        mockMvc.perform(get("/form/nino").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"string-form\"")));
    }

    @Test
    public void GivenValidNino_ReturnsSuccessPage() throws Exception {
        Cookie cookie = mockMvc.perform(post("/form/nino")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question.value", "AA 12 34 56 A"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/personal-details")))
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        Optional<Claim> claimOptional = claimRepository.findById(cookie.getValue());
        StringQuestion ninoQuestion = (StringQuestion) claimOptional.get().get(NinoController.IDENTIFIER).get();
        assertThat(ninoQuestion.getValue(), is("AA123456A"));
    }

    @Test
    public void GivenInValidNino_ReturnsErrorPage() throws Exception {
        mockMvc.perform(post("/form/nino")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question.value", "abcdefgh1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("nino.form.error.invalid")));
    }

    @Test
    public void GivenBlankNino_ReturnsBlankError() throws Exception {
        mockMvc.perform(post("/form/nino")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question.value", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("nino.form.error.blank")));
    }
}
