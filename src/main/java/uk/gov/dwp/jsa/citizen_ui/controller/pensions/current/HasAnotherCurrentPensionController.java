package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.services.PensionCurrentMappingService.PENSION_CURRENT_LOOP_IDENTIFIERS;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/form/pensions/current")
public class HasAnotherCurrentPensionController extends CounterFormController<GuardForm<LoopEndBooleanQuestion>> {

    public static final String IDENTIFIER = "form/pensions/current/has-another-pension";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/%s/has-another-pension";
    public static final String IDENTIFIER_MAX_CURRENT_WORK = "form/pensions/max-current-pensions";
    private final PensionsService pensionsService;
    private CookieLocaleResolver cookieLocaleResolver;

    public HasAnotherCurrentPensionController(
            final ClaimRepository claimRepository,
            final RoutingService routingService,
            final PensionsService pensionsService,
            final CookieLocaleResolver cookieLocaleResolver
    ) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/education/have-you-been",
                "/form/pensions/current/details/%s/provider-name",
                Section.CURRENT_PENSIONS);

        this.pensionsService = pensionsService;
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @GetMapping("/{count:[1-9]+}/has-another-pension")
    public String getView(@PathVariable final Integer count,
                          final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {
        String viewName = get(model, claimId, request, count);
        String editMode = request.getParameter(EDIT_PARAMETER);
        if (EditMode.SECTION.toString().equals(editMode)) {
            return "redirect:/form/summary";
        } else {
            useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
            return viewName;
        }
    }


    @GetMapping(path = "/{count:[1-9]+}/remove")
    public final String delete(@PathVariable final Integer count,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId) {
        Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
        PENSION_CURRENT_LOOP_IDENTIFIERS.forEach(
                identifier -> deleteInstance(identifier, claim, count, MAX_PENSIONS_ALLOWED));
        updateGuardChoice(HasCurrentPensionController.IDENTIFIER,
                ProviderNameController.IDENTIFIER, MAX_PENSIONS_ALLOWED, claim);
        getClaimRepository().save(claim);
        return "redirect:/form/summary";
    }

    @PostMapping("/{count:[1-9]+}/has-another-pension")
    public String submitForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             @ModelAttribute(FORM_NAME) @Valid final GuardForm form,
                             final BindingResult bindingResult,
                             final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Model model) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        if (!bindingResult.hasErrors()) {
            removeFrom(!form.getQuestion().getChoice(), claimId, PENSION_CURRENT_LOOP_IDENTIFIERS,
                    form.getCount(), HasCurrentPensionController.IDENTIFIER, ProviderNameController.IDENTIFIER,
                    MAX_PENSIONS_ALLOWED);
        }
        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public GuardForm<LoopEndBooleanQuestion> getForm() {
        return new GuardForm<>(new LoopEndBooleanQuestion());
    }

    @Override
    public GuardForm<LoopEndBooleanQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final GuardForm form, final String claimId) {
        form.setTranslationKey("pensions.current.has.another.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public String getNextPath(final Claim claim, final GuardForm form, final StepInstance stepInstance) {
        if (form.isGuardedCondition()) {
            if (!pensionsService.canAddPension(claim)) {
                String editParam = getEditParam(form);
                String nextUrl = "redirect:/form/pensions/max-current-pensions";
                if (StringUtils.isEmpty(editParam)) {
                    nextUrl += "?backUrl=%s";
                } else {
                    nextUrl += editParam + "&backUrl=%s";
                }
                return format(
                        nextUrl,
                        format("/form/pensions/current/%s/has-another-pension", form.getCount()));
            } else {
                return format("redirect:/form/pensions/current/details/%s/provider-name", (form.getCount() + 1));
            }
        } else {
            return super.getNextPath(claim, form, stepInstance);
        }
    }

    @Override
    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", justification = "False positive")
    public void updateClaim(final GuardForm<LoopEndBooleanQuestion> form, final Claim claim,
                            final StepInstance currentStepInstance, final Optional<StepInstance> lastGuard) {
        LoopEndBooleanQuestion question = form.getQuestion();
        if (canAddPension(form, claim)) {
            question.setHasMoreThanLimit(true);
            form.setQuestion(question);
        }
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<LoopEndBooleanQuestion> form) {
        if (form.getCount() < claimDB.getCircumstances().getPensions().getCurrent().size()) {
            final boolean hasExtraPensions = TRUE.equals(claimDB.getCircumstances().getPensions().isHasExtraPensions());
            final LoopEndBooleanQuestion question = new LoopEndBooleanQuestion(true,
                    hasExtraPensions);
            form.setQuestion(question);
        } else {
            form.setQuestion(new LoopEndBooleanQuestion(false, false));
        }
    }

    private boolean canAddPension(final GuardForm form, final Claim claim) {
        return form.getQuestion().getChoice() && !pensionsService.canAddPension(claim);
    }
}
