package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

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
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingQuestion;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class JuryServiceDatesAcceptanceTests {
    private static final String FOR_EXAMPLE_START_DATE_TEXT = "31 1 2019";
    private static final String FOR_EXAMPLE_END_DATE_TEXT = "31 1 2020";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");

    @Test
    public void getJuryServiceDates_returnsForm() throws Exception {

        String exampleStartDate = LocalDate.now().minus(1, ChronoUnit.YEARS).format(formatter);
        String exampleEndDate = LocalDate.now().format(formatter);

        mockMvc.perform(get("/form/claim-start/jury-service/start-date")
                                .with(csrf())).andExpect(status().isOk())
                .andExpect(model().attribute("exampleStartDate", FOR_EXAMPLE_START_DATE_TEXT))
                .andExpect(model().attribute("exampleEndDate", FOR_EXAMPLE_END_DATE_TEXT))
                .andExpect(content().string(containsString("start-day")))
                .andExpect(content().string(containsString("end-day")));
    }

    @Test
    public void submitJuryServiceDatesWhenCurrentlyNotWorking_returnsPreviousEmployment() throws Exception {

        Claim claim = new Claim();
        AreYouWorkingQuestion areYouWorkingQuestion = new AreYouWorkingQuestion();
        areYouWorkingQuestion.setAreYouWorking(false);
        claim.setAreYouWorkingQuestion(areYouWorkingQuestion);
        claimRepository.save(claim);

        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.WEEKS);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());
        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .param("count", "1")
                                .with(csrf())
                                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/current-work/are-you-working")));
    }

    @Test
    public void submitJuryServiceDatesWhenCurrentlyWorking_returnsCurrentEmployment() throws Exception {

        Claim claim = new Claim();
        AreYouWorkingQuestion areYouWorkingQuestion = new AreYouWorkingQuestion();
        areYouWorkingQuestion.setAreYouWorking(true);
        claim.setAreYouWorkingQuestion(areYouWorkingQuestion);
        claimRepository.save(claim);

        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.WEEKS);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .param("count", "1")
                                .with(csrf())
                                .cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/current-work/are-you-working")));
    }

    @Test
    public void submitJurServiceDatesWithoutDates_returnsFormWithErrors() throws Exception {

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter the jury service start date")))
                .andExpect(content().string(containsString("Enter the jury service end date")));
    }

    @Test
    public void submitJurServiceDatesWithNonExistingDates_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.WEEKS);

        String startDay = "32";
        String startMonth = "13";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "32";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Jury service start date must be a real date")))
                .andExpect(content().string(containsString("Jury service end date must be a real date")));
    }

    @Test
    public void submitJurServiceDatesWithIncompleteDates_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "32";
        String startMonth = "";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Jury service start date must include a month")))
                .andExpect(content().string(containsString("Jury service end date must include a day")));
    }

    @Test
    public void submitJurServiceDatesStartDateMoreThanAYearAgo_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.YEARS);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .param("count", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("juryservice.error.min.startdate")));
    }

    @Test
    public void submitJurServiceDatesEndDateMoreThanToday_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        LocalDate startDate = endDate.minus(1, ChronoUnit.MONTHS);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .param("count", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("juryservice.error.max.enddate")));
    }

    @Test
    public void submitJurServiceDatesStartDateGreaterThanEndDate_returnsFormWithErrors() throws Exception {
        LocalDate startDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        LocalDate endDate = startDate.minus(1, ChronoUnit.DAYS);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/claim-start/jury-service/start-date").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .param("count", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("juryservice.error.startdate.after.enddate")));
    }
}
