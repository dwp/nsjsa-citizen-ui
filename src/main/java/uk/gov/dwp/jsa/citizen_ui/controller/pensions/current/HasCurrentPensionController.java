package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/" + HasCurrentPensionController.IDENTIFIER)
public class HasCurrentPensionController extends BaseFormController<GuardForm<GuardQuestion>> {

    public static final String IDENTIFIER = "form/pensions/current/has-pension";
    private final PensionsService pensionsService;

    public HasCurrentPensionController(final ClaimRepository claimRepository, final RoutingService routingService,
                                       final PensionsService pensionsService) {

        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/education/have-you-been",
                "/form/pensions/current/details/1/provider-name",
                Section.CURRENT_PENSIONS);
        this.pensionsService = pensionsService;
    }

    @Override
    public GuardForm<GuardQuestion> getForm() {
        return new GuardForm<>(new GuardQuestion());
    }

    @Override
    public GuardForm<GuardQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final GuardForm<GuardQuestion> form, final String claimId) {
        form.setTranslationKey("pensions.current.has.pension.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<GuardQuestion> form) {
        form.setQuestion(new GuardQuestion((resolve(() -> !claimDB
                .getCircumstances()
                .getPensions()
                .getCurrent()
                .isEmpty())
                .orElse(false))));
    }

    @GetMapping
    public final String getDoYouHavePensions(
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postDoYouHavePensions(
            @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {

        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public String getNextPath(final Claim claim,
                              final GuardForm<GuardQuestion> form,
                              final StepInstance stepInstance) {
        if (form.isGuardedCondition()) {
            if (!pensionsService.canAddPension(claim)) {
                return format("redirect:/form/pensions/max-current-pensions?backUrl=%s",
                        "/form/pensions/current/has-pension");
            } else {
                return "redirect:/form/pensions/current/details/1/provider-name" + getEditParam(form);
            }
        } else {
            return super.getNextPath(claim, form, stepInstance);
        }
    }
}
