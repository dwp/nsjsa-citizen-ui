package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.pensions.current.details;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ProviderAddressAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClaimRepository claimRepository;
    private static int count = 1;

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
    public void GetProviderAddress_ReturnsProviderAddressForm() throws Exception {
        mockMvc.perform(get("/form/pensions/current/details/1/provider-address").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"address\"")));
    }

    @Test
    public void GivenValidProviderAddress_PostForm_ReturnsPensionPaymentFrequency() throws Exception {
        String line1 = "line 1";
        String line2 = "line 2";
        String townOrCity = "town or city";
        String postCode = "PO1 3AX";
        mockMvc.perform(
                post(String.format("/form/pensions/current/details/%s/provider-address", count))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("addressQuestion.addressLine1", line1)
                        .param("addressQuestion.addressLine2", line2)
                        .param("addressQuestion.townOrCity", townOrCity)
                        .param("addressQuestion.postCode", postCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/pensions/current/details/1/payment-frequency"));
    }

    @Test
    public void GivenValidProviderAddressWithNoPostcode_PostForm_ReturnsSuccess() throws Exception {
        String line1 = "line 1";
        String line2 = "line 2";
        String townOrCity = "town or city";
        mockMvc.perform(
                post(String.format("/form/pensions/current/details/%s/provider-address", count))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("addressQuestion.addressLine1", line1)
                        .param("addressQuestion.addressLine2", line2)
                        .param("addressQuestion.townOrCity", townOrCity))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenInvalidAddressFields_PostForm_ShowsErrorMessage() throws Exception {
        String line1 = "";
        String line2 = "%^";
        String townOrCity = "town or,,city";
        String postCode = "invalid postcode";
        mockMvc.perform(
                post(String.format("/form/pensions/current/details/%s/provider-address", count))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("count", "1")
                        .param("addressQuestion.addressLine1", line1)
                        .param("addressQuestion.addressLine2", line2)
                        .param("addressQuestion.townOrCity", townOrCity)
                        .param("addressQuestion.postCode", postCode))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("about.address.line1.blank")))
                .andExpect(content().string(containsString("about.address.town.error")))
                .andExpect(content().string(containsString("about.address.postcode.error")));
    }
}
