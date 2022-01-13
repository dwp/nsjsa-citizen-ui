package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersDatesController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.services.PreviousEmploymentMappingService.PREVIOUS_EMPLOYMENT_LOOP_IDENTIFIERS;

@Controller
@RequestMapping("/form/previous-employment")
public class AddWorkController extends CounterFormController<GuardForm<LoopEndBooleanQuestion>> {

    public static final String IDENTIFIER = "form/previous-employment/add-work";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/%s/add-work";
    public static final String IDENTIFIER_MAX_CURRENT_WORK = "form/previous-employment/max-jobs";

    @Autowired
    public AddWorkController(final ClaimRepository claimRepository,
                             final RoutingService routingService) {
        super(claimRepository,
              BOOLEAN_VIEW_NAME,
              FORM_NAME,
              routingService,
              IDENTIFIER,
                "/" + HasOutsideWorkController.IDENTIFIER,
              NO_ALTERNATIVE_IDENTIFIER,
              Section.PREVIOUS_EMPLOYMENT);
    }

    @GetMapping(path = "/{count:[1-4]+}/add-work")
    public final String getAddWork(@PathVariable final Integer count,
                                   final Model model,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        String viewName = get(model, claimId, request, count);
        String editMode = request.getParameter(EDIT_PARAMETER);
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        if (EditMode.SECTION.toString().equals(editMode)) {
            return "redirect:/form/summary";
        } else {
            return viewName;
        }
    }

    @GetMapping(path = "/{count:[1-4]+}/remove-work")
    public final String deleteWork(@PathVariable final Integer count,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
        PREVIOUS_EMPLOYMENT_LOOP_IDENTIFIERS.forEach(
                identifier -> deleteInstance(identifier, claim, count, MAX_JOBS_ALLOWED));
        updateGuardChoice(HasPreviousWorkController.IDENTIFIER, EmployersDatesController.IDENTIFIER, MAX_JOBS_ALLOWED,
                          claim);
        getClaimRepository().save(claim);

        return "redirect:/form/summary";
    }

    @GetMapping(path = "/max-jobs")
    public final String getMaxJobsWarning() {

        return "form/previous-employment/max-previous-jobs";
    }

    @PostMapping(path = "/{count:[1-4]+}/add-work")
    public final String submitAddWork(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final GuardForm<LoopEndBooleanQuestion> addWorkForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {
        BooleanQuestion question = addWorkForm.getQuestion();
        Boolean choice = question.getChoice();
        if (!bindingResult.hasErrors()) {
            removeFrom(!choice, claimId, PREVIOUS_EMPLOYMENT_LOOP_IDENTIFIERS,
                    addWorkForm.getCount(), HasPreviousWorkController.IDENTIFIER, EmployersDatesController.IDENTIFIER,
                    MAX_JOBS_ALLOWED);
        }
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, addWorkForm, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
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

    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", justification = "False positive")
    private boolean hasMoreThan4Jobs(final GuardForm<LoopEndBooleanQuestion> form, final Claim claim) {
        return form.getQuestion().getChoice()
                && (claim.count(EmployersDatesController.IDENTIFIER, MAX_JOBS_ALLOWED) >= MAX_JOBS_ALLOWED);
    }

    @Override
    public String getNextPath(final Claim claim, final GuardForm<LoopEndBooleanQuestion> form,
                              final StepInstance stepInstance) {
        if (form.isGuardedCondition()) {
            if (form.getCount() >= MAX_JOBS_ALLOWED) {
                return "redirect:/" + IDENTIFIER_MAX_CURRENT_WORK + getEditParam(form);
            } else {
                return format("redirect:/form/previous-employment/employer-details/%s/dates", (form.getCount() + 1));
            }
        } else {
            return super.getNextPath(claim, form, stepInstance);
        }
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
    public void setFormAttrs(final GuardForm<LoopEndBooleanQuestion> form, final String claimId) {
        form.setTranslationKey("previousWork.hasAnotherWork.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<LoopEndBooleanQuestion> form) {
        if (form.getCount() < claimDB.getCircumstances().getPreviousWork().size()) {
            LoopEndBooleanQuestion question = new LoopEndBooleanQuestion(true);
            if (TRUE.equals(claimDB.getCircumstances().isHasExtraPreviousWork())) {
                question.setHasMoreThanLimit(true);
            }
            form.setQuestion(question);
        } else {
            LoopEndBooleanQuestion question = new LoopEndBooleanQuestion(false);
            form.setQuestion(question);
        }
    }
}
