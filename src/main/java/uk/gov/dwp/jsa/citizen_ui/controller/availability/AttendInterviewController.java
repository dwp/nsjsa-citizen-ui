package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.InterviewAvailability;
import uk.gov.dwp.jsa.citizen_ui.util.date.SimpleI8NDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q88 Why you can't attend the job centre interview.
 */
@Controller
public class AttendInterviewController extends BaseFormController<AttendInterviewForm> {

    public static final String IDENTIFIER = "form/availability/availability";
    private static final String VIEW_NAME = "form/availability/availability";
    private InterviewAvailability interviewAvailability;
    private CookieLocaleResolver cookieLocaleResolver;
    private ClaimRepository claimRepository;
    private static final StepInstance STEP_INSTANCE = new StepInstance(
            new Step(VIEW_NAME, "/form/summary", NO_ALTERNATIVE_IDENTIFIER, Section.NONE),
            0, false, false, false);

    public AttendInterviewController(final ClaimRepository claimRepository,
                                     final RoutingService routingService,
                                     final InterviewAvailability interviewAvailability,
                                     final CookieLocaleResolver cookieLocaleResolver) {
        super(claimRepository,
                VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/summary",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.AVAILABILITY);
        this.claimRepository = claimRepository;
        this.interviewAvailability = interviewAvailability;
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @Override
    public AttendInterviewForm getForm() {
        AttendInterviewQuestion interviewQuestion = new AttendInterviewQuestion();
        interviewQuestion.setDaysNotToAttend(interviewAvailability.createWorkingDays());
        return new AttendInterviewForm(interviewQuestion, new SimpleI8NDateFormat(Locale.getDefault()));
    }

    @Override
    public AttendInterviewForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final AttendInterviewForm form) {
        form.getQuestion().setDaysNotToAttend(resolve(() -> claimDB
                .getCircumstances()
                .getAvailableForInterview()
                .getDaysNotAvailable()
                .stream()
                .map(day -> {
                    return new DayTransformer(new ReasonTransformer()).transform(day);
                })
                .collect(toList()))
                .orElse(EMPTY_LIST));
    }

    /**
     * Q88 Renders the form for the interview availability.
     *
     * @param claimId Claimant Id
     * @param httpRequest HttpServletRequest
     * @param model SpringBoot MVC model
     * @return Name of view
     */
    @GetMapping("/form/availability/availability")
    public final String getAttendInterview(
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest httpRequest) {
        Claim claim = getOrCreateClaim(claimRepository, claimId);
        Locale currentLocale = cookieLocaleResolver.resolveLocale(httpRequest);
        AttendInterviewForm form = createNewForm(claim, currentLocale);
        getRoutingService().arrivedOnPage(claim.getId(), STEP_INSTANCE);
        setEditMode(httpRequest, form);
        model.addAttribute(FORM_NAME, form);
        setFormAttrs(form, claim.getId());
        addTitlePrefix(model, getIdentifier(), false);
        return VIEW_NAME;

    }

    public AttendInterviewForm createNewForm(final Claim claim, final Locale locale) {
        AttendInterviewForm form = super.createNewForm(claim);
        form.setDateFormat(new SimpleI8NDateFormat(locale));
        return form;
    }

    /**
     * Q88 Captures the submission for the interview availability.
     *
     * @param claimId Claimant Id
     * @param attendInterviewForm Questions related to interview attendance
     * @param bindingResult Spring MVC Response
     * @param httpRequest HttpServletRequest
     * @param response HttpServletResponse
     * @param model Spring MVC Model
     * @param claimId Claim Id
     * @return Name of view
     */
    @PostMapping(path = "/form/availability/availability")
    public final String submitAttendInterview(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final AttendInterviewForm attendInterviewForm,
            final BindingResult bindingResult,
            final HttpServletRequest httpRequest,
            final HttpServletResponse response,
            final Model model) {
        Locale currentLocale = cookieLocaleResolver.resolveLocale(httpRequest);
        attendInterviewForm.setDateFormat(new SimpleI8NDateFormat(currentLocale));
        addTitlePrefix(model, getIdentifier(), true);
        return post(claimId, attendInterviewForm, bindingResult, response, model);
    }

}
