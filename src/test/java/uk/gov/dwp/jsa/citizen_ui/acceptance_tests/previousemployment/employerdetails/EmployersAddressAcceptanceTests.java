package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.previousemployment.employerdetails;

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
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
public class EmployersAddressAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
        assertThat(claimRepository.count(), is(0L));
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void GetEmployerAddress_ReturnsEmployerAddressForm() throws Exception {
        mockMvc.perform(get("/form/previous-employment/employer-details/1/address").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"employer-details-address\"")));
    }

    @Test
    public void GivenValidEmployerAddress_PostForm_ReturnsSuccess() throws Exception {
        String line1 = "line 1";
        String line2 = "line 2";
        String townOrCity = "town or city";
        String postCode = "PO1 3AX";
        mockMvc.perform(
                post("/form/previous-employment/employer-details/1/address")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("employersAddressQuestion.addressLine1", line1)
                        .param("employersAddressQuestion.addressLine2", line2)
                        .param("employersAddressQuestion.townOrCity", townOrCity)
                        .param("employersAddressQuestion.postCode", postCode))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenValidEmployerAddressWithNoPostcode_PostForm_ReturnsSuccess() throws Exception {
        String line1 = "line 1";
        String line2 = "line 2";
        String townOrCity = "town or city";
        mockMvc.perform(
                post("/form/previous-employment/employer-details/1/address")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("employersAddressQuestion.addressLine1", line1)
                        .param("employersAddressQuestion.addressLine2", line2)
                        .param("employersAddressQuestion.townOrCity", townOrCity))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenInvalidAddressFields_PostForm_ShowsErrorMessage() throws Exception {
        String line1 = "";
        String line2 = "%^";
        String townOrCity = "town or,,city";
        String postCode = "invalid postcode";
        mockMvc.perform(
                post("/form/previous-employment/employer-details/1/address")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("employersAddressQuestion.addressLine1", line1)
                        .param("employersAddressQuestion.addressLine2", line2)
                        .param("employersAddressQuestion.townOrCity", townOrCity)
                        .param("employersAddressQuestion.postCode", postCode))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("about.address.line1.blank")))
                .andExpect(content().string(containsString("about.address.town.error")))
                .andExpect(content().string(containsString("about.address.postcode.error")));
    }
}
