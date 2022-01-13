package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.currentwork;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class HoursAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    private String NEXT_STEP = "/form/current-work/details/1/choose-payment";
    private static final String FORM_URL = "/form/current-work/details/is-work-paid";

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
        assertThat(claimRepository.count(), is(0L));
    }


    @Test
    public void getHoursForm_returnsHoursForm_Volunteering() throws Exception {
        mockMvc.perform(get("/form/current-work/details/1/hours").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"hours-form\"")));
    }
    @Test
    public void getHoursForm_returnsHoursForm_Paid() throws Exception {
        mockMvc.perform(get("/form/current-work/details/1/hours").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"hours-form\"")));
    }

    @Test
    public void givenValidWorkHours_returnNextPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "30"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        equalTo("/form/current-work/details/1/self-employed-confirmation")));
    }

    @Test
    public void givenEmptyHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.length.invalid")));
    }

    @Test
    public void givenOverMaxLimitHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());;
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.invalid")));
    }

    @Test
    public void givenNegativeHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "-5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.invalid")));
    }

    @Test
    public void givenNullHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"error-summary-title\"")));
    }

    @Test
    public void givenZeroHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.invalid.zero.hours")));
    }

    @Test
    public void givenDecimalValueOfZeroHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "0.0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.invalid")));
    }

    @Test
    public void givenDecimalValueHours_returnErrorPage() throws Exception {
        Claim claim = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        claim.setId(claimId);
        claimRepository.save(claim);
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        mockMvc.perform(post("/form/current-work/details/1/hours")
                .with(csrf())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .param("hoursQuestion.hours", "37.5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("currentwork.hours.invalid")));
    }


    private static Claim generateClaimWithTypeOfWorkAndCount(Integer count, TypeOfWork typeOfWork) {
        Claim claim = new Claim();
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(typeOfWork);
        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+count+"/how-often-paid",
                Section.CURRENT_WORK);
        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);
        claim.save(stepInstance, typeOfWorkOption, Optional.empty());
        return claim;
    }
}
