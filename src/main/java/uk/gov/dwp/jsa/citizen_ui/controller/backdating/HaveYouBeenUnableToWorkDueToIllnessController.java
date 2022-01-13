package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.DateRangeQuestionWithBoolean;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.UnableToWorkDueToIllnessForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.UnableToWorkDueToIllnessQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/" + HaveYouBeenUnableToWorkDueToIllnessController.IDENTIFIER)
public class HaveYouBeenUnableToWorkDueToIllnessController extends BaseFormController<UnableToWorkDueToIllnessForm> {
    public static final String VIEW_NAME = "form/backdating/have-you-been-unable-to-work-due-to-illness";
    public static final String IDENTIFIER = "form/backdating/have-you-been-unable-to-work-due-to-illness";
    private static final String FOR_EXAMPLE_START_DATE_TEXT = "01 09 2020";
    private static final String FOR_EXAMPLE_END_DATE_TEXT = "14 09 2020";

    @Autowired
    public HaveYouBeenUnableToWorkDueToIllnessController(final ClaimRepository claimRepository, final RoutingService routingService,
                                                         final DateFormatterUtils dateFormatterUtils, final CookieLocaleResolver resolver) {
        super(claimRepository,
                VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/" + HaveYouTravelledOutsideController.IDENTIFIER,
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING,
                dateFormatterUtils,
                resolver);
    }

    @Override
    public UnableToWorkDueToIllnessForm getForm() {
        UnableToWorkDueToIllnessForm form = new UnableToWorkDueToIllnessForm();
        form.setQuestion(new UnableToWorkDueToIllnessQuestion());
        return form;
    }

    @Override
    public UnableToWorkDueToIllnessForm getTypedForm() {
        return getForm();
    }

    @GetMapping
    public final String getHaveYouBeenUnableToWorkDueToIllness(
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {
        addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postHaveYouBeenUnableToWorkDueToIllness(
            @ModelAttribute(FORM_NAME) @Valid final UnableToWorkDueToIllnessForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Model model) {
        if (bindingResult.hasErrors()) {
            addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        }
        return post(claimId, form, bindingResult, response, model);
    }

    @ModelAttribute
    public void exampleDates(final Model model) {
        model.addAttribute("exampleStartDate", FOR_EXAMPLE_START_DATE_TEXT);
        model.addAttribute("exampleEndDate", FOR_EXAMPLE_END_DATE_TEXT);
    }

    @Override
    public void setFormAttrs(final UnableToWorkDueToIllnessForm form, final String claimId) {
        form.setTranslationKey("backdating.unable.to.work.due.to.illness.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void updateClaim(final UnableToWorkDueToIllnessForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        DateRangeQuestionWithBoolean question = form.getQuestion().getDateRangeQuestion();
        form.getQuestion().setDateRangeQuestion(question);
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final UnableToWorkDueToIllnessForm form) {
        resolve(() -> claimDB.getCircumstances().getBackDating().getNonWorkingIllness())
                .ifPresent(nonWorkingIllness -> {
                    form.getQuestion().setHasProvidedAnswer(nonWorkingIllness.getHadIllness());
                    if (nonWorkingIllness.getHadIllness() != null && nonWorkingIllness.getHadIllness()) {
                        form.getQuestion().setDateRangeQuestion(new DateRangeQuestionWithBoolean(
                                new DateQuestion(nonWorkingIllness.getStartDate()),
                                new DateQuestion(nonWorkingIllness.getEndDate())
                        ));
                    }
                });
    }
}
