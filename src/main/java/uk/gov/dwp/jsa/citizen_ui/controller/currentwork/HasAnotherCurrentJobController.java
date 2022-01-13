package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.services.CurrentEmploymentMappingService.CURRENT_EMPLOYMENT_LOOP_IDENTIFIERS;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
public class HasAnotherCurrentJobController extends CounterFormController<GuardForm<LoopEndBooleanQuestion>> {

    public static final String IDENTIFIER = "form/current-work/has-another-job";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/%s/has-another-job";
    public static final String IDENTIFIER_MAX_CURRENT_WORK = "form/current-work/max-jobs";
    private CookieLocaleResolver cookieLocaleResolver;

    public HasAnotherCurrentJobController(final ClaimRepository claimRepository,
                                          final RoutingService routingService,
                                          final CookieLocaleResolver cookieLocaleResolver) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/has-previous-work",
                "/form/current-work/details/%s/is-work-paid",
                Section.CURRENT_WORK);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @GetMapping("/form/current-work/{count:[1-4]+}/has-another-job")
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

    @GetMapping(path = "/form/current-work/{count:[1-4]+}/remove-work")
    public final String deleteWork(@PathVariable final Integer count,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
        CURRENT_EMPLOYMENT_LOOP_IDENTIFIERS.forEach(
                identifier -> deleteInstance(identifier, claim, count, MAX_JOBS_ALLOWED));
        updateGuardChoice(HasCurrentWorkController.IDENTIFIER, EmployersNameController.IDENTIFIER, MAX_JOBS_ALLOWED,
                          claim);
        getClaimRepository().save(claim);

        return "redirect:/form/summary";
    }

    @GetMapping(path = "/form/current-work/max-jobs")
    public final String getMaxJobsWarning(final Model model) {
        model.addAttribute("backUrl", "/form/current-work/4/has-another-job");
        model.addAttribute("nextUrl", "/form/previous-employment/has-previous-work");
        return IDENTIFIER_MAX_CURRENT_WORK;
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
        form.setTranslationKey("currentwork.has.another.job.");
        super.setFormAttrs(form, claimId);
    }

    @PostMapping("/form/current-work/{count:[1-4]+}/has-another-job")
    public String submitForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             @ModelAttribute(FORM_NAME) @Valid final GuardForm form,
                             final BindingResult bindingResult,
                             final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Model model) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        if (!bindingResult.hasErrors()) {
            removeFrom(!form.getQuestion().getChoice(), claimId, CURRENT_EMPLOYMENT_LOOP_IDENTIFIERS,
                    form.getCount(), HasCurrentWorkController.IDENTIFIER, EmployersNameController.IDENTIFIER,
                    MAX_JOBS_ALLOWED);
        }
        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public String getNextPath(final Claim claim, final GuardForm form, final StepInstance stepInstance) {
        if (form.isGuardedCondition()) {
            if (form.getCount() >= MAX_JOBS_ALLOWED) {
                return "redirect:/form/current-work/max-jobs" + getEditParam(form);
            } else {
                return format("redirect:/form/current-work/details/%s/is-work-paid", (form.getCount() + 1));
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
        if (hasMoreThan4Jobs(form, claim)) {
            question.setHasMoreThanLimit(true);
            form.setQuestion(question);
        }
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<LoopEndBooleanQuestion> form) {
        if (form.getCount() < claimDB.getCircumstances().getCurrentWork().size()) {
            boolean hasExtraCurrentWork =
                    resolve(() -> claimDB.getCircumstances().isHasExtraCurrentWork()).orElse(false);
            form.setQuestion(new LoopEndBooleanQuestion(true, hasExtraCurrentWork));
        } else {
            form.setQuestion(new LoopEndBooleanQuestion(false, false));
        }
    }

    private boolean hasMoreThan4Jobs(final GuardForm form, final Claim claim) {
        return form.getQuestion().getChoice()
                && (claim.count(WorkPaidOrVoluntaryController.IDENTIFIER, MAX_JOBS_ALLOWED) >= MAX_JOBS_ALLOWED);
    }
}
