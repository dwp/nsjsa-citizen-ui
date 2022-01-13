package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.After;
import org.junit.Before;
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
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimWarningDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ClaimWarningDateControllerAcceptanceTest {

    private static final String CLAIM_WARNING_URL = "/" + ClaimWarningDateController.IDENTIFIER;
    private static final String RESET_URL = "/form/warning/claim-start-date/reset";
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
    public void GetClaimWarningPage_ReturnsClaimWarningPage() throws Exception {

        mockMvc.perform(get(CLAIM_WARNING_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "id=\"claim-warning\""
                )));
    }

    @Test
    public void ResetClaim_ReturnClaimStartPage() throws Exception {

        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(get(RESET_URL)
                .cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId()))
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/form/claim-start")));
        assertFalse(claimRepository.findById(claim.getId()).isPresent());
    }
}
