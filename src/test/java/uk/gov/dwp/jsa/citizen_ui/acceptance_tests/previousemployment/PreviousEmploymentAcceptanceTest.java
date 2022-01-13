package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class PreviousEmploymentAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void a_post_to_has_previous_work_should_reload_with_error_message_if_parameters_are_invalid()
    throws Exception {
        mockMvc.perform(post("/form/previous-employment/has-previous-work")
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("govuk-error-summary")))
            .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void has_previous_work_should_return_the_start_end_form_if_true() throws Exception {
        mockMvc.perform(post("/form/previous-employment/has-previous-work")
            .param("question.choice", "true")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", is("/form/previous-employment/employer-details/1/dates")));
    }

    @Test
    public void has_previous_work_should_redirect_to_the_next_section_if_false() throws Exception {
        mockMvc.perform(post("/form/previous-employment/has-previous-work")
            .param("question.choice", "false")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", is("/form/outside-work/has-outside-work")));
    }
}
