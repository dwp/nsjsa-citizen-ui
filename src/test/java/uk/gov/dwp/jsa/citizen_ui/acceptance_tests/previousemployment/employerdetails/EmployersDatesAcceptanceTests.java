package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment.employerdetails;

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
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.EmployersDetails;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.lang.Integer.valueOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class EmployersDatesAcceptanceTests {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d M yyyy");

    private static final String IDENTIFIER = "form/previous-employment/employer-details/dates";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @Test
    public void getEmploymentDates_returnsForm() throws Exception {

        LocalDate today = LocalDate.now();
        String exampleStartDate = today.minusMonths(1).format(formatter);
        String exampleEndDate = today.format(formatter);

        mockMvc.perform(get("/form/previous-employment/employer-details/1/dates")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(model().attribute("exampleStartDate", exampleStartDate))
                .andExpect(model().attribute("exampleEndDate", exampleEndDate))
                .andExpect(content().string(containsString("start-day")))
                .andExpect(content().string(containsString("end-day")));
    }


    @Test
    public void submitEmploymentDates_returnsReasonEnded() throws Exception {

        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/1/why-end")));
    }

    @Test
    public void submitEmploymentDatesWithoutDates_returnsFormWithErrors() throws Exception {

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter the employment start date")))
                .andExpect(content().string(containsString("Enter the employment end date")));
    }

    @Test
    public void submitEmploymentDatesWithIncompleteDates_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "32";
        String startMonth = "";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Previous employment start date must include a month")))
                .andExpect(content().string(containsString("Previous employment end date must include a day")));
    }

    @Test
    public void submitEmploymentDatesWithEndDateMoreThan6MonthsAgo_returnsFormWithErrors() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate sevenMonthsAgoStartDate = now.minusMonths(7);
        LocalDate sixMonthsAndOneDayAgoEndDate = now.minusMonths(6).minusDays(1);

        String startDay = String.valueOf(sevenMonthsAgoStartDate.getDayOfMonth());
        String startMonth = String.valueOf(sevenMonthsAgoStartDate.getMonthValue());
        String startYear = String.valueOf(sevenMonthsAgoStartDate.getYear());
        String endDay = String.valueOf(sixMonthsAndOneDayAgoEndDate.getDayOfMonth());
        String endMonth = String.valueOf(sixMonthsAndOneDayAgoEndDate.getMonthValue());
        String endYear = String.valueOf(sixMonthsAndOneDayAgoEndDate.getYear());

        String formattedSixMonthsAndOneDayAgo = sixMonthsAndOneDayAgoEndDate.format(formatter);
        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Employment end date must be after " + formattedSixMonthsAndOneDayAgo)));
    }

    @Test
    public void submitEmploymentDatesWithNonExistingDate_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "32";
        String startMonth = "13";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "32";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Employment start date must be a real date")))
                .andExpect(content().string(containsString("Employment end date must be a real date")));
    }

    @Test
    public void submitEmploymentDatesStartDateLessThan01_01_1950_returnsFormWithErrors() throws Exception {
        LocalDate startDate = LocalDate.of(1950, 1, 1).minusDays(1);
        LocalDate endDate = LocalDate.now().minusMonths(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Employment start date must be in the past")));
    }

    @Test
    public void submitEmploymentDatesStartAndEndDatesInTheFuture_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().plusDays(2);
        LocalDate startDate = LocalDate.now().plusDays(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Employment start date must be in the past")))
                .andExpect(content().string(containsString("Employment end date must be in the past")));
    }

    @Test
    public void submitEmploymentDatesStartDateGreaterThanEndDate_returnsFormWithErrors() throws Exception {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = startDate.minusYears(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/1/dates").with(csrf())
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
                .andExpect(content().string(containsString("Employment start date must not be after the end date")));
    }

    @Test
    public void submitSecondEmploymentDates_addsDates() throws Exception {
        Claim claim = new Claim();
        EmploymentDurationQuestion employmentDurationQuestion = new EmploymentDurationQuestion();
        DateQuestion startDate1 = new DateQuestion();
        startDate1.setDay(1);
        startDate1.setMonth(1);
        startDate1.setYear(1950);
        employmentDurationQuestion.setStartDate(startDate1);
        DateQuestion endDate1 = new DateQuestion();
        endDate1.setDay(1);
        endDate1.setMonth(1);
        endDate1.setYear(1951);
        employmentDurationQuestion.setEndDate(endDate1);
        Step step = new Step(IDENTIFIER, "", "", Section.NONE);
        StepInstance stepInstance1 = new StepInstance(step, 1, false, false, false);
        claim.save(stepInstance1, employmentDurationQuestion, Optional.empty());
        claimRepository.save(claim);


        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/2/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .param("count", "2")
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/2/why-end")));

        claim = claimRepository.findById(claim.getId()).orElse(null);
        assertNotNull(claim);

        StepInstance stepInstance2 = new StepInstance(step, 2, false, false, false);
        employmentDurationQuestion = (EmploymentDurationQuestion) claim.get(stepInstance2).get();
        assertNotNull(employmentDurationQuestion);

        DateQuestion startDateResult = employmentDurationQuestion.getStartDate();
        assertNotNull(startDateResult);
        assertEquals(valueOf(startDay), startDateResult.getDay());
        assertEquals(valueOf(startMonth), startDateResult.getMonth());
        assertEquals(valueOf(startYear), startDateResult.getYear());

        DateQuestion endDateResult = employmentDurationQuestion.getEndDate();
        assertNotNull(endDateResult);
        assertEquals(valueOf(endDay), endDateResult.getDay());
        assertEquals(valueOf(endMonth), endDateResult.getMonth());
        assertEquals(valueOf(endYear), endDateResult.getYear());
    }

    @Test
    public void submitThirdEmploymentDatesWhenSecondNotExists_addsDatesAsSecond() throws Exception {
        Claim claim = new Claim();
        EmployersDetails employersDetails = new EmployersDetails();
        DateQuestion startDate1 = new DateQuestion();
        startDate1.setDay(1);
        startDate1.setMonth(1);
        startDate1.setYear(1950);
        employersDetails.setStartDate(startDate1);
        DateQuestion endDate1 = new DateQuestion();
        endDate1.setDay(1);
        endDate1.setMonth(1);
        endDate1.setYear(1951);
        employersDetails.setEndDate(endDate1);
        claim.getPreviousEmployment().updateEmployerDetails(1, employersDetails);
        claimRepository.save(claim);


        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/3/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .param("count", "3")
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/3/why-end")));
        claim = claimRepository.findById(claim.getId()).orElse(null);
        assertNotNull(claim);
        Step step = new Step(IDENTIFIER, "", "", Section.NONE);
        EmploymentDurationQuestion question = (EmploymentDurationQuestion) claim.get(new StepInstance(step, 3, false, false, false)).get();
        assertNotNull(question);
        DateQuestion startDateResult = question.getStartDate();
        assertNotNull(startDateResult);
        assertEquals(valueOf(startDay), startDateResult.getDay());
        assertEquals(valueOf(startMonth), startDateResult.getMonth());
        assertEquals(valueOf(startYear), startDateResult.getYear());
        DateQuestion endDateResult = question.getEndDate();
        assertNotNull(endDateResult);
        assertEquals(valueOf(endDay), endDateResult.getDay());
        assertEquals(valueOf(endMonth), endDateResult.getMonth());
        assertEquals(valueOf(endYear), endDateResult.getYear());
    }

    @Test
    public void submitSecondEmploymentDatesWhenSecondExists_updatesSecond() throws Exception {
        Claim claim = new Claim();

        DateQuestion startDate1 = new DateQuestion();
        startDate1.setDay(1);
        startDate1.setMonth(1);
        startDate1.setYear(1950);

        DateQuestion endDate1 = new DateQuestion();
        endDate1.setDay(1);
        endDate1.setMonth(1);
        endDate1.setYear(1951);

        EmploymentDurationQuestion employmentDurationQuestion = new EmploymentDurationQuestion();
        employmentDurationQuestion.setStartDate(startDate1);
        employmentDurationQuestion.setEndDate(endDate1);

        Step step = new Step("form/previous-employment/employer-details/dates", "", "", Section.NONE);
        StepInstance stepInstance1 = new StepInstance(step, 1, false, false, false);

        claim.save(stepInstance1, employmentDurationQuestion, Optional.empty());

        DateQuestion startDate2 = new DateQuestion();
        startDate2.setDay(1);
        startDate2.setMonth(1);
        startDate2.setYear(1960);
        DateQuestion endDate2 = new DateQuestion();
        endDate2.setDay(1);
        endDate2.setMonth(1);
        endDate2.setYear(1961);
        employmentDurationQuestion = new EmploymentDurationQuestion();
        employmentDurationQuestion.setStartDate(startDate2);
        employmentDurationQuestion.setEndDate(endDate2);

        step = new Step(IDENTIFIER, "", "", Section.NONE);
        StepInstance stepInstance2 = new StepInstance(step, 2, false, false, false);
        claim.save(stepInstance2, employmentDurationQuestion, Optional.empty());

        claimRepository.save(claim);

        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/2/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .param("count", "2")
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/2/why-end")));
        claim = claimRepository.findById(claim.getId()).orElse(null);
        assertNotNull(claim);

        EmploymentDurationQuestion question = (EmploymentDurationQuestion) claim.get(stepInstance2).get();
        DateQuestion startDateResult = question.getStartDate();

        assertNotNull(startDateResult);
        assertEquals(valueOf(startDay), startDateResult.getDay());
        assertEquals(valueOf(startMonth), startDateResult.getMonth());
        assertEquals(valueOf(startYear), startDateResult.getYear());
        DateQuestion endDateResult = question.getEndDate();
        assertNotNull(endDateResult);
        assertEquals(valueOf(endDay), endDateResult.getDay());
        assertEquals(valueOf(endMonth), endDateResult.getMonth());
        assertEquals(valueOf(endYear), endDateResult.getYear());
    }

    @Test
    public void submitFifthEmploymentDates_returnsError() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/previous-employment/employer-details/5/dates").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .param("count", "5")
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }
}
