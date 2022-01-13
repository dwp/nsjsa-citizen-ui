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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
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
public class EducationCourseHoursAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetEducationCourseHoursForm_ReturnsEducationCourseHoursForm() throws Exception {
        mockMvc.perform(get("/form/education/course-hours").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"course-hours-form\"")));
    }

    @Test
    public void GivenValidEducationCourseHours_ReturnSuccessPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "30"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/education/course-duration")));
    }

    @Test
    public void GivenValidEducationCourseHoursDecimal_ReturnSuccessPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "30.5"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/education/course-duration")));
    }

    @Test
    public void GivenValidDecimalEducationCourseHours_ReturnSuccessPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "30.5"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/education/course-duration")));
    }

    @Test
    public void GivenEmptyEducationCourseHours_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.empty")));
    }

    @Test
    public void GivenInvalidEducationCourseHours_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.length.invalid")));
    }

    @Test
    public void GivenAlphasCharactersEducationCourseHours_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "rr"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("Hours spent on the course a week must be a number, like 30.5")));
    }

    @Test
    public void GivenNegativeCharactersEducationCourseHours_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "-9"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.invalid")));
    }

    @Test
    public void GivenFirstCharacterDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", ".11"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.invalid")));
    }

    @Test
    public void GivenLastCharacterDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "111."))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.length.invalid")));
    }

    @Test
    public void GivenTwoDigitsAfterDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "1.55"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("education.coursehours.invalid")));
    }

    @Test
    public void GivenAlphaInvalidFormatDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "d.55"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("Hours spent on the course a week must be a number, like 30.5")));
    }

    @Test
    public void GivenInvalidFormatDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "1..5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("Hours spent on the course a week must be a number, like 30.5")));
    }

    @Test
    public void GivenOnlyDecimal_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-hours")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("educationCourseHoursQuestion.courseHours", "."))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("Hours spent on the course a week must be a number, like 30.5")));
    }
}
