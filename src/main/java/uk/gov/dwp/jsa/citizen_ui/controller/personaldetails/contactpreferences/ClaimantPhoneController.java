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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PhoneSanitiser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/personal-details/contact/telephone")
public class ClaimantPhoneController extends BaseFormController<PhoneForm> {

    public static final String IDENTIFIER = "form/personal-details/contact/telephone";
    private static final String PHONE_VIEW_NAME = "form/personal-details/phone-number";
    private final PhoneSanitiser phoneSanitiser;

    public ClaimantPhoneController(final ClaimRepository claimRepository,
                                   final RoutingService routingService,
                                   final PhoneSanitiser phoneSanitiser) {

        super(claimRepository,
                PHONE_VIEW_NAME,
                "phoneForm",
                routingService,
                IDENTIFIER,
                "/form/personal-details/contact/email",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS);
        this.phoneSanitiser = phoneSanitiser;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PhoneForm form) {
        resolve(() -> claimDB.getClaimant().getContactDetails().getNumber())
                .ifPresent(phone -> form.getQuestion().setPhoneNumber(phone));
    }

    @GetMapping
    public final String getPhoneView(final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false)
                                     final String claimId,
                                     final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitPhoneForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                        @Valid final PhoneForm phoneForm,
                                        final BindingResult bindingResult,
                                        final HttpServletResponse response,
                                        final Model model) {
        return post(claimId, phoneForm, bindingResult, response, model);
    }

    @Override
    public void updateClaim(final PhoneForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        String rawValue = form.getQuestion().getPhoneNumber();
        String sanitisedValue = phoneSanitiser.sanitise(rawValue);
        form.getQuestion().setPhoneNumber(sanitisedValue);
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public PhoneForm getForm() {
        return new PhoneForm(new PhoneQuestion());
    }

    @Override
    public PhoneForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final PhoneForm form, final String claimId) {
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model,
                                      final PhoneForm form) {
        model.addAttribute(FORM_NAME, form);
    }

    @Override
    public Optional<StepInstance> getLastGuard(final PhoneForm form, final Claim claim, final StepInstance stepInstance) {
        return Optional.empty();
    }
}
