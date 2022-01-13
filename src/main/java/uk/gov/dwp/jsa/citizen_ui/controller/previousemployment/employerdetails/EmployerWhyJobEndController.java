package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q42 Previous Employment Why Did Job End.
 */
@Controller
public class EmployerWhyJobEndController extends CounterFormController<WhyJobEndForm> {

    public static final String PROPERTY_KEY_PREFIX = "previousemployment.employerdetails.whyended.options.";
    public static final String TRANSLATION_KEY = "previousemployment.employerdetails.whyended.";
    public static final String IDENTIFIER = "form/previous-employment/employer-details/why-end";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/why-end";

    public EmployerWhyJobEndController(final ClaimRepository claimRepository,
                                       final RoutingService routingService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/employer-details/%s/name",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
    }

    @ModelAttribute
    public void whyEndReasons(final Model model) {
        model.addAttribute("whyEndReasons", WhyJobEndedReason.values());
        model.addAttribute("propertyKeyPrefix", PROPERTY_KEY_PREFIX);
    }

    /**
     * Renders previous employer's Why Job Ended form.
     *
     * @param model Spring MVC Model
     * @param count Count
     * @param claimId Claim Id
     * @param request HttpServletRequest
     * @return Name of view
     */
    @GetMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/why-end")
    public final String getWhyJobEnd(@PathVariable final Integer count, final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                     final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    /**
     * Captures submission of the previous employer's reason for end of job form.
     *
     * @param whyJobEndForm Form object used in the form
     * @param bindingResult  Spring Boot MVC binding result
     * @param model Spring MVC Model
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param response HttpServletResponse
     * @return Name of view
     */
    @PostMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/why-end")
    public final String submitWhyJobEnd(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final WhyJobEndForm whyJobEndForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, whyJobEndForm, bindingResult, response, model);
    }

    @Override
    public WhyJobEndForm getForm() {
        WhyJobEndForm form = new WhyJobEndForm();
        form.setQuestion(new WhyJobEndQuestion());
        return form;
    }

    @Override
    public WhyJobEndForm getTypedForm() {
        return getForm();
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final WhyJobEndForm whyJobEndForm) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(whyJobEndForm.getCount() - 1))
                .ifPresent(whyJobEndedReason -> {
                    whyJobEndForm.getQuestion().setWhyJobEndedReason(
                            WhyJobEndedReason.valueOf(whyJobEndedReason.getReasonEnded())
                    );
                    whyJobEndForm.getQuestion().setDetailedReason(whyJobEndedReason.getOtherReasonDetails());
                });
    }
}
