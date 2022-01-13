package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.CURRENT_PENSIONS;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller("CurrentPensionProviderAddressController")
public class ProviderAddressController extends CounterFormController<ProvidersAddressForm> {
    public static final String IDENTIFIER = "form/pensions/current/details/provider-address";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/details/%s/provider-address";
    public static final String TRANSLATION_KEY = "pensions.current.providers.address.";

    public ProviderAddressController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/pensions/details/address",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/pensions/current/details/%s/payment-frequency",
                NO_ALTERNATIVE_IDENTIFIER,
                CURRENT_PENSIONS
        );
    }

    /**
     * Renders current Pension Provider Address Form.
     *
     * @param count Count
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping(path = "/form/pensions/current/details/{count:[1-9]+}/provider-address")
    public final String getProviderAddress(@PathVariable final Integer count,
                                           final Model model,
                                           @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                           final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    @Override
    public ProvidersAddressForm getForm() {
        ProvidersAddressForm form = new ProvidersAddressForm();
        form.setQuestion(new ProvidersAddressQuestion());
        return form;
    }

    @Override
    public ProvidersAddressForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final ProvidersAddressForm form, final String claimId) {
        form.setTranslationKey(TRANSLATION_KEY);
        super.setFormAttrs(form, claimId);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    /**
     * The method submits the current Pension Provider address form.
     *
     * @param addressForm Pension Providers Address Form
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return String
     */
    @PostMapping(path = "/form/pensions/current/details/{count:[1-9]+}/provider-address")
    public final String submitProviderAddress(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final ProvidersAddressForm addressForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, addressForm, bindingResult, response, model);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final ProvidersAddressForm form) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPensions()
                .getCurrent()
                .get(((ProvidersAddressForm) form).getCount() - 1)
                .getProviderAddress())
                .ifPresent(address -> {
                    form.getQuestion().setAddressLine1(address.getFirstLine());
                    form.getQuestion().setAddressLine2(address.getSecondLine());
                    form.getQuestion().setTownOrCity(address.getTown());
                    form.getQuestion().setPostCode(address.getPostCode());
                });
    }
}
