package uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.claimstart.JuryServiceDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q17 Jury Service Dates controller.
 */
@Controller
public class JuryServiceDatesController extends BaseFormController<DateRangeForm> {

    public static final String IDENTIFIER                  = "form/claim-start/jury-service/start-date";
    public static final String FOR_EXAMPLE_END_DATE_TEXT   = "31 1 2020";
    public static final String FOR_EXAMPLE_START_DATE_TEXT = "31 1 2019";
    private static final int END_DATE_THRESHOLD_YEARS = 1;
    private static final String END_DATE_THRESHOLD_MODEL_VARIABLE = "dateOneYearAndOneDayAgo";

    private DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(Constants.EXAMPLE_DATE_FORMAT_EXCLUDING_ZERO_PREFIXES);

    public JuryServiceDatesController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/common/date-range",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/current-work/are-you-working",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.JURY_SERVICE);
    }

    /**
     * Add example dates in the model.
     *
     * @param model the Spring Boot MVC model
     */
    @ModelAttribute
    public void exampleDates(final Model model) {
        model.addAttribute("exampleStartDate", FOR_EXAMPLE_START_DATE_TEXT);
        model.addAttribute("exampleEndDate", FOR_EXAMPLE_END_DATE_TEXT);
        model.addAttribute(END_DATE_THRESHOLD_MODEL_VARIABLE, getDateOneYearAndOneDayAgo());
    }

    /**
     * Q17 Renders the form for Jury service dates.
     *
     * @param model   the Spring Boot MVC model
     * @param claimId Claim's id in cache
     * @param request HttpServletRequest
     * @return view name to render
     */
    @GetMapping(path = "/form/claim-start/jury-service/start-date")
    public final String getJuryForm(final Model model,
                                    @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                    final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Q16 Captures submission of the Jury service dates form.
     *
     * @param form          form backing object
     * @param bindingResult Spring Boot MVC binding result
     * @param claimId       Claim's id in cache
     * @param response      The http response
     * @param model         the Spring Boot MVC model
     * @return Name of the view
     */
    @PostMapping(path = "/form/claim-start/jury-service/start-date")
    public final String submitJuryForm(
            @ModelAttribute(FORM_NAME) @Validated(ValidationSequence.class) final DateRangeForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, form, bindingResult, response, model);
    }


    @Override
    public void setFormAttrs(final DateRangeForm form, final String claimId) {
        form.setTranslationKey("juryservice.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final DateRangeForm form) {
        resolve(() -> claimDB.getCircumstances().getJuryService())
                .ifPresent(juryService -> {
                    if (nonNull(juryService.getStartDate())) {
                        form.getQuestion().setStartDate(new DateQuestion(juryService.getStartDate()));
                    }
                    if (nonNull(juryService.getEndDate())) {
                        form.getQuestion().setEndDate(new DateQuestion(juryService.getEndDate()));
                    }
                });
    }

    @Override
    public DateRangeForm getForm() {
        JuryServiceDurationQuestion question = new JuryServiceDurationQuestion();
        DateRangeForm form = new DateRangeForm();
        form.setCount(1);
        form.setDateRange(question);
        return form;
    }

    @Override
    public DateRangeForm getTypedForm() {
        return getForm();
    }

    private String getDateOneYearAndOneDayAgo() {
        return LocalDate.now().minusYears(END_DATE_THRESHOLD_YEARS).minusDays(1).format(formatter);
    }

}
