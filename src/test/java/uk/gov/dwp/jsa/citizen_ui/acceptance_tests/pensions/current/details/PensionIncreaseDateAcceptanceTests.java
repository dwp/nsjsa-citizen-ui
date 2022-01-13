package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.pensions.current.details;

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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
public class PensionIncreaseDateAcceptanceTests {

    private static final String FORM_URL = "/form/pensions/current/details/%s/increase-date";
    private static final String NEXT_URL = "/form/pensions/current/1/has-another-pension";
    private static final String PARAM_NAME = "multipleOptionsQuestion.userSelectionValue";
    int count = 1;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void get_ReturnsForm() throws Exception {
        mockMvc.perform(get("/form/pensions/current/details/1/increase-date")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("multipleOptionsQuestion.option")));
    }

    @Test
    public void post_WithJanuarySelected_ReturnsPensionIncreaseFrequencyForm() throws Exception {
        mockMvc.perform(post(String.format(FORM_URL, count))
                .param("multipleOptionsQuestion.userSelectionValue", "JANUARY")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(NEXT_URL)));
    }

    @Test
    public void post_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format(FORM_URL, count))
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Select when your pension or annuity will increase")));
    }

    @Test
    public void post_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format(FORM_URL, count))
                .param(PARAM_NAME, "invalid")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
