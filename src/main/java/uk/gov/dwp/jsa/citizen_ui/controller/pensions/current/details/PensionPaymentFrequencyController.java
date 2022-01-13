package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionsPaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
public class PensionPaymentFrequencyController extends CounterFormController<PaymentFrequencyForm> {
    private static final String VIEW_NAME = "form/pensions/payment-frequency";
    public static final String IDENTIFIER = "form/pensions/current/details/payment-frequency";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/details/%s/payment-frequency";
    public static final String NEXT_IDENTIFIER = "/form/pensions/current/details/%s/pension-increase";

    public static final String TRANSLATION_KEY = "common.paymentfrequency.";

    public PensionPaymentFrequencyController(final ClaimRepository claimRepository,
                                             final RoutingService routingService) {
        super(claimRepository,
                VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                NEXT_IDENTIFIER,
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_PENSIONS);
    }

    @Override
    public PaymentFrequencyForm getForm() {
        PaymentFrequencyForm form = new PaymentFrequencyForm();
        form.setQuestion(new PensionsPaymentFrequencyQuestion());
        return form;
    }

    @Override
    public PaymentFrequencyForm getTypedForm() {
        return getForm();
    }

    @GetMapping("/form/pensions/current/details/{count:[1-9]+}/payment-frequency")
    public final String getHowOftenPaid(
            @PathVariable final Integer count,
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void setFormAttrs(final PaymentFrequencyForm form, final String claimId) {
        form.setTranslationKey(TRANSLATION_KEY);
        super.setFormAttrs(form, claimId);
    }

    @PostMapping("/form/pensions/current/details/{count:[1-9]+}/payment-frequency")
    public final String postHowOftenPaid(
            @ModelAttribute(FORM_NAME) @Valid final PaymentFrequencyForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {
        setModelAttributesIfInvalidPaymentFrequency(bindingResult, model,
                "pensions.current.paymentfrequency.invalid.error");
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PaymentFrequencyForm form) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPensions()
                .getCurrent()
                .get(form.getCount() - 1))
                .ifPresent(pensionDetail -> {
                    if (isNotEmpty(pensionDetail.getPaymentFrequency())) {
                        form.getQuestion().
                                setPaymentFrequency(PaymentFrequency.valueOf(pensionDetail.getPaymentFrequency()));
                        form.getQuestion().setSelectedPaymentAmounts(pensionDetail.getGrossPay());
                    }
                });
    }
}
