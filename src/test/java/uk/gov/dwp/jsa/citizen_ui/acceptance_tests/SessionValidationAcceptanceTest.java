package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.adaptors.enums.ClaimType;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {App.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class SessionValidationAcceptanceTest {

    private static final String CITIZEN_COOKIES_POLICY_URL = "/cookies-policy";
    private static final String HOME_URL = "/";
    private static final String CLAIMANT_URL = "/claimant";
    private static final String INIT_CLAIMANT_URL = "/init-claim";
    private static final String CLAIMANT_CONFIRMATION_URL = "/claimant-confirmation";
    private static final String FORM_URL = "/form/eligibility/residence";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    public void givenRequestToHomePage_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        mockMvc.perform(get(HOME_URL).with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Session time out"))));
    }

    @Test
    public void givenRequestToCookiePolicyPage_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        mockMvc.perform(get(CITIZEN_COOKIES_POLICY_URL).with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Session time out"))));
    }

    @Test
    public void givenRequestToClaimantPage_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        mockMvc.perform(get(CLAIMANT_URL).with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Session time out"))));
    }

    @Test
    public void givenRequestToInitClaimantPage_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        mockMvc.perform(get(INIT_CLAIMANT_URL)
                .param("claimType", ClaimType.NEW_CLAIM.toString())
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));
    }

    @Test
    public void givenRequestToClaimantConfirmationPage_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        mockMvc.perform(get(CLAIMANT_CONFIRMATION_URL).cookie(new Cookie(COOKIE_CLAIM_ID, UUID.randomUUID().toString())))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Session time out"))));
    }

    @Test
    public void givenRequestToAFormPageWithNoSession_WhenRequestIsProcessed_InterceptionShouldRedirectToExpiredSession() throws Exception {
        mockMvc.perform(get(FORM_URL)).andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error/session-timeout"));
    }

    @Test
    public void givenRequestToAFormPageWithSession_WhenRequestIsProcessed_InterceptionDoesNotInterfereAndPageIsReturned() throws Exception {
        final Claim claim = new Claim();
        claim.setId(UUID.randomUUID().toString());
        claimRepository.save(claim);

        mockMvc.perform(get(FORM_URL).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Session time out"))));
    }

    @Test
    public void givenRequestToAFormPageWithExpiredSession_WhenRequestIsProcessed_InterceptionShouldRedirectToExpiredSession() throws Exception {
        final Claim claim = new Claim();
        claim.setId(UUID.randomUUID().toString());
        claim.setClaimantLatestActivity(LocalDateTime.now().minusDays(1));
        claimRepository.save(claim);

        mockMvc.perform(get(FORM_URL).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error/session-timeout"));
    }

    @Test
    public void givenRequestToAFormPageWithInvalidSession_WhenRequestIsProcessed_InterceptionShouldRedirectToExpiredSession() throws Exception {
        mockMvc.perform(get(FORM_URL).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error/session-timeout"));
    }

    @Test
    public void givenRequestToAFormPageWithNonExistentSession_WhenRequestIsProcessed_InterceptionShouldRedirectToExpiredSession() throws Exception {
        mockMvc.perform(get(FORM_URL).cookie(new Cookie(Constants.COOKIE_CLAIM_ID, UUID.randomUUID().toString())))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error/session-timeout"));
    }

}
