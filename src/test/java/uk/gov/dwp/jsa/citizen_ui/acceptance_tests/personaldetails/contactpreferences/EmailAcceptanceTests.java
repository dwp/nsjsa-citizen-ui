package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails.contactpreferences;


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

import javax.servlet.http.Cookie;


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
public class EmailAcceptanceTests {

    private static final String URL = "/form/personal-details/contact/email";
    private static final String JSA_COOKIE_LANGUAGE = "jsa_lang";
    private static final String WELSH_LOCALE_LANG_IDENTIFY = "cy";

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void GetEmailForm_ReturnsEmailForm() throws Exception {
        mockMvc.perform(get(URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"emailForm\"")));
    }

    @Test
    public void GivenValidEmail_ReturnSuccessPage() throws Exception {
        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "true")
                .param("emailStringQuestion.email", "my@test.com"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/bank-account")));
    }

    @Test
    public void GivenInValidEmail_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "true")
                .param("emailStringQuestion.email", "my+test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("contactpreferences.email.error.invalid")));
    }

    @Test
    public void GivenEmptyEmail_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "")
                .param("emailStringQuestion.email", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("contactpreferences.email.mandatory")));
    }

    @Test
    public void GivenMissingEmail_ReturnErrorPage() throws Exception {
        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "true")
                .param("emailStringQuestion.email", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("contactpreferences.email.error.invalid")));
    }

    @Test
    public void GivenInvalidEmailLength_ReturnErrorPage() throws Exception {
        String email = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@a.aa";

        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "true")
                .param("emailStringQuestion.email", email))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("contactpreferences.email.error.length")));
    }

    @Test
    public void GetWelshEmailForm_ReturnsEmailForm() throws Exception {
        mockMvc.perform(get(URL).with(csrf()).cookie(new Cookie(JSA_COOKIE_LANGUAGE, WELSH_LOCALE_LANG_IDENTIFY)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oes")));
    }



    @Test
    public void GivenWhiteSpaceInEmail_ThenItIsIgnored() throws Exception {
        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("emailStringQuestion.hasProvidedEmail", "true")
                .param("emailStringQuestion.email", " my@ test.com "))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/bank-account")));
    }
}
