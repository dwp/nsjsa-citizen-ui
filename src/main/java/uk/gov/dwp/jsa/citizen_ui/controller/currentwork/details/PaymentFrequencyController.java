package uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.EmploymentPaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyForm;
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
public class PaymentFrequencyController extends CounterFormController<PaymentFrequencyForm> {
    private static final String VIEW_NAME = "form/current-work/how-often-paid";
    public static final String IDENTIFIER = "form/current-work/details/how-often-paid";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/details/%s/how-often-paid";
    public static final String NEXT_IDENTIFIER = "/form/current-work/details/%s/name";

    public PaymentFrequencyController(final ClaimRepository claimRepository, final RoutingService routingService) {

        super(claimRepository,
                VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                NEXT_IDENTIFIER,
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_WORK);
    }


    @Override
    public PaymentFrequencyForm getForm() {
        PaymentFrequencyForm form = new PaymentFrequencyForm();
        form.setQuestion(new EmploymentPaymentFrequencyQuestion());
        return form;
    }

    @Override
    public PaymentFrequencyForm getTypedForm() {
        return getForm();
    }

    @GetMapping("/form/current-work/details/{count:[1-4]+}/how-often-paid")
    public final String getHowOftenPaid(
            @PathVariable final Integer count,
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    @PostMapping("/form/current-work/details/{count:[1-4]+}/how-often-paid")
    public final String postHowOftenPaid(
            @ModelAttribute(FORM_NAME) @Valid final PaymentFrequencyForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {
        setModelAttributesIfInvalidPaymentFrequency(bindingResult, model, "employment.amount.invalid");
        return post(claimId, form, bindingResult, response, model);
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void setFormAttrs(final PaymentFrequencyForm form, final String claimId) {
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PaymentFrequencyForm form) {
        resolve(() -> claimDB.getCircumstances().getCurrentWork().get(form.getCount() - 1))
                .ifPresent(currentWork -> {
                    if (isNotEmpty(currentWork.getPaymentFrequency())) {
                        form.getQuestion().
                                setPaymentFrequency(PaymentFrequency.valueOf(currentWork.getPaymentFrequency()));
                        form.getQuestion().setSelectedPaymentAmounts(currentWork.getNetPay());
                    }
                });
    }
}
