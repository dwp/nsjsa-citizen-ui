package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails;

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
public class AboutPostalAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getPostal_ReturnsPostalForm() throws Exception {
        mockMvc.perform(get("/form/personal-details/address-is-it-postal")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"boolean-form\"")));
    }

    @Test
    public void submitPostalFormYesSelected_ReturnsThePostalAddressForm() throws Exception {
        mockMvc.perform(post("/form/personal-details/address-is-it-postal")
                .param("question.choice", "true")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/personal-details/postal-address")));
    }

    @Test
    public void submitPostalFormNoSelected_ReturnsBankAccountPage() throws Exception {
        mockMvc.perform(post("/form/personal-details/address-is-it-postal")
                .param("question.choice", "false")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/personal-details/contact/telephone")));
    }

    @Test
    public void submitPostalFormEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post("/form/personal-details/address-is-it-postal")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }
}
