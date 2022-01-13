package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.NinoSanitiser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

/**
 * Nino capture controller.
 */
@Controller
@RequestMapping("/form/nino")
public class NinoController extends BaseFormController<StringForm<NinoQuestion>> {

    public static final String IDENTIFIER = "form/nino";

    private final NinoSanitiser ninoSanitiser;

    public NinoController(final ClaimRepository claimRepository,
                          final RoutingService routingService,
                          final NinoSanitiser ninoSanitiser) {
        super(claimRepository,
                StringForm.TEXT_INPUT_VIEW_NAME,
                FORM_NAME,
                routingService,
                "form/nino",
                "/form/personal-details",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS);
        this.ninoSanitiser = ninoSanitiser;
    }

    @Override
    public StringForm<NinoQuestion> getForm() {
        StringForm<NinoQuestion> form = new StringForm();
        form.setQuestion(new NinoQuestion());
        return form;
    }

    @Override
    public StringForm<NinoQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey("nino.form.");
        super.setFormAttrs(form, claimId);
    }

    /**
     * Renders the National Insurance Number part of the form.
     *
     * @param model   the Spring Boot MVC model
     * @param claimId Claim's id in cache
     * @param request HttpServletRequest
     * @return Name of view
     */
    @GetMapping
    public final String nino(final Model model,
                             @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Captures submission of the National Insurance Number form.
     *
     * @param ninoForm      Form object used in the form
     * @param bindingResult Spring Boot MVC binding result
     * @param model SpringBoot MVC model
     * @param claimId Claimaint Id
     * @param response HttpServletResponse
     * @return Name of view
     */
    @PostMapping
    public final String submitNino(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final StringForm<NinoQuestion> ninoForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, ninoForm, bindingResult, response, model);
    }

    @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
    @Override
    public void updateClaim(final StringForm<NinoQuestion> form, final Claim claim,
                            final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        String rawValue = form.getQuestion().getValue();
        String sanitisedValue = ninoSanitiser.sanitise(rawValue);
        form.setQuestion(new NinoQuestion(sanitisedValue));
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm<NinoQuestion> form) {
        form.setQuestion(new NinoQuestion(claimDB.getClaimant().getNino()));
    }
}
