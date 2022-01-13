package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

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
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.DefaultStartDateController;

import javax.servlet.http.Cookie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class DefaultClaimStartDateAcceptanceTests {

    private static final String CLAIM_START_URL = "/" + DefaultStartDateController.IDENTIFIER;
    private static final String JSA_COOKIE_LANGUAGE = "jsa_lang";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GetClaimStartDate_ReturnsClaimStartDateForm() throws Exception {

        mockMvc.perform(get(CLAIM_START_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Do you want your application to start today?"
                )));
    }

    @Test
    public void GetClaimStartDate_ReturnsFormWithNonChangeableClaimStartDateWarning() throws Exception {

        mockMvc.perform(get(CLAIM_START_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-warning-text")))
                .andExpect(content().string(containsString("You cannot change the start date later in the application.")));
    }

    @Test
    public void GivenNoAnswerSelected_ErrorReturned() throws Exception {
        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString("Select yes if you want your application to start today")));
    }

    @Test
    public void GivenEnglishLanguage_ThenDefaultDateIsInWelsh() throws Exception {
        String currentMonth = DateTimeFormatter.ofPattern("MMMM").format(LocalDate.now());

        mockMvc.perform(get(CLAIM_START_URL).with(csrf())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(currentMonth)));

    }

    @Test
    public void GivenWelshLanguage_ThenDefaultDateIsInWelsh() throws Exception {

        String[] welshMonths = new String[] {
            "Ionawr", "Chwefror", "Mawrth", "Ebrill", "Mai",
                "Mehefin", "Gorffennaf", "Awst", "Medi", "Hydref",
                "Tachwedd", "Rhagfyr"
        };

        mockMvc.perform(get(CLAIM_START_URL).with(csrf())
                .with(csrf()).cookie(new Cookie(JSA_COOKIE_LANGUAGE, Constants.WELSH_LOCALE)))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(containsString(welshMonths[0]),
                        containsString(welshMonths[1]),
                        containsString(welshMonths[2]),
                        containsString(welshMonths[3]),
                        containsString(welshMonths[4]),
                        containsString(welshMonths[5]),
                        containsString(welshMonths[6]),
                        containsString(welshMonths[7]),
                        containsString(welshMonths[8]),
                        containsString(welshMonths[9]),
                        containsString(welshMonths[10]),
                        containsString(welshMonths[11]))));
    }
}
