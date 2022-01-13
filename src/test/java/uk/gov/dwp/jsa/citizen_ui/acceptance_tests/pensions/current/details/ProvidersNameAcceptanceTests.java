package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.pensions.current.details;

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
public class ProvidersNameAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    private static int count =  1;

    @Test
    public void GetProvidersNameForm_ReturnsProvidersNameForm() throws Exception {
        mockMvc.perform(get("/form/pensions/current/details/1/provider-name").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"string-form\"")));
    }

    @Test
    public void GivenValidProvidersName_ReturnTheNextPage() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/provider-name", count))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "my provider name")
                .param("count", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/pensions/current/details/1/provider-address")));
    }

    @Test
    public void GivenBlankProvidersName_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/provider-name", count))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void GivenProvidersNameLongerThan81_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/provider-name", count))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "NIxOdFxt1H9lzja9ClXjqsfmsIb4X67xVT08xF0CtSKCTGeyCBIzDdS7fxeBdBDA3aStVlkoWunK9NR89r")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    @Test
    public void GivenProvidersNameWithInvalidCharacters_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/provider-name", count))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "£56invalid")
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-error-summary")))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

}
