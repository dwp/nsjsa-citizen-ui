package uk.gov.dwp.jsa.citizen_ui.controller.claimstart;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

import static java.lang.Boolean.TRUE;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@Controller
@RequestMapping("/" + DefaultStartDateController.IDENTIFIER)
public class DefaultStartDateController extends BaseFormController<GuardForm<GuardQuestion>> {
    public static final String IDENTIFIER = "form/default-claim-start";
    private final ClaimStartDateController claimStartDateController;
    private final CookieLocaleResolver cookieLocaleResolver;

    private DateFormatterUtils dateFormatterUtils;

    @Autowired
    public DefaultStartDateController(final ClaimRepository claimRepository,
                                      final RoutingService routingService,
                                      final ClaimStartDateController claimStartDateController,
                                      final CookieLocaleResolver cookieLocaleResolver,
                                      final DateFormatterUtils dateFormatterUtils) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/claim-start",
                "/form/nino",
                Section.BACK_DATING);
        this.claimStartDateController = claimStartDateController;
        this.cookieLocaleResolver = cookieLocaleResolver;
        this.dateFormatterUtils = dateFormatterUtils;
    }

    @ModelAttribute
    public void defaultDate(final Model model, final HttpServletRequest request) {
        LocalDate todayDate = dateFormatterUtils.getTodayDate();
        model.addAttribute("defaultDate",
                dateFormatterUtils.formatDate(request, cookieLocaleResolver, todayDate));
    }

    /**
     * Renders the Claim Start Date part of the form.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping
    public final String claimStartDate(final Model model,
                                       @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                       final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Captures submission of the Claim Start Date part of the form.
     *
     * @param form Guard Form
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return View
     */
    @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
    @PostMapping
    public final String claimStartDate(
            @Validated(ValidationSequence.class) @ModelAttribute(FORM_NAME) final GuardForm<GuardQuestion> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {
        String result = post(claimId, form, bindingResult, response, model);
        if (!getViewName().equals(result) && TRUE.equals(form.getQuestion().getChoice())) {
            setDefaultStartDate(bindingResult, claimId, response, model);
        }
        return result;
    }

    public void setDefaultStartDate(final BindingResult bindingResult,
                                    @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                    final HttpServletResponse response, final Model model) {
        ClaimStartDateForm claimStartDateForm = new ClaimStartDateForm();
        final ClaimStartDateQuestion claimStartDateQuestion = new ClaimStartDateQuestion();
        LocalDate date = LocalDate.now();
        claimStartDateQuestion.setDay(date.getDayOfMonth());
        claimStartDateQuestion.setMonth(date.getMonthValue());
        claimStartDateQuestion.setYear(date.getYear());
        claimStartDateForm.setClaimStartDateQuestion(claimStartDateQuestion);
        claimStartDateController.post(claimId, claimStartDateForm, bindingResult, response, model, false);
    }

    public GuardForm<GuardQuestion> getForm() {
        return new GuardForm<>(new GuardQuestion());
    }

    @Override
    public GuardForm<GuardQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm form) {
        // nothing to load
    }
}
