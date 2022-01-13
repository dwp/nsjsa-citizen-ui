package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/personal-details/contact/email")
public class EmailController extends BaseFormController<EmailForm> {

    public static final String IDENTIFIER = "form/personal-details/contact/email";
    public static final String EMAIL_INPUT_VIEW_NAME = "form/personal-details/email";
    private final EmailSanitiser emailSanitiser;

    public EmailController(final ClaimRepository claimRepository,
                           final RoutingService routingService,
                           final EmailSanitiser emailSanitiser) {
        super(claimRepository,
                EMAIL_INPUT_VIEW_NAME,
                "emailForm",
                routingService,
                IDENTIFIER,
                "/form/bank-account",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS_EMAIL);
        this.emailSanitiser = emailSanitiser;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final EmailForm form) {
        resolve(() -> claimDB.getClaimant().getContactDetails().getEmail())
                .ifPresent(email -> form.getQuestion().setEmail(email));
    }

    @GetMapping
    public final String getEmail(final Model model,
                                 @CookieValue(value = COOKIE_CLAIM_ID, required = false)
                                 final String claimId,
                                 final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitEmail(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid final EmailForm emailForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, emailForm, bindingResult, response, model);
    }

    @Override
    public void updateClaim(final EmailForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        String rawValue = form.getQuestion().getEmail();
        String sanitisedValue = emailSanitiser.sanitise(rawValue);
        form.getQuestion().setEmail(sanitisedValue);
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public EmailForm getForm() {
        return new EmailForm(new EmailStringQuestion());
    }

    @Override
    public EmailForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final EmailForm form, final String claimId) {
        super.setFormAttrs(form, claimId);
    }

    @Override
    public Optional<StepInstance> getLastGuard(final EmailForm form, final Claim claim, final StepInstance stepInstance) {
        return Optional.empty();
    }
}
