package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.backdating;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
public class HaveYouBeenUnableToWorkDueToIllnessAcceptanceTests {
    private static final String UNABLE_TO_WORK_DUE_TO_ILLNESS_URL = "/form/backdating/have-you-been-unable-to-work-due-to-illness";
    private static final String PAGE_TITLE = "Have you been unable to work because of illness";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");

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
    public void getUnableToWorkDueToIllness_ReturnsForm() throws Exception {
        mockMvc.perform(get(UNABLE_TO_WORK_DUE_TO_ILLNESS_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(PAGE_TITLE)));
    }

    @Test
    public void postUnableToWorkDueToIllness_WithNoSelected_redirectsToNextPage() throws Exception {
        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.WEEKS);

        Claim claim = new Claim();
        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(startDate);
        claimRepository.save(claim);

        mockMvc.perform(post("/form/backdating/have-you-been-unable-to-work-due-to-illness")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("unableToWorkDueToIllnessQuestion.hasProvidedAnswer", "false")
                .with(csrf())
                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/backdating/have-you-travelled-outside-england-wales-scotland")));
    }

    @Ignore
    @Test
    public void postUnableToWorkDueToIllness_WithYesSelected_redirectsToNextPage() throws Exception {
        Claim claim = new Claim();
        LocalDate claimStartDate = LocalDate.now().minusDays(7);
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(4);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(claimStartDate);
        claim.setClaimStartDateQuestion(startDateQuestion);
        claim.setAnswer(ClaimStartDateController.IDENTIFIER, startDateQuestion);
        claimRepository.save(claim);
        mockMvc.perform(post(UNABLE_TO_WORK_DUE_TO_ILLNESS_URL)
                .with(csrf())
                .param("unableToWorkDueToIllnessQuestion.hasProvidedAnswer", "true")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.day", startDay)
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.month", startMonth)
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.year", startYear)
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.day", endDay)
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.month", endMonth)
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.year", endYear)
                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/backdating/have-you-travelled-outside-england-wales-scotland"));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void submitUnableToWorkDueToIllnessYesNoNotSelected_returnsFormWithErrors() throws Exception {
        Claim claim = new Claim();
        LocalDate claimStartDate = LocalDate.now().minusDays(7);
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(4);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(claimStartDate);
        claim.setClaimStartDateQuestion(startDateQuestion);
        claimRepository.save(claim);

        mockMvc.perform(post(UNABLE_TO_WORK_DUE_TO_ILLNESS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("unableToWorkDueToIllnessQuestion.hasProvidedAnswer", "")
                .with(csrf()).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.unable.to.work.due.to.illness.mandatory")));
    }

    @Test
    public void submitUnableToWorkDueToIllnessYesSelectedNoDatesEntered_returnsFormWithErrors() throws Exception {
        Claim claim = new Claim();
        LocalDate claimStartDate = LocalDate.now().minusDays(7);
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(4);

        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(claimStartDate);
        claim.setClaimStartDateQuestion(startDateQuestion);
        claimRepository.save(claim);

        mockMvc.perform(post(UNABLE_TO_WORK_DUE_TO_ILLNESS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("unableToWorkDueToIllnessQuestion.hasProvidedAnswer", "true")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.day", "")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.month", "")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.startDate.year", "")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.day", "")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.month", "")
                .param("unableToWorkDueToIllnessQuestion.dateRangeQuestion.endDate.year", "")
                .with(csrf()).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.unable.to.work.due.to.illness.startdate.mandatory")))
                .andExpect(content().string(containsString("backdating.unable.to.work.due.to.illness.enddate.mandatory")));
    }
}
