package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/previous-employment/employer-details")
public class ExpectPaymentController extends CounterFormController<BooleanForm> {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/expect-payment";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/expect-payment";

    public ExpectPaymentController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                BooleanForm.BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/employer-details/%s/status",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
    }

    @GetMapping("/{count:[1-4]+}/expect-payment")
    public String getView(@PathVariable final Integer count,
                          final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request, count);
    }

    @PostMapping(path = "/{count:[1-4]+}/expect-payment")
    public final String submitStatus(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                     @ModelAttribute(FORM_NAME) @Valid final BooleanForm form,
                                     final BindingResult bindingResult,
                                     final HttpServletResponse response,
                                     final HttpServletRequest request,
                                     final Model model) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
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
        form.setTranslationKey("previousemployment.expectpayment.");
        super.setFormAttrs(form, claimId);
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final BooleanForm form) {
        form.getQuestion().setChoice(resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(form.getCount() - 1)
                .isPaymentExpected())
                .orElse(null));
    }
}
