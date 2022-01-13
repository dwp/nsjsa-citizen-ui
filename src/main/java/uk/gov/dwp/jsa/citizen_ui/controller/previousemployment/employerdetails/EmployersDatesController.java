package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q41 Previous employer's dates controller.
 */
@Controller
public class EmployersDatesController extends CounterFormController<DateRangeForm> {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/dates";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/dates";
    private static final String EXCEEDING_END_DATE_THRESHOLD_LOCALE =
            "previousemployment.dates.error.end.date.exceeds.threshold";
    private static final String DATE_SIX_MONTHS_AGO_MODEL_VARIABLE = "dateSixMonthsAgo";
    private static final int END_DATE_THRESHOLD_MONTHS = 6;

    private DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(Constants.EXAMPLE_DATE_FORMAT_EXCLUDING_ZERO_PREFIXES);

    public EmployersDatesController(final ClaimRepository claimRepository,
                                    final RoutingService routingService) {
        super(claimRepository,
                "form/common/date-range",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/employer-details/%s/why-end",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
    }

    /**
     * Add example dates in the model.
     *
     * @param model the Spring Boot MVC model
     */
    @ModelAttribute
    public void exampleDates(final Model model) {
        LocalDate now = LocalDate.now();
        model.addAttribute("exampleStartDate", LocalDate.now().minusMonths(1).format(formatter));
        model.addAttribute("exampleEndDate", now.format(formatter));
        model.addAttribute(DATE_SIX_MONTHS_AGO_MODEL_VARIABLE, getSixMonthsOneDayAgo());
    }

    /**
     * Renders previous employer's dates form.
     *
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param count Count
     * @param request HttpServletRequest
     * @return Name of view
     */
    @GetMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/dates")
    public final String getName(@PathVariable final Integer count, final Model model,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    /**
     * Captures submission of the previous employer's name form.
     *
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param form Employer details questions
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @param form          Form object used in the form
     * @param bindingResult Spring Boot MVC binding result
     * @return Name of view
     */
    @PostMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/dates")
    public final String submitForm(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @ModelAttribute(FORM_NAME) @Validated(ValidationSequence.class) final DateRangeForm form,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void setFormAttrs(final DateRangeForm form, final String claimId) {
        form.setTranslationKey("previousemployment.");
        super.setFormAttrs(form, claimId);
    }


    @Override
    public DateRangeForm getForm() {
        DateRangeForm form = new DateRangeForm(new EmploymentDurationQuestion());
        form.setCount(1);
        form.setMaxCount(MAX_JOBS_ALLOWED);
        return form;
    }

    @Override
    public DateRangeForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final DateRangeForm form) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(form.getCount() - 1)
                .getStartDate())
                .ifPresent(localDate -> form.getDateRange().setStartDate(new DateQuestion(localDate)));
        resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(form.getCount() - 1)
                .getEndDate())
                .ifPresent(localDate -> form.getDateRange().setEndDate(new DateQuestion(localDate)));
    }

    private String getSixMonthsOneDayAgo() {
        return LocalDate.now().minusMonths(END_DATE_THRESHOLD_MONTHS).minusDays(1).format(formatter);
    }
}
