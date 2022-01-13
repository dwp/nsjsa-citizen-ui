package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.currentwork;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
public class EmployersNameAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetEmployersNameForm_ReturnsEmployersNameForm() throws Exception {
        mockMvc.perform(get("/form/current-work/details/1/name").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"string-form\"")));
    }

    @Test
    public void GivenValidEmployersName_ReturnTheNextPage() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "my employers name")
                .param("count", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/current-work/details/1/address")));
    }

    @Test
    public void GivenInValidEmployersName_ReturnErrorPage() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void submitCurrentEmployersNameGreaterThan81Chars_showError() throws Exception {
        mockMvc.perform(post("/form/current-work/details/1/name")
                .with(csrf())
                .param("stringQuestion.value", "Charlie & Chocolate Factory Charlie & Chocolate Factory  is 82 characters in total")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("current.work.employers.name.too.many.char.error")));
    }



}
