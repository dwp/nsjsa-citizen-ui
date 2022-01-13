package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionsAnswer;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static java.util.Arrays.asList;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm.MULTIPLE_OPTIONS_VIEW_NAME;

@Controller
@RequestMapping("/form/eligibility/contributions")
public class ContributionsController extends
        BaseFormController<MultipleOptionsForm<ContributionQuestion, ContributionsAnswer>> {

    private static final ContributionsAnswer TRUE_CONDITION_VALUE = ContributionsAnswer.NO;

    public ContributionsController(final ClaimRepository claimRepository, final RoutingService routingService) {

        super(claimRepository,
                MULTIPLE_OPTIONS_VIEW_NAME,
                FORM_NAME,
                routingService,
                "form/eligibility/contributions",
                "/form/eligibility/eligible",
                "/form/eligibility/contributions/ineligible",
                Section.NONE);
    }

    @Override
    public Form getForm() {
        return new MultipleOptionsForm<>(new ContributionQuestion(), TRUE_CONDITION_VALUE);
    }

    @Override
    public void setFormAttrs(final MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form,
                             final String claimId) {
        form.setTranslationKey("eligibility.contributions.form.");
        form.setInline(false);
        form.setDefaultOption(TRUE_CONDITION_VALUE);
        form.setOptions(asList(ContributionsAnswer.values()));
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB,
                         final MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form) {
        // Nothing to load.
    }

    @GetMapping
    public final String getContributions(final Model model,
                                         @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                         final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postContributions(
            @ModelAttribute(FORM_NAME) @Valid final MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> createNewForm(final Claim claim) {

        ContributionQuestion question = claim.getContributionsQuestion();

        return new MultipleOptionsForm<>(question, TRUE_CONDITION_VALUE);
    }

    @Override
    public void updateClaim(final MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form,
                            final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        MultipleOptionsQuestion<ContributionsAnswer> multipleOptionsQuestion = form.getMultipleOptionsQuestion();
        if (multipleOptionsQuestion instanceof ContributionQuestion) {
            ContributionQuestion contributionsQuestion = (ContributionQuestion) multipleOptionsQuestion;
            claim.setContributionsQuestion(contributionsQuestion);
        } else {
            throw new ClassCastException("ContributionForm is not instance of ContributionQuestion");
        }
    }

}
