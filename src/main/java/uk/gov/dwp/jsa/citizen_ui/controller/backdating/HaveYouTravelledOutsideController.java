package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.HaveYouTravelledOutsideForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.HaveYouTravelledOutsideQuestion;
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
@RequestMapping("/" + HaveYouTravelledOutsideController.IDENTIFIER)
public class HaveYouTravelledOutsideController extends BaseFormController<HaveYouTravelledOutsideForm> {

    public static final String IDENTIFIER = "form/backdating/have-you-travelled-outside-england-wales-scotland";
    private static final String FOR_EXAMPLE_START_DATE_TEXT = "01 09 2020";
    private static final String FOR_EXAMPLE_END_DATE_TEXT = "14 09 2020";

    public HaveYouTravelledOutsideController(final ClaimRepository claimRepository,
                                             final RoutingService routingService,
                                             final CookieLocaleResolver cookieLocaleResolver,
                                             final DateFormatterUtils dateFormatterUtils) {
        super(claimRepository,
                IDENTIFIER,
                "haveYouTravelledOutside",
                routingService,
                IDENTIFIER,
                "/form/backdating/have-you-been-in-full-time-education",
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING,
                dateFormatterUtils,
                cookieLocaleResolver);
    }

    @GetMapping
    public final String getHaveYouTravelledOutside(final Model model,
                                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false)
                                                   final String claimId,
                                                   final HttpServletRequest request) {
        addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitHaveYouTravelledOutside(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                                      @ModelAttribute("haveYouTravelledOutside")
                                                      @Valid final HaveYouTravelledOutsideForm haveYouTravelledOutsideForm,
                                                      final BindingResult bindingResult,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response,
                                                      final Model model) {
        if (bindingResult.hasErrors()) {
            addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        }
        return post(claimId, haveYouTravelledOutsideForm, bindingResult, response, model);
    }

    @Override
    public HaveYouTravelledOutsideForm getForm() {
        return new HaveYouTravelledOutsideForm(new HaveYouTravelledOutsideQuestion());
    }

    @Override
    public HaveYouTravelledOutsideForm getTypedForm() {
        return getForm();
    }

    @ModelAttribute
    public void exampleDates(final Model model) {
        model.addAttribute("exampleStartDate", FOR_EXAMPLE_START_DATE_TEXT);
        model.addAttribute("exampleEndDate", FOR_EXAMPLE_END_DATE_TEXT);
    }

    @Override
    public void updateClaim(final HaveYouTravelledOutsideForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        DateRangeQuestionWithBoolean question = form.getQuestion().getDateRangeQuestion();
        form.getQuestion().setDateRangeQuestion(question);
        super.updateClaim(form, claim, currentStepInstance, lastGuard);
    }


    @Override
    public void setFormAttrs(final HaveYouTravelledOutsideForm form, final String claimId) {
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model,
                                      final HaveYouTravelledOutsideForm form) {
        model.addAttribute(FORM_NAME, form);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final HaveYouTravelledOutsideForm form) {
        resolve(() -> claimDB.getCircumstances().getBackDating().getTravelledOutsideUk())
                .ifPresent(travelledOutsideUk -> {
                    form.getQuestion().setHasProvidedAnswer(travelledOutsideUk.getHadTravelled());
                    if (travelledOutsideUk.getHadTravelled() != null && travelledOutsideUk.getHadTravelled()) {
                        form.getQuestion().setDateRangeQuestion(new DateRangeQuestionWithBoolean(
                                new DateQuestion(travelledOutsideUk.getStartDate()),
                                new DateQuestion(travelledOutsideUk.getEndDate())
                        ));
                    }
                });
    }
}
