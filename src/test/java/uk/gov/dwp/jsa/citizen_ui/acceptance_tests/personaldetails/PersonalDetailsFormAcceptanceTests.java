package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails;

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
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum.MR;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class PersonalDetailsFormAcceptanceTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ClaimRepository claimRepository;

    private static final String TITLE_ERROR_MESSAGE = "Select a title";
    private static final String FIRST_NAME_ERROR_MESSAGE = "First name must only include letters, full stops, hyphens, spaces and apostrophes";
    private static final String FIRST_NAME_BLANK_MESSAGE = "Enter a first name";
    private static final String FIRST_NAME_LAST_CHAR_MESSAGE = "First name must end with letters a to z";
    private static final String LAST_NAME_ERROR_MESSAGE = "Last name must only include letters, full stops, hyphens, spaces and apostrophes";
    private static final String LAST_NAME_BLANK_MESSAGE = "Enter a last name";
    private static final String LAST_NAME_LAST_CHAR_MESSAGE = "Last name must end with letters a to z";

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void GetPersonalDetails_ReturnsPersonalDetailsForm() throws Exception {
        mockMvc.perform(get("/form/personal-details").with(csrf())).andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"personal-details\"")));
    }

    @Test
    public void GivenValidPersonalDetails_PostForm_ReturnsSuccess() throws Exception {
        String title = "MR";
        String firstName = "S";
        String lastName = "D.ol'b-y ki";
        mockMvc.perform(post("/form/personal-details").with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title)
                            .param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                            .param("personalDetailsQuestion.lastNameQuestion.value", lastName)).andExpect(status().isFound());

        Claim claim = claimRepository.findAll().iterator().next();
        Step step = new Step("form/personal-details", "", "", Section.NONE);
        PersonalDetailsQuestion question = (PersonalDetailsQuestion) claim.get(new StepInstance(step, 0, false, false, false)).get();
        assertThat(question.getFirstNameQuestion().getValue(), is(firstName));
        assertThat(question.getLastNameQuestion().getValue(), is(lastName));
        assertThat(question.getTitleQuestion().getUserSelectionValue(), is(MR));
    }

    // Invalid Request
    @Test
    public void GivenEmptyPersonalDetails_PostForm_ShowsBlankErrorMessage() throws Exception {
        testAllBlank("", "", "");
    }

    @Test
    public void GivenNameWithInvalidCharacters_PostForm_ShowsErrorMessage() throws Exception {
        testLastCharInvalid("", "DAvid*", "DAvid(");
    }

    @Test
    public void GivenNameWithApostropheAtBeginning_PostForm_ShowErrorMessage() throws Exception {
        testAllInvalid("", "'David", "'Beckem");
    }

    @Test
    public void GivenNameWithApostropheAtEnd_PostForm_ShowAlternativeErrorMessage() throws Exception {
        testLastCharInvalid("", "David'", "Beckem'");
    }

    @Test
    public void GivenNameWithHyphenAndDotInEnd_PostForm_ShowsAlternativeErrorMessage() throws Exception {
        testLastCharInvalid("", "DAvid-", "Beckam.");
    }

    @Test
    public void GivenNameWithHyphenAndDotAtStart_PostForm_ShowsErrorMessage() throws Exception {
        testAllInvalid("", "-DAvid", ".Beckam");
    }

    @Test
    public void GivenNameWithConsecutiveHyphenAndDot_PostForm_ShowsErrorMessage() throws Exception {
        testAllInvalid("", "DA-.vid", "Bec''kam");
    }

    // Valid Requests
    @Test
    public void GivenFirstNameWithSpaceAtBeginning_PostForm_RedirectsToCorrectPage() throws Exception {
        testIsValid("MR", "   David", "Beckam");
    }

    @Test
    public void GivenFirstNameWithSpaceAtEnd_PostForm_RedirectsToCorrectPage() throws Exception {
        testIsValid("MR", "David   ", "Beckam");
    }

    @Test
    public void GivenLastNameWithSpaceAtBeginning_PostForm_RedirectsToCorrectPage() throws Exception {
        testIsValid("MR", "David", "   Beckam");
    }

    @Test
    public void GivenLastNameWithSpaceAtEnd_PostForm_RedirectsToCorrectPage() throws Exception {
        testIsValid("MR", "David", "Beckam   ");
    }

    @Test
    public void GivenFirstNameAndLastNameWithSpaceAtEndAndBeginning_PostForm_RedirectsToCorrectPage() throws Exception {
        testIsValid("MR", "   David   ", "   Beckam   ");
    }

    private void testAllBlank(final String title, final String firstName, final String lastName) throws Exception {
        mockMvc.perform(post("/form/personal-details").with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title).param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                .param("personalDetailsQuestion.lastNameQuestion.value", lastName)).andExpect(status().isOk())
                .andExpect(content().string(containsString(TITLE_ERROR_MESSAGE)))
                .andExpect(content().string(containsString(FIRST_NAME_BLANK_MESSAGE)))
                .andExpect(content().string(containsString(LAST_NAME_BLANK_MESSAGE)));
    }

    private void testAllInvalid(
        String title,
        String firstName,
        String lastName
    ) throws Exception {
        mockMvc.perform(post("/form/personal-details").with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title).param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                            .param("personalDetailsQuestion.lastNameQuestion.value", lastName)).andExpect(status().isOk())
            .andExpect(content().string(containsString(TITLE_ERROR_MESSAGE)))
            .andExpect(content().string(containsString(FIRST_NAME_ERROR_MESSAGE)))
            .andExpect(content().string(containsString(LAST_NAME_ERROR_MESSAGE)));
    }

    private void testLastCharInvalid(
            String title,
            String firstName,
            String lastName
    ) throws Exception {
        mockMvc.perform(post("/form/personal-details").with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title).param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                .param("personalDetailsQuestion.lastNameQuestion.value", lastName)).andExpect(status().isOk())
                .andExpect(content().string(containsString(TITLE_ERROR_MESSAGE)))
                .andExpect(content().string(containsString(FIRST_NAME_LAST_CHAR_MESSAGE)))
                .andExpect(content().string(containsString(LAST_NAME_LAST_CHAR_MESSAGE)));
    }

    private void testIsValid(String title, String firstName, String lastName) throws Exception {
        try {
            mockMvc.perform(post("/form/personal-details").with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title).param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                    .param("personalDetailsQuestion.lastNameQuestion.value", lastName)).andExpect(status().isFound())
                    .andExpect(view().name("redirect:/form/date-of-birth"));
        } catch(Exception exception) {
            System.out.println("Exception caught in - uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails");
            System.out.println(exception);
        }
    }
}
