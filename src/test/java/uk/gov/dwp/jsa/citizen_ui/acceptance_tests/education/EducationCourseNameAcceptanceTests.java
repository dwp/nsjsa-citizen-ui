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
public class EducationCourseNameAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetEducationCourseNameForm_ReturnsEducationCourseNameForm() throws Exception {
        mockMvc.perform(get("/form/education/course-name").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"string-form\"")));
    }

    @Test
    public void GivenValidEducationCourseName_ReturnSuccessPage() throws Exception {
        mockMvc.perform(post("/form/education/course-name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "Computer Science & Mathematics"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/education/place")));
    }

    @Test
    public void GivenInValidEducationCourseName_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/education/course-name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

}
