package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.education;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
public class EducationCourseDurationAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");

    @Test
    public void getEducationDates_returnsForm() throws Exception {

        LocalDate today = LocalDate.now();
        String exampleStartDate = LocalDate.of(today.getYear(), 9, 1)
                .minusYears(1).format(formatter);
        today.minusYears(1).format(formatter);
        String exampleEndDate = today.format(formatter);

        mockMvc.perform(get("/form/education/course-duration")
                                .with(csrf())).andExpect(status().isOk())
                .andExpect(model().attribute("exampleStartDate", exampleStartDate))
                .andExpect(model().attribute("exampleEndDate", exampleEndDate))
                .andExpect(content().string(containsString("start-day")))
                .andExpect(content().string(containsString("end-day")));
    }


    @Test
    public void submitEducation_returnsSummary() throws Exception {

        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
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
                .andExpect(header().string("Location", is("/form/summary")));
    }

    @Test
    public void submitEducationDatesWithoutDates_returnsFormWithErrors() throws Exception {

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("education.courseduration.empty.start")))
                .andExpect(content().string(containsString("education.courseduration.empty.end")));
    }

    @Test
    public void submitEducationDatesWithNonExistingDate_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "32";
        String startMonth = "13";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "32";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("dateRange.startDate.day", startDay)
                                .param("dateRange.startDate.month", startMonth)
                                .param("dateRange.startDate.year", startYear)
                                .param("dateRange.endDate.day", endDay)
                                .param("dateRange.endDate.month", endMonth)
                                .param("dateRange.endDate.year", endYear)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Course start date must be a real date")))
                .andExpect(content().string(containsString("Course end date must be a real date")));
    }

    @Test
    public void submitEducationDatesWithAlphasDate_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "dd";
        String startMonth = "11";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "12";
        String endMonth = "dd";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Course start date must be a number")))
                .andExpect(content().string(containsString("Course end date must be a number")));
    }

    @Test
    public void submitEducationDatesWithIncompleteDate_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusMonths(1);
        LocalDate startDate = endDate.minusWeeks(1);

        String startDay = "32";
        String startMonth = "";
        String startYear = String.valueOf(startDate.getYear());
        String endDay = "";
        String endMonth = "13";
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("dateRange.startDate.day", startDay)
                .param("dateRange.startDate.month", startMonth)
                .param("dateRange.startDate.year", startYear)
                .param("dateRange.endDate.day", endDay)
                .param("dateRange.endDate.month", endMonth)
                .param("dateRange.endDate.year", endYear)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Course start date must include a month")))
                .andExpect(content().string(containsString("Course end date must include a day")));
    }

    @Test
    public void submitEducationDatesStartDateEqualsGreaterThanToday_returnsFormWithErrors() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
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
                .andExpect(content().string(containsString("education.courseduration.error.max.startdate")));
    }

    @Test
    public void submitEducationDatesStartDateMoreThanTenYearsAgo_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusYears(10);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
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
                .andExpect(content().string(containsString("education.courseduration.error.min.startdate")));
    }

    @Test
    public void submitEducationDatesEndDateMoreThanTenYears_returnsFormWithErrors() throws Exception {
        LocalDate endDate = LocalDate.now().plusYears(10).plusDays(1);
        LocalDate startDate = LocalDate.now();

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
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
                .andExpect(content().string(containsString("education.courseduration.error.max.enddate")));
    }

    @Test
    public void submitEducationDatesStartDateGreaterThanEndDate_returnsFormWithErrors() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = startDate.minusDays(1);

        String startDay = String.valueOf(startDate.getDayOfMonth());
        String startMonth = String.valueOf(startDate.getMonthValue());
        String startYear = String.valueOf(startDate.getYear());
        String endDay = String.valueOf(endDate.getDayOfMonth());
        String endMonth = String.valueOf(endDate.getMonthValue());
        String endYear = String.valueOf(endDate.getYear());

        mockMvc.perform(post("/form/education/course-duration").with(csrf())
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
                .andExpect(content().string(containsString("education.courseduration.error.startdate.after.enddate")));
    }
}
