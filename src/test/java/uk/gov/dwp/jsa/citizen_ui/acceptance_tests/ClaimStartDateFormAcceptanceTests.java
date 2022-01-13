package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.After;
import org.junit.Before;
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
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.lang.String.valueOf;
import static java.time.LocalDate.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ClaimStartDateFormAcceptanceTests {

    private static final String INVALID_STRING = "I£$";
    private static final String CLAIM_START_URL = "/" + ClaimStartDateController.IDENTIFIER;
    private static final LocalDate TODAY_DATE = now();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void GetClaimStartDate_ReturnsClaimStartDateForm() throws Exception {

        mockMvc.perform(get(CLAIM_START_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "id=\"claim-start-date\""
                )));
    }

    @Test
    public void GivenDayMonthYearIsOutOfBoundInteger_PostClaimStartForm_ReturnsError()
            throws Exception {
        String maxValue = valueOf(Long.MAX_VALUE);
        expectErrorMessageWithDateValues(maxValue, maxValue, maxValue, null);
    }

    @Test
    public void GivenDayIsNotNumeric_PostClaimStartForm_ReturnsError() throws Exception {
        expectErrorMessageWithDateValues(INVALID_STRING, INVALID_STRING,
                INVALID_STRING, "The date you want your application to start must be a real date");
    }

    @Test
    public void GivenDateFieldsAreBlank_PostClaimStartForm_ReturnsError()
            throws Exception {
        expectErrorMessageWithDateValues(EMPTY, EMPTY, EMPTY, "Enter the date you want your application to start");
    }

    @Test
    public void GivenDayAndMonthAreAboveValidRange_PostClaimStartForm_ReturnsError()
            throws Exception {
        expectErrorMessageWithDateValues("32", "13", "1980", "");
    }

    @Test
    public void GivenDayMonthAndYearAreNegative_PostClaimStartForm_ReturnsError()
            throws Exception {
        expectErrorMessageWithDateValues("-2", "-14", "-90", "The date you want your application to start must be a real date");
    }

    @Test
    public void GivenInvalidDate_PostClaimStartForm_ReturnsError() throws Exception {
        expectErrorMessageWithDateValues("30", "02", "1980", "The date you want your application to start must be a real date");
    }

    @Test
    public void GivenDayMonthAndYearAreZero_PostClaimStartForm_ReturnsError()
            throws Exception {
        expectErrorMessageWithDateValues("0", "0", "0", "The date you want your application to start must be a real date");
    }

    @Test
    public void GivenDateIsMoreThan13WeeksOld_PostClaimStartForm_ReturnsError()
            throws Exception {
        LocalDate localDate = TODAY_DATE.minusWeeks(14);
        expectErrorMessageWithDateValues(valueOf(localDate.getDayOfMonth()),
                valueOf(localDate.getMonthValue()), valueOf(localDate.getYear()),
               "The date you want your application to start must be in the past 3 months");
    }

    @Test
    public void GivenDateIsTomorrow_PostClaimStartForm_ReturnsError()
            throws Exception {
        LocalDate localDate = TODAY_DATE.plusDays(1);
        expectErrorMessageWithDateValues(valueOf(localDate.getDayOfMonth()),
                valueOf(localDate.getMonthValue()), valueOf(localDate.getYear()),
                "The date you want your application to start must not be in the future");
    }

    @Test
    public void GivenDayAndMonthAreWithinRange_PostClaimStartForm_ReturnsSuccess()
            throws Exception {
        LocalDate localDate = TODAY_DATE.minusWeeks(13);
        expectSuccessResultWithDateValues(valueOf(localDate.getDayOfMonth()),
                valueOf(localDate.getMonthValue()), valueOf(localDate.getYear()));
    }

    @Test
    public void GivenClaimIdDoesNotExists_CreatesNewClaimAndSetCookie()
            throws Exception {

        String day = valueOf(TODAY_DATE.getDayOfMonth());
        String month = valueOf(TODAY_DATE.getMonthValue());
        String year = valueOf(TODAY_DATE.getYear());
        Cookie claimIdCookie = mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        assertNotNull(claimIdCookie);
        assertNotNull(claimIdCookie.getValue());
    }

    @Test
    public void GivenSpamBot_redirectToErrorPage() throws Exception {
        String day = valueOf(now().getDayOfMonth());
        String month = valueOf(now().getMonthValue());
        String year = valueOf(now().getYear());

        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year)
                        .param("dateOfBirth","anyValue")
                        .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/error/404.html"));
    }


    @Test
    public void GivenClaimIdExists_LoadClaimFromCache() throws Exception {
        String day = valueOf(now().getDayOfMonth());
        String month = valueOf(now().getMonthValue());
        String year = valueOf(now().getYear());

        Claim claim = new Claim();
        claimRepository.save(claim);

        Cookie claimIdCookie = mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year)
                        .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        assertNotNull(claimIdCookie);
        assertEquals(claim.getId(), claimIdCookie.getValue());
    }

    @Test
    public void GivenWrongClaimIdExists_CreatesNewClaim() throws Exception {
        String day = valueOf(now().getDayOfMonth());
        String month = valueOf(now().getMonthValue());
        String year = valueOf(now().getYear());

        String invalidClaimId = UUID.randomUUID().toString();
        Cookie claimIdCookie = mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year)
                        .cookie(new Cookie(COOKIE_CLAIM_ID, invalidClaimId)))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        assertNotNull(claimIdCookie);
        assertNotEquals(invalidClaimId, claimIdCookie.getValue());
    }

    private void expectErrorMessageWithDateValues(String day, String month, String year, String errorMessage)
            throws Exception {
        if (errorMessage == null) {
            errorMessage = "The date you want your application to start must be a real date";
        }
        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"claim-start-date\"")))
                .andExpect(content().string(containsString("govuk-form-group--error")))
                .andExpect(content().string(containsString(errorMessage)));
    }

    private void expectSuccessResultWithDateValues(String day, String month, String year) throws Exception {
        assertEquals(0L, claimRepository.count());
        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year))
                .andExpect(status().isFound());
        assertEquals(1L, claimRepository.count());
    }

    @Test
    public void expectSuccessRedirectToBackdating() throws Exception{
        assertEquals(0L, claimRepository.count());
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String day = valueOf(yesterday.getDayOfMonth());
        String month = valueOf(yesterday.getMonthValue());
        String year = valueOf(yesterday.getYear());

        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/backdating/were-you-available-for-work"));
    }

    @Test
    public void expectSuccessRedirectToNino() throws Exception {
        assertEquals(0L, claimRepository.count());
        String day = valueOf(TODAY_DATE.getDayOfMonth());
        String month = valueOf(TODAY_DATE.getMonthValue());
        String year = valueOf(TODAY_DATE.getYear());

        mockMvc.perform(
                post(CLAIM_START_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", day)
                        .param("question.month", month)
                        .param("question.year", year))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/nino"));
    }


}
