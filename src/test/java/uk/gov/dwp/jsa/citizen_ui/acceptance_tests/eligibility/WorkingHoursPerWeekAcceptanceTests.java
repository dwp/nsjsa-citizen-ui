package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.eligibility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class WorkingHoursPerWeekAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    @Test
    public void GivenGetHoursWorkingPerWeekQuestion_ShouldDirectToExpectedQuestionForm() throws Exception {
        mockMvc.perform(
                get("/form/eligibility/working-over"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("multipleOptionsQuestion.userSelectionValue")));
    }

    @Test
    public void GivenSubmitHoursWorkingPerWeekQuestion_ShouldShowErrorIfNotFilledIn() throws Exception {
        mockMvc.perform(
                post("/form/eligibility/working-over")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void GivenPostHoursWorkingPerWeekQuestionWithoutUkResidence_ShouldDirectToExpectedQuestionForm() throws Exception {
        Claim claim = generateClaimWithResidence(false);
        claim.setId(claimId);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/eligibility/working-over")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("multipleOptionsQuestion.userSelectionValue", "WORKING_MORE_THAN_16_HOURS"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        equalTo("/form/eligibility/working-over-residence/working-over/ineligible")));
    }

    @Test
    public void GivenPostHoursWorkingPerWeekQuestionWithUkResidence_ShouldDirectToExpectedQuestionForm() throws Exception {
        Claim claim = generateClaimWithResidence(true);
        claim.setId(claimId);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/eligibility/working-over")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("multipleOptionsQuestion.userSelectionValue", "WORKING_MORE_THAN_16_HOURS"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        equalTo("/form/eligibility/working-over/ineligible")));
    }

    @Test
    public void GivenPostEligibleHoursWorkingPerWeekQuestionWithUkResidence_ShouldDirectToExpectedQuestionForm() throws Exception {
        Claim claim = generateClaimWithResidence(true);
        claim.setId(claimId);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/eligibility/working-over")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("multipleOptionsQuestion.userSelectionValue", "WORKING_LESS_THAN_16_HOURS"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        equalTo("/form/eligibility/eligible")));
    }

    private Claim generateClaimWithResidence(boolean isUkResident) {
        Claim claim = new Claim();
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(isUkResident);
        claim.setResidenceQuestion(residenceQuestion);
        return claim;
    }
}
