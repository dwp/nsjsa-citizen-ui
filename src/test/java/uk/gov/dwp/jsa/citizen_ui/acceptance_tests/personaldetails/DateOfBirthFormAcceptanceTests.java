package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.UUID;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class DateOfBirthFormAcceptanceTests {

    private static final String INVALID_STRING = "I£$";
    private static final LocalDate VALID_DATE = LocalDate.now().minusYears(25);
    private String day = valueOf(VALID_DATE.getDayOfMonth());
    private String month = valueOf(VALID_DATE.getMonthValue());
    private String year = valueOf(VALID_DATE.getYear());
    private Cookie cookie = new Cookie(COOKIE_CLAIM_ID, "9999");
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
    public void GetDateOfBirth_ReturnsDOBForm() throws Exception {

        mockMvc.perform(get("/form/date-of-birth").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "id=\"date-of-birth\""
                )));
    }

    @Test
    public void GivenValidDateOfBirth_PostDOBForm_ReturnsSuccess()
            throws Exception {

        expectSuccessResultWithDateValues(day, month, year);
    }

    @Test
    public void GivenDayMonthYearIsOutOfBoundInteger_PostDOBForm_ReturnsError()
            throws Exception {
        String maxValue = valueOf(Long.MAX_VALUE);
        expectErrorMessageWithDateValues(maxValue, maxValue, maxValue, "Your date of birth must be a number");
    }

    @Test
    public void GivenDateOfBirthIsLessThan16Years_PostDOBForm_ReturnsError()
            throws Exception {
        LocalDate localDate = LocalDate.now().minusYears(12);
        String day = valueOf(localDate.getDayOfMonth());
        String month = valueOf(localDate.getMonthValue());
        String year = valueOf(localDate.getYear());
        expectErrorMessageWithDateValues(day, month, year, "You must be 16 or over to claim");
    }

    @Test
    public void GivenDateOfBirthIs17YearsBefore_PostDOBForm_ReturnsIneligibleForm() throws Exception {
        LocalDate localDate = LocalDate.now().minusYears(17);
        String day = valueOf(localDate.getDayOfMonth());
        String month = valueOf(localDate.getMonthValue());
        String year = valueOf(localDate.getYear());
        expectIneligibleUnder18PageWithDateValues(day, month, year);
    }

    @Test
    public void GivenDayIsNotNumeric_PostDOBForm_ReturnsError() throws Exception {
        expectErrorMessageWithDateValues(INVALID_STRING, INVALID_STRING,
                INVALID_STRING, "Your date of birth must be a number");
    }

    @Test
    public void GivenDateFieldsAreBlank_PostDOBForm_ReturnsError()
            throws Exception {
        expectErrorMessageWithDateValues(EMPTY, EMPTY, EMPTY, "Enter your date of birth");
    }

    @Test
    public void GivenInvalidDate_PostDOBForm_ReturnsError() throws Exception {
        expectNonExistingDateErrorMessage("30", "02", "1980");
    }

    @Test
    public void GivenDayMonthAndYearAreNegative_PostDOBForm_ReturnsError()
            throws Exception {
        expectNonExistingDateErrorMessage("-2", "-14", "-90");
    }

    @Test
    public void GivenDayAndMonthAreAboveValidRange_PostDOBForm_ReturnsError()
            throws Exception {
        expectNonExistingDateErrorMessage("32", "13", "1980");
    }

    @Test
    public void GivenDayMonthAndYearAreZero_PostDOBForm_ReturnsError()
            throws Exception {
        expectNonExistingDateErrorMessage("0", "0", "0");
    }

    @Test
    public void GivenIncompleteDateDAY_PostDOBForm_ReturnsError()
            throws Exception {
        expectIncompleteDateErrorMessage("", "1", String.valueOf(LocalDate.now().getYear()));
    }

    @Test
    public void GivenIncompleteMONTH_PostDOBForm_ReturnsError()
            throws Exception {
        expectIncompleteDateErrorMessage("1", "", String.valueOf(LocalDate.now().getYear()));
    }

    @Test
    public void GivenIncompleteYEAR_PostDOBForm_ReturnsError()
            throws Exception {
        expectIncompleteDateErrorMessage("1", "1", "");
    }

    @Test
    public void GivenDateOfBirthIsAbovePensionAge_PostDOBForm_ReturnsError()
            throws Exception {

        LocalDate now = LocalDate.now();
        MvcResult mvcResult = mockMvc.perform(
                post("/" + ClaimStartDateController.IDENTIFIER).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", valueOf(now.getDayOfMonth()))
                        .param("question.month", valueOf(now.getMonthValue()))
                        .param("question.year", valueOf(now.getYear())))
                .andReturn();

        cookie = mvcResult.getResponse().getCookie(COOKIE_CLAIM_ID);

        expectErrorMessageWithDateValues("6", "2", "1900",
                "You must be under State Pension age to claim");
    }

    @Test
    public void GivenDayAndMonthAreSingleDigit_PostDOBForm_ReturnsSuccess()
            throws Exception {
        expectSuccessResultWithDateValues("2", "3", "1980");
    }

    @Test
    public void GivenClaimIdDoesNotExists_CreatesNewClaimAndSetCookie()
            throws Exception {
        Cookie claimIdCookie = mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        assertNotNull(claimIdCookie);
        assertNotNull(claimIdCookie.getValue());
    }

    @Test
    public void GivenClaimIdExists_LoadClaimFromCache() throws Exception {

        Claim claim = new Claim();
        claimRepository.save(claim);

        Cookie claimIdCookie = mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year)
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
        String invalidClaimId = UUID.randomUUID().toString();
        Cookie claimIdCookie = mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year)
                        .cookie(new Cookie(COOKIE_CLAIM_ID, invalidClaimId)))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);
        assertNotNull(claimIdCookie);
        assertNotEquals(invalidClaimId, claimIdCookie.getValue());
    }

    private void expectErrorMessageWithDateValues(String day, String month, String year, String message)
            throws Exception {
        if (message == null) {
            message = "Your date of birth must be a real date";
        }
        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(cookie)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"date-of-birth\"")))
                .andExpect(content().string(containsString(message)))
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

    private void expectIneligibleUnder18PageWithDateValues(String day, String month, String year) throws Exception {
        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/personal-details/under18")));
    }

    private void expectSuccessResultWithDateValues(String day, String month, String year) throws Exception {
        assertEquals(0L, claimRepository.count());
        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isFound());
        assertEquals(1L, claimRepository.count());
    }

    private void expectNonExistingDateErrorMessage(String day, String month, String year) throws Exception {
        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(cookie)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your date of birth must be a real date")));
    }

    private void expectIncompleteDateErrorMessage(String day, String month, String year) throws Exception {
        String expectedFailedFieldPostFix = "";
        if (day.isEmpty()) expectedFailedFieldPostFix = " day";
        if (month.isEmpty()) expectedFailedFieldPostFix = " month";
        if (year.isEmpty()) expectedFailedFieldPostFix = " year";

        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(cookie)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Birth date must include" + expectedFailedFieldPostFix)));
    }
}
