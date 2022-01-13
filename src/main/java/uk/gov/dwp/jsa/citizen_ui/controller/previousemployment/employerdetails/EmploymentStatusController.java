package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
public class EmploymentStatusController extends CounterFormController<BooleanForm> {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/status";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/status";
    private CookieLocaleResolver cookieLocaleResolver;

    public EmploymentStatusController(final ClaimRepository claimRepository, final RoutingService routingService,
                                      final CookieLocaleResolver cookieLocaleResolver) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/%s/add-work",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @GetMapping("/form/previous-employment/employer-details/{count:[1-4]+}/status")
    public String getView(@PathVariable final Integer count,
                          final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request, count);
    }


    @Override
    public BooleanForm getForm() {
        return new BooleanForm(new BooleanQuestion());
    }

    @Override
    public BooleanForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final BooleanForm form, final String claimId) {
        form.setTranslationKey("previousemployment.employmentstatus.");
        super.setFormAttrs(form, claimId);
    }
    @PostMapping("/form/previous-employment/employer-details/{count:[1-4]+}/status")
    public final String submitStatus(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                     @ModelAttribute(FORM_NAME) @Valid final BooleanForm employmentStatusForm,
                                     final BindingResult bindingResult,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response,
                                     final Model model) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, employmentStatusForm, bindingResult, response, model);
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final BooleanForm form) {

        resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(form.getCount() - 1)
                .isSelfEmployedOrDirector())
                .ifPresent(choice -> form.getQuestion().setChoice(choice));
    }
}
