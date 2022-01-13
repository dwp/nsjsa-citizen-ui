package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

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
import uk.gov.dwp.jsa.citizen_ui.Constants;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.dwp.jsa.citizen_ui.Constants.ALLOWED_LANG_CHANGE_URL;
import static uk.gov.dwp.jsa.citizen_ui.Constants.LANG_PARAM_NAME;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class IntroductionPageAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetEnglishLocaleByDefault() throws Exception {

        mockMvc.perform(get(ALLOWED_LANG_CHANGE_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Find out if you may be eligible for New Style Jobseeker"
                )));
    }

    @Test
    public void GetWelshLocale_whenClickSwitch() throws Exception {

        mockMvc.perform(get(ALLOWED_LANG_CHANGE_URL)
                .param(LANG_PARAM_NAME, "cy")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Darganfyddwch os allech fod yn gymwys am Lwfans Ceisio Gwaith Dull Newydd"
                )));
    }

    @Test
    public void GetWelshLocale_whenCookieIsCy_AndRandomPage() throws Exception {

        Cookie cookie = new Cookie(Constants.LANG_COOKIE_ID, "cy");
        mockMvc.perform(get("/form/date-of-birth")
                .cookie(cookie)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Beth yw eich dyddiad geni?"
                )));
    }
}
