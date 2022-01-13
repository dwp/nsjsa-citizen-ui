package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import uk.gov.dwp.jsa.citizen_ui.gateway.BaseRequestJSONBuilder;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.services.RequiredDataService;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static java.lang.String.valueOf;
import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
public class DeclarationFormAcceptanceTests {
    private static final String GET_LOCAL_OFFICE_URL = "/nsjsa/v1/office/job-centre/postcode/([a-zA-Z0-9\\-]*)";
    private static final LocalDate TODAY = LocalDate.now();

    @Rule
    public WireMockRule wireMockRuleClaimant = new WireMockRule(wireMockConfig().port(39090));
    @Rule
    public WireMockRule wireMockRuleCircumstances = new WireMockRule(wireMockConfig().port(39091));
    @Rule
    public WireMockRule wireMockRuleBank = new WireMockRule(wireMockConfig().port(39092));
    @Rule
    public WireMockRule wireMockRuleNotification = new WireMockRule(wireMockConfig().port(39097));
    @Rule
    public WireMockRule wireMockRuleValidation = new WireMockRule(wireMockConfig().port(39094));
    @Autowired
    public ClaimRepository claimRepository;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        wireMockRuleClaimant.stubFor(WireMock.post(urlPathMatching("/nsjsa/v1/citizen"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
        wireMockRuleBank.stubFor(WireMock.post(urlPathMatching("/nsjsa/v1/claim/([a-zA-Z0-9\\-]*)/bank-details"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
        wireMockRuleNotification.stubFor(WireMock.post("/nsjsa/v1/notification/sms/claim-confirmation")
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
        wireMockRuleNotification.stubFor(WireMock.post(urlPathMatching("/nsjsa/v1/notification/mail/claim-confirmation"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
        wireMockRuleCircumstances.stubFor(WireMock.post(urlPathMatching("/nsjsa/v1/citizen/([a-zA-Z0-9\\-]*)/claim"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
        wireMockRuleValidation.stubFor(WireMock.post(urlPathMatching("/nsjsa/v1/claim/([a-zA-Z0-9\\-]*)/validation"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(BaseRequestJSONBuilder.JSON_RESPONSE)
                        .withStatus(201)));
    }

    @Test
    @Ignore
    public void GetDeclaration_ReturnsDeclarationForm() throws Exception {
        String nino = "AC123456C";
        Cookie claimIdCookie = mockMvc.perform(
                post("/" + ClaimStartDateController.IDENTIFIER).with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.day", String.valueOf(TODAY.getDayOfMonth()))
                        .param("question.month", String.valueOf(TODAY.getMonthValue()))
                        .param("question.year", String.valueOf(TODAY.getYear())))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_CLAIM_ID);

        LocalDate dob = TODAY.minusYears(20);

        postDateOfBirth(claimIdCookie,
                String.valueOf(dob.getDayOfMonth()),
                String.valueOf(dob.getMonthValue()),
                String.valueOf(dob.getYear()));
        postNino(claimIdCookie, nino);
        mockMvc.perform(get("/form/declaration").with(csrf())
                .cookie(claimIdCookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"declaration\"")));
    }

    @Test
    public void GivenCheckedDeclaration_PostForm_ReturnsConfirmationPageAndDeletesClaimObject() throws Exception {
        String day = valueOf(now().getDayOfMonth());
        String month = valueOf(now().getMonthValue());
        String year = valueOf(now().getYear());
        Cookie claimIdCookie = mockMvc.perform(
                post("/" + ClaimStartDateController.IDENTIFIER).with(csrf())
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
        checkIfClaimIsPresent(claimIdCookie.getValue(), true);
        mockMvc.perform(
                post("/form/declaration").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.agreed", "true")
                        .cookie(claimIdCookie))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", equalTo("/claimant-confirmation")));
        checkIfClaimIsPresent(claimIdCookie.getValue(), true);
    }

    @Test
    public void GivenCitizenNotAgreedTermsAndConditions_returnError() throws Exception {
        mockMvc.perform(post("/form/declaration")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question.agreed", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Declaration acceptance is required")));
    }

    private void checkIfClaimIsPresent(final String claimId, final boolean isClaimPresent) {
        final Optional<Claim> claim = claimRepository.findById(claimId);
        boolean hasNext = claim.isPresent();
        assertThat(hasNext, is(isClaimPresent));
        claim.ifPresent(c-> {
            c.setClaimantId(UUID.randomUUID().toString());
            claimRepository.save(c);
        });
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
}
