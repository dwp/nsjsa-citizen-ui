package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.backdating;

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
public class HaveYouTravelledOutsideAcceptanceTests {
    private static final String HAVE_YOU_TRAVELLED_OUTSIDE_URL = "form/backdating/have-you-travelled-outside-england-wales-scotland";
    private static final String NEXT_PAGE = "/form/backdating/have-you-been-in-full-time-education";
    private static final String PAGE_TITLE = "Have you travelled outside of England, Wales or Scotland since";
    private static final String FOR_EXAMPLE_START_DATE_TEXT = "31 1 2019";
    private static final String FOR_EXAMPLE_END_DATE_TEXT = "31 1 2020";

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

    @Test
    public void getHaveYouTravelledOutside_returnsForm() throws Exception {
        mockMvc.perform(get("/form/backdating/have-you-travelled-outside-england-wales-scotland")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"travelled-outside-form\"")));
    }
    @Ignore
    @Test
    public void submitValidHaveYouTravelledOutside_returnsFullTimeEducation() throws Exception {

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
        mockMvc.perform(post("/form/backdating/have-you-travelled-outside-england-wales-scotland")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("haveYouTravelledOutsideQuestion.hasProvidedAnswer", "true")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.day", startDay)
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.month", startMonth)
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.year", startYear)
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.day", endDay)
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.month", endMonth)
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.year", endYear)
                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/backdating/have-you-been-in-full-time-education")));
    }
    @Test
    public void submitValidHaveYouTravelledOutside_NoSelected_returnsFulltimeEducation() throws Exception {

        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.WEEKS);

        Claim claim = new Claim();
        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(startDate);
        claimRepository.save(claim);

        mockMvc.perform(post("/form/backdating/have-you-travelled-outside-england-wales-scotland")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("haveYouTravelledOutsideQuestion.hasProvidedAnswer", "false")
                .with(csrf())
                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/backdating/have-you-been-in-full-time-education")));
    }

    @Test
    public void submitHaveYouTravelledOutUkYesNoNotSelected_returnsFormWithErrors() throws Exception {
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

        mockMvc.perform(post("/form/backdating/have-you-travelled-outside-england-wales-scotland")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("haveYouTravelledOutsideQuestion.hasProvidedAnswer", "")
                .with(csrf()).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.have.you.travelled.outside.mandatory")));
    }

    @Test
    public void submitHaveYouTravelledOutUkYesSelectedNoDatesEntered_returnsFormWithErrors() throws Exception {
        Claim claim = new Claim();
        LocalDate claimStartDate = LocalDate.now().minusDays(7);
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(4);

        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion(claimStartDate);
        claim.setClaimStartDateQuestion(startDateQuestion);
        claimRepository.save(claim);

        mockMvc.perform(post("/form/backdating/have-you-travelled-outside-england-wales-scotland")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("haveYouTravelledOutsideQuestion.hasProvidedAnswer", "true")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.day", "")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.month", "")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.startDate.year", "")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.day", "")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.month", "")
                .param("haveYouTravelledOutsideQuestion.dateRangeQuestion.endDate.year", "")
                .with(csrf()).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("backdating.have.you.travelled.outside.startdate.mandatory")))
                .andExpect(content().string(containsString("backdating.have.you.travelled.outside.enddate.mandatory")));
    }

}
