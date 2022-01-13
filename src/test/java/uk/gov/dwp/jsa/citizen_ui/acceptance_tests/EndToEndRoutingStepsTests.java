package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;


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
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.PersonalDetails;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.NONE;
import static uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum.MR;
import static uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum.MRS;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class EndToEndRoutingStepsTests {
    public static final String LOCATION = "Location";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    @Ignore
    public void AQuestionHasBeenCompletedWhenWeGoBackAPage() throws Exception {
        //GIVEN I have completed the forms with the following values
        Integer day = 2;
        Integer month = 7;
        Integer year = 2018;
        String nino = "AA 12 34 56 A";
        TitleEnum title = MR;
        String firstName = "David";
        String lastName = "Beckham";
        Claim claim = createClaim(day, month, year, nino, title, firstName, lastName);
        claimRepository.save(claim);

        //WHEN I go back to date of birth form
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        getRequest("/form/date-of-birth", cookie)
                //THEN I can see the values I have submitted
                .andExpect(model().attribute("form", hasProperty(
                        "dateOfBirthQuestion", hasProperty("day", equalTo(day)))))
                .andExpect(model().attribute("form", hasProperty(
                        "dateOfBirthQuestion", hasProperty("month", equalTo(month)))))
                .andExpect(model().attribute("form", hasProperty(
                        "dateOfBirthQuestion", hasProperty("year", equalTo(year)))));

        //WHEN I go back to nino form
        getRequest("/form/nino", cookie)
                //THEN I can see the values I have submitted
                .andExpect(model().attribute("form",
                        hasProperty("question",
                                hasProperty("value", equalTo(nino)))));

        //WHEN I go back to nino form
        getRequest("/form/personal-details", cookie)
                //THEN I can see the values I have submitted
                .andExpect(model().attribute("personalDetailsForm", hasProperty(
                        "personalDetailsQuestion", hasProperty("titleQuestion", hasProperty("userSelectionValue", equalTo(title))))))
                .andExpect(model().attribute("personalDetailsForm", hasProperty(
                        "personalDetailsQuestion", hasProperty("firstNameQuestion", hasProperty("value", equalTo(firstName))))))
                .andExpect(model().attribute("personalDetailsForm", hasProperty(
                        "personalDetailsQuestion", hasProperty("lastNameQuestion", hasProperty("value", equalTo(lastName))))));

        //WHEN I go back to declaration form
        getRequest("/form/declaration", cookie)
                //THEN I can see the values I have submitted
                .andExpect(model().attribute("form", hasProperty("question",
                        hasProperty("agreed",
                                equalTo(true)))));
    }

    @Test
    @Ignore
    public void whenIAmendAQuestionIEnsureThatItUpdated() throws Exception {
        //GIVEN I have completed the forms with the following values
        Integer day = 2;
        Integer month = 7;
        Integer year = 2018;
        Integer updatedYear = 1980;
        String nino = "AA 12 34 56 A";
        String updatedNino = "AB 12 34 56 A";
        TitleEnum title = MR;
        String firstName = "Beckham";
        String lastName = "David";
        TitleEnum updatedTitle = MRS;
        String updatedFirstName = "Victoria";
        String updatedLastName = "Adams";
        Boolean welshContact = false;
        Boolean welshSpeech = false;
        String addrLine1 = "33 The Street";
        String addrLine2 = "The Valley";
        String townOrCity = "The Town";
        String postCode = "P05 2CO";
        Claim claim = createClaim(day, month, year, nino, title, firstName, lastName);
        claimRepository.save(claim);

        //WHEN I post the new date of birth form and nino form
        Cookie cookie = new Cookie(COOKIE_CLAIM_ID, claim.getId());
        postDateOfBirth(cookie, day.toString(), month.toString(), updatedYear.toString());
        postNino(cookie, updatedNino);
        postPersonalDetails(cookie, updatedTitle, updatedFirstName, updatedLastName);
        postLanguagePreference(cookie, welshContact.toString(), welshSpeech.toString());
        postAddress(cookie, addrLine1, addrLine2, townOrCity, postCode);

        //THEN I can see the updated values in summary page
        getRequest("/form/summary", cookie)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Check your answers")));
    }

    @Test
    public void ensureThatICanGoBackToTheSummaryPageWhenIPostFromEditableSinglePage() throws Exception {
        whenIGoFromTheSummaryToAEditableSinglePage();
        thenIExpectToGoBackToTheSummaryFromTheEditableSinglePage();
    }

    @Test
    public void ensureThatICanGoBackToTheSummaryPageWhenIPostFromTheLastEditableSectionPage() throws Exception {
        whenIGoFromTheSummaryToTheLastPageInAEditableSection();
        thenIExpectToGoBackToTheSummaryFromTheLastPageEditableSection();
    }

    private void whenIGoFromTheSummaryToAEditableSinglePage() throws Exception {
        mockMvc.perform(get("/form/date-of-birth")
                .param("edit","SINGLE")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"edit\" id=\"edit\" value=\"SINGLE\">")));
    }

    private void thenIExpectToGoBackToTheSummaryFromTheEditableSinglePage() throws Exception {
        mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", "1")
                        .param("dateOfBirthQuestion.month", "1")
                        .param("dateOfBirthQuestion.year", "2000")
                        .param("edit", "SINGLE"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/summary?edit=SINGLE"));
    }

    private void whenIGoFromTheSummaryToTheLastPageInAEditableSection() throws Exception {
        mockMvc.perform(get("/form/other-benefits/details")
                .param("edit","SECTION")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"edit\" id=\"edit\" value=\"SECTION\">")));
    }

    private void thenIExpectToGoBackToTheSummaryFromTheLastPageEditableSection() throws Exception {
        mockMvc.perform(post("/form/other-benefits/details")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("stringQuestion.value", "Other Sample Benefits")
                .param("edit", "SECTION"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/summary?edit=SECTION"));
    }

    /**
     * Brett Perry 4 March this method replaces the existing method commented out below to exclude the Marital
     * Status Question.
     */
    private Claim createClaim(Integer day, Integer month, Integer year, String nino, TitleEnum title, String firstName,
                              String lastName) {
        Claim claim = new Claim();
        PersonalDetails personalDetails = new PersonalDetails();

        DateOfBirthQuestion dateOfBirthQuestion = new DateOfBirthQuestion();
        dateOfBirthQuestion.setDay(day);
        dateOfBirthQuestion.setMonth(month);
        dateOfBirthQuestion.setYear(year);
        Step step = new Step("form/date-of-birth", "", "", NONE);
        claim.save(
                new StepInstance(step, 0, true, false, false),
                dateOfBirthQuestion,
                Optional.empty()
        );

        NinoQuestion ninoQuestion = new NinoQuestion();
        ninoQuestion.setValue(nino);
        step = new Step("form/nino", "", "", NONE);
        claim.save(new StepInstance(step, 0, false, false, false),
                ninoQuestion, Optional.empty());

        DeclarationQuestion declarationQuestion = new DeclarationQuestion();
        declarationQuestion.setAgreed(true);
        step = new Step("form/declaration", "", "", NONE);
        claim.save(
                new StepInstance(step, 0, false, false, false),
                declarationQuestion,
                Optional.empty()
        );

        TitleQuestion titleQuestion = new TitleQuestion();
        titleQuestion.setUserSelectionValue(title);

        NameStringTruncatedQuestion firstNameQuestion = new NameStringTruncatedQuestion();
        firstNameQuestion.setValue(firstName);

        NameStringTruncatedQuestion lastNameQuestion = new NameStringTruncatedQuestion();
        lastNameQuestion.setValue(lastName);

        PersonalDetailsQuestion personalDetailsQuestion = new PersonalDetailsQuestion(titleQuestion, firstNameQuestion, lastNameQuestion);
        step = new Step("form/personal-details", "", "", NONE);
        claim.save(
                new StepInstance(step, 0, false, false, false),
                personalDetailsQuestion, Optional.empty());
        claim.setPersonalDetails(personalDetails);
        claimRepository.save(claim);
        return claim;
    }

    private ResultActions getRequest(String s, Cookie... cookies) throws Exception {
        return mockMvc.perform(get(s).with(csrf()).cookie(cookies));
    }

    private String postNino(Cookie claimIdCookie, String nino) throws Exception {
        return mockMvc.perform(
                post("/form/nino").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.value", nino)
                        .cookie(claimIdCookie))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String postPersonalDetails(Cookie claimIdCookie, TitleEnum title, String firstName,
                                       String lastName) throws Exception {
        return mockMvc.perform(
                post("/form/personal-details").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("personalDetailsQuestion.titleQuestion.userSelectionValue", title.name())
                        .param("personalDetailsQuestion.firstNameQuestion.value", firstName)
                        .param("personalDetailsQuestion.lastNameQuestion.value", lastName)
                        .cookie(claimIdCookie))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String postDateOfBirth(Cookie claimIdCookie, String day, String month, String year) throws Exception {
        return mockMvc.perform(
                post("/form/date-of-birth").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dateOfBirthQuestion.day", day)
                        .param("dateOfBirthQuestion.month", month)
                        .param("dateOfBirthQuestion.year", year)
                        .cookie(claimIdCookie))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String postLanguagePreference(Cookie claimIdCookie, String welshContact, String welshSpeech) throws Exception {
        return mockMvc.perform(
                post("/form/personal-details/language-preference").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("languagePreferenceQuestion.welshContact", welshContact)
                        .param("languagePreferenceQuestion.welshSpeech", welshSpeech)
                        .cookie(claimIdCookie))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String postAddress(
            Cookie claimIdCookie,
            String addrLine1,
            String addrLine2,
            String townOrCity,
            String postCode
    ) throws Exception {
        return mockMvc.perform(
                post("/form/personal-details/address").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("addressQuestion.addressLine1", addrLine1)
                        .param("addressQuestion.addressLine2", addrLine2)
                        .param("addressQuestion.townOrCity", townOrCity)
                        .param("addressQuestion.postCode", postCode)
                        .cookie(claimIdCookie))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }
}
