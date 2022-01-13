package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

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
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.ClaimBuilder;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ConfirmationAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }


    private void checkIfClaimIsPresent(final String claimId, final boolean isClaimPresent) {
        boolean hasNext = claimRepository.findById(claimId).isPresent();
        assertThat(hasNext, is(isClaimPresent));
    }

    @Test
    public void GivenConfirmationPageIsLoaded_NewContentIsDisplayed() throws Exception {
        Claim claim = new ClaimBuilder().build();
        claim.setClaimantId(UUID.randomUUID().toString());
        claimRepository.save(claim);

        mockMvc.perform(get("/claimant-confirmation").cookie(new Cookie(COOKIE_CLAIM_ID, claim.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("We’ll process your application.")))
        ;

    }


    public void setupJuryService(Claim claim, boolean hasBeen) {

    }

    public void setupNoPreviousEmployment(Claim claim) {

    }

    public void setupPreviousEmployment(final Claim claim, final Boolean expectingPayment) {
        Step step = new Step(HasPreviousWorkController.IDENTIFIER, "", "", Section.NONE);
        StepInstance stepInstance = new StepInstance(
                step,
                0,
                false,
                false,
                false
                );
        claim.save(stepInstance, new BooleanQuestion(false), Optional.empty());
    }

    public void setupPaidCurrentWorkWithFrequency(final Claim claim, final PaymentFrequency frequency) {

    }
}
