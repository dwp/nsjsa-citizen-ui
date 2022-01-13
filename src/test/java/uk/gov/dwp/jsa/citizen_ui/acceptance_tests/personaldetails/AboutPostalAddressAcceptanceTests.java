package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.personaldetails;

import org.hamcrest.Matchers;
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
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Country;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.NONE;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class AboutPostalAddressAcceptanceTests {
    public static final String FORM_PERSONAL_DETAILS_POSTAL_ADDRESS = "/form/personal-details/postal-address";
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
    public void GetPostalAddress_ReturnsPostalAddressForm() throws Exception {
        mockMvc.perform(get(FORM_PERSONAL_DETAILS_POSTAL_ADDRESS).with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"personal-details-postal-address\"")))
                .andExpect(model().attribute("countries", Matchers.is(Country.values())));
    }

    @Test
    public void GivenValidPostalAddress_PostForm_ReturnsSuccess() throws Exception {
        String line1 = "line 1";
        String line2 = "line 2";
        String townOrCity = "town or city";
        String postCode = "PO1 3AX";
        String country = "ENGLAND";
        mockMvc.perform(
                post(FORM_PERSONAL_DETAILS_POSTAL_ADDRESS).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("postalAddressQuestion.addressLine1", line1)
                        .param("postalAddressQuestion.addressLine2", line2)
                        .param("postalAddressQuestion.townOrCity", townOrCity)
                        .param("postalAddressQuestion.postCode", postCode))
                .andExpect(status().isFound());

        Claim claim = claimRepository.findAll().iterator().next();
        Step step = new Step("form/personal-details/postal-address", "", "", NONE);
        StepInstance stepInstance = new StepInstance(step, 0, false, false, false);
        PostalAddressQuestion question = (PostalAddressQuestion) claim.get(stepInstance).get();
        assertThat(question.getAddressLine1(), is(line1));
        assertThat(question.getAddressLine2(), is(line2));
        assertThat(question.getTownOrCity(), is(townOrCity));
        assertThat(question.getPostCode(), is(postCode));
    }

    @Test
    public void GivenWeHaveInvalidPostalAddressFields_PostForm_ShowsErrorMessage() throws Exception {
        String line1 = "";
        String line2 = "%^";
        String townOrCity = "town or,,city";
        String postCode = "invalid postcode";
        String country = "GREECE";
        mockMvc.perform(
                post(FORM_PERSONAL_DETAILS_POSTAL_ADDRESS).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("postalAddressQuestion.addressLine1", line1)
                        .param("postalAddressQuestion.addressLine2", line2)
                        .param("postalAddressQuestion.townOrCity", townOrCity)
                        .param("postalAddressQuestion.postCode", postCode))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("about.address.line1.blank")))
                .andExpect(content().string(containsString("about.address.town.error")))
                .andExpect(content().string(containsString("about.address.postcode.error")));
    }
}
