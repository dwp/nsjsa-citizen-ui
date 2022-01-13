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
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.HoursWorkingPerWeekQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.Working16Hours;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static java.util.Arrays.asList;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm.MULTIPLE_OPTIONS_VIEW_NAME;

@Controller
@RequestMapping("/form/eligibility/working-over")
public class WorkingHoursPerWeekController extends
        BaseFormController<MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours>> {

    private static final Working16Hours TRUE_CONDITION_VALUE = Working16Hours.WORKING_LESS_THAN_16_HOURS;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    public WorkingHoursPerWeekController(final ClaimRepository claimRepository,
                                         final RoutingService routingService,
                                         final CookieLocaleResolver cookieLocaleResolver) {

        super(claimRepository,
                MULTIPLE_OPTIONS_VIEW_NAME,
                FORM_NAME,
                routingService,
                "form/eligibility/working-over",
                Section.NONE,
                cookieLocaleResolver);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @Override
    public String nextStepBasedOn(final MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> form, final Claim claim) {
        Working16Hours answer = form.getQuestion().getUserSelectionValue();

        if (Working16Hours.WORKING_LESS_THAN_16_HOURS.equals(answer)) {
            if (claim.getResidenceQuestion().getUkResidence()) {
                return "/form/eligibility/eligible";
            }
            return "/form/eligibility/residence/working-over/ineligible";
        }

        if (Working16Hours.WORKING_MORE_THAN_16_HOURS.equals(answer)) {
            if (claim.getResidenceQuestion().getUkResidence()) {
                return "/form/eligibility/working-over/ineligible";
            } else {
                return "/form/eligibility/working-over-residence/working-over/ineligible";
            }
        }
        throw new RuntimeException("Cannot resolve next step");
    }

    @Override
    public Form getForm() {
        return new MultipleOptionsForm<>(new HoursWorkingPerWeekQuestion(), TRUE_CONDITION_VALUE);
    }

    @Override
    public void setFormAttrs(final MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> form,
                             final String claimId) {
        form.setTranslationKey("eligibility.hoursworkingperweek.form.");
        form.setInline(false);
        form.setDefaultOption(TRUE_CONDITION_VALUE);
        form.setOptions(asList(Working16Hours.values()));
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final MultipleOptionsForm<HoursWorkingPerWeekQuestion,
            Working16Hours> form) {
        // Nothing to load.
    }

    @GetMapping
    public final String getHoursWorkingPerWeek(final Model model,
                                               @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                               final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }


    @PostMapping
    public final String postHoursWorkingPerWeek(
            @ModelAttribute(FORM_NAME) @Valid final MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> createNewForm(final Claim claim) {

        HoursWorkingPerWeekQuestion question = claim.getHoursWorkingPerWeekQuestion();

        return new MultipleOptionsForm<>(question, TRUE_CONDITION_VALUE);
    }

    @Override
    public void updateClaim(final MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> form,
                            final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        MultipleOptionsQuestion<Working16Hours> multipleOptionsQuestion = form.getMultipleOptionsQuestion();
        if (multipleOptionsQuestion instanceof HoursWorkingPerWeekQuestion) {
            HoursWorkingPerWeekQuestion hoursWorkingQuestion = (HoursWorkingPerWeekQuestion) multipleOptionsQuestion;
            claim.setHoursWorkingPerWeekQuestion(hoursWorkingQuestion);
        } else {
            throw new ClassCastException("Hours Working Per Week Form is not instance of HoursWorkingPerWeekQuestion");
        }
    }
}
