package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.dwp.jsa.adaptors.ServicesProperties;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasCurrentWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.ExpectPaymentController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.ConfirmationForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static uk.gov.dwp.jsa.citizen_ui.Constants.*;

@Controller
public class ConfirmationController extends BaseFormController<ConfirmationForm> {

    private final PensionsService pensionsService;
    private ServicesProperties servicesProperties;

    public ConfirmationController(final ClaimRepository claimRepository,
                                  final RoutingService routingService,
                                  final PensionsService pensionsService, final ServicesProperties servicesProperties) {
        super(claimRepository,
                "claimant-confirmation",
                "claim",
                routingService,
                "claimant-confirmation",
                "/",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.NONE);
        this.pensionsService = pensionsService;
        this.servicesProperties = servicesProperties;
    }

    @GetMapping(path = "/claimant-confirmation")
    public final String getView(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final Model model,
            final HttpServletRequest request,
            final HttpServletResponse httpServletResponse,
            @Value("${" + Constants.AGENT_MODE + "}") final boolean agentMode) {
        Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
        model.addAttribute("isAgent", agentMode);
        if (nonNull(claim.getClaimantId())) {
            model.addAttribute("bookAppointmentUrl",
                    servicesProperties.getAgentUiServer() + Constants.AGENT_UI_CLAIM_CREATED_URL);
            model.addAttribute("claimantId",
                    claim.getClaimantId());
        }
        String viewName = get(model, claimId, request);
        deleteClaimAndCookieIfPostsAreSuccessful(httpServletResponse, claimId);
        return viewName;
    }

    protected void deleteClaimAndCookieIfPostsAreSuccessful(final HttpServletResponse response,
                                                            final String claimId) {
        deleteClaimFromCache(claimId);
        deleteCookie(response, COOKIE_CLAIM_ID);
        deleteCookie(response, JSESSIONID);
        deleteCookie(response, JWT_SESSION);
        deleteCookie(response, XSRF_TOKEN);
    }

    private void deleteCookie(final HttpServletResponse response, final String cookieId) {
        Cookie cookie = new Cookie(cookieId, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private void deleteClaimFromCache(final String claimId) {
        getClaimRepository().deleteById(claimId);
    }

    @Override
    public ConfirmationForm createNewForm(final Claim claim) {
        ConfirmationForm confirmationForm = new ConfirmationForm();

        Optional<BooleanQuestion> juryServiceQuestion =
                claim.get(JuryServiceConfirmationController.IDENTIFIER, BooleanQuestion.class);

        juryServiceQuestion.ifPresent(booleanQuestion ->
                confirmationForm.setHasBeenToJuryService(booleanQuestion.getChoice()));

        List<BooleanQuestion> expectPaymentQuestions =
                claim
                        .getAll(
                                ExpectPaymentController.IDENTIFIER,
                                MAX_JOBS_ALLOWED,
                                BooleanQuestion.class)
                        .stream()
                        .filter(BooleanQuestion::getChoice)
                        .collect(Collectors.toList());

        confirmationForm.setExpectPaymentInLast6Months(!expectPaymentQuestions.isEmpty());

        Optional<BooleanQuestion> hasPreviousEmployment =
                claim.get(HasPreviousWorkController.IDENTIFIER, BooleanQuestion.class);

        hasPreviousEmployment.ifPresent(booleanQuestion ->
                confirmationForm.setHasWorkedInLast6Months(booleanQuestion.getChoice()));

        Optional<BooleanQuestion> areYouWorking =
                claim.get(HasCurrentWorkController.IDENTIFIER, BooleanQuestion.class);

        if (areYouWorking.map(BooleanQuestion::getChoice).orElse(false)) {

            List<PaymentFrequencyQuestion> paymentFrequencies =
                    claim.getAll(
                            PaymentFrequencyController.IDENTIFIER,
                            MAX_JOBS_ALLOWED,
                            PaymentFrequencyQuestion.class);

            confirmationForm.setCurrentlyWorkingWithPay(!paymentFrequencies.isEmpty());

            List<PaymentFrequencyQuestion> weeklyOfFortnightly =
                    paymentFrequencies
                            .stream()
                            .filter(PaymentFrequencyQuestion::isWeeklyOrFortnightlyPayments)
                            .collect(Collectors.toList());

            confirmationForm.setPaymentWeeklyOrFortnightly(!weeklyOfFortnightly.isEmpty());

            List<PaymentFrequencyQuestion> monthlyOrFourWeekly =
                    paymentFrequencies
                            .stream()
                            .filter(PaymentFrequencyQuestion::isMonthlyOrFourWeeklyPayments)
                            .collect(Collectors.toList());

            confirmationForm.setPaymentMonthlyOrFourWeekly(!monthlyOrFourWeekly.isEmpty());
        }

        // -- PENSIONS
        boolean hasPensions = pensionsService.hasCurrentPension(claim.getId());
        confirmationForm.setHasPensions(hasPensions);

        //bank details - if true Claimant needs to bring details of bank account to appointment

        boolean needsBankDetailsEvidence = !claim.getBankAccount().isPresent();
        confirmationForm.setNeedsBankDetailsEvidence(needsBankDetailsEvidence);

        //claim start date - if true claimant needs to bring proof of job seeking since their claim start date
        boolean hasChangedStartDate = false;
        Optional<ClaimStartDateQuestion> claimStartDateOptional = claim.getClaimStartDate();
        if (claimStartDateOptional.isPresent()) {
            ClaimStartDateQuestion claimStartDate = claimStartDateOptional.get();
            hasChangedStartDate = !LocalDate.parse(claimStartDate.getFormattedDate(), SDT).equals(
                    claim.getInitialDateOfContact());
            if (hasChangedStartDate) {
                confirmationForm.setNewStartDate(claimStartDate.getFormattedDate());
            }
        }
        confirmationForm.setHasChangedStartDate(hasChangedStartDate);


        return confirmationForm;
    }

    @Override
    public void updateClaim(final ConfirmationForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final ConfirmationForm form) {
        // Nothing to load
    }

}
