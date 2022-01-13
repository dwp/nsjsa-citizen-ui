package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/" + BankAccountFormController.IDENTIFIER)
public class BankAccountFormController extends BaseFormController<BankAccountForm> {

    public static final String IDENTIFIER = "form/bank-account";

    public static final String NEXT_STEP_IDENTIFIER = "/form/other-benefits/have-you-applied";
    public static final String FORM_SUMMARY = "/form/summary";

    public BankAccountFormController(final ClaimRepository claimRepository,
                                     final RoutingService routingService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                NEXT_STEP_IDENTIFIER,
                NO_ALTERNATIVE_IDENTIFIER,
                Section.BANK_DETAILS);
    }

    @Override
    public BankAccountForm getForm() {
        return new BankAccountForm(new BankAccountQuestion());
    }

    @Override
    public BankAccountForm getTypedForm() {
        return getForm();
    }

    @Deprecated
    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model,
                                      final BankAccountForm form) {
        model.addAttribute(FORM_NAME, form);
    }

    @GetMapping
    public final String getView(final Model model,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitBankAccount(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                          @Valid @ModelAttribute(FORM_NAME) final BankAccountForm bankAccountForm,
                                          final BindingResult bindingResult,
                                          final HttpServletResponse response,
                                          final Model model) {
        return post(claimId, bankAccountForm, bindingResult, response, model);
    }

    @GetMapping(path = "/without-bank-details")
    public final String removeAndRedirect(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                          final HttpServletRequest request) {
        ClaimRepository claimRepository = getClaimRepository();
        Claim claim = getOrCreateClaim(claimRepository, claimId);

        StepInstance stepInstance = createStepInstance();
        getRoutingService().leavePage(claimId, stepInstance);

        removeBankDetailsAnswer(claimRepository, claim, stepInstance);

        String editMode = request.getParameter(EDIT_PARAMETER);
        return (editMode != null) ? "redirect:" + FORM_SUMMARY : "redirect:" + NEXT_STEP_IDENTIFIER;
    }

    private StepInstance createStepInstance() {
        Step step = new Step(IDENTIFIER, NEXT_STEP_IDENTIFIER, NO_ALTERNATIVE_IDENTIFIER, Section.BANK_DETAILS);
        return new StepInstance(step, 0, false, false, false);
    }

    private void removeBankDetailsAnswer(final ClaimRepository claimRepository, final Claim claim,
                                         final StepInstance stepInstance) {
        claim.delete(stepInstance, 1);
        claimRepository.save(claim);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final BankAccountForm form) {
        resolve(() -> claimDB.getBankDetails())
                .ifPresent(bankDetails -> {
                    if (!bankDetails.isEmpty()) {
                        form.getQuestion().setAccountHolder(bankDetails.getAccountHolder());
                        form.getQuestion().setAccountNumber(bankDetails.getAccountNumber());
                        form.getQuestion().setSortCode(new SortCode(bankDetails.getSortCode()));
                        form.getQuestion().setReferenceNumber(bankDetails.getReference());
                    }
                });

    }
}
