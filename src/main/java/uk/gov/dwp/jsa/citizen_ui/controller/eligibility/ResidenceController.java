package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/form/eligibility/residence")
public class ResidenceController extends BaseFormController<ResidenceForm> {

    public static final String IDENTIFIER = "form/eligibility/residence";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    public ResidenceController(final ClaimRepository claimRepository,
                               final CookieLocaleResolver cookieLocaleResolver,
                               final RoutingService routingService) {

        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                Section.NONE,
                cookieLocaleResolver);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @Override
    public String nextStepBasedOn(final ResidenceForm form, final Claim claim) {
        if (form.getQuestion().getChoice()) {
            return "/form/eligibility/working";
        } else {
            return "/form/eligibility/residence/ineligible";
        }
    }

    @GetMapping
    public final String getResidence(final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                     final HttpServletRequest request) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitResidence(final Model model,
                                        @ModelAttribute(FORM_NAME) @Valid final ResidenceForm form,
                                        final BindingResult bindingResult,
                                        @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public ResidenceForm getForm() {
        return new ResidenceForm();
    }

    @Override
    public ResidenceForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final ResidenceForm form) {
    }

    @Override
    public void setFormAttrs(final ResidenceForm form, final String claimId) {
        form.setTranslationKey("eligibility.residence.form.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void updateClaim(final ResidenceForm form, final Claim claim, final StepInstance currentStepInstance, final Optional<StepInstance> lastGuard) {
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(form.getQuestion().getUkResidence());
        claim.setResidenceQuestion(residenceQuestion);
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }
}
