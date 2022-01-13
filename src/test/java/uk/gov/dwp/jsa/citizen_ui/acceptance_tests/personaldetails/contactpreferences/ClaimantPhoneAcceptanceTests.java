package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails.contactpreferences;

import org.junit.Before;
import org.junit.Ignore;
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
public class ClaimantPhoneAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @Test
    public void getContactPhone_returnsForm() throws Exception {
        mockMvc.perform(get("/form/personal-details/contact/telephone")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"phone-number-form\"")));
    }

    @Test
    public void submitValidContactPhone_returnsEmailConfirmationPage() throws Exception {
        String phone = "01(244) 12-345";

        mockMvc.perform(post("/form/personal-details/contact/telephone").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("phoneQuestion.hasProvidedPhoneNumber", "true")
                .param("phoneQuestion.phoneNumber", phone)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/personal-details/contact/email")));
    }

    @Test
    public void submitNoToPhoneNumber_returnsEmailConfirmationPage() throws Exception {
        String phone = "01(244) 12-345";

        mockMvc.perform(post("/form/personal-details/contact/telephone").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("phoneQuestion.hasProvidedPhoneNumber", "false")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/personal-details/contact/email")));
    }

    @Test
    public void submitInValidContactPhone_returnsErrorPage() throws Exception {
        String phone = "01(244) 12-34^522";

        mockMvc.perform(post("/form/personal-details/contact/telephone").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("phoneQuestion.hasProvidedPhoneNumber", "true")
                .param("phoneQuestion.phoneNumber", phone)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("contactpreferences.phone.invalid")));
    }
}
