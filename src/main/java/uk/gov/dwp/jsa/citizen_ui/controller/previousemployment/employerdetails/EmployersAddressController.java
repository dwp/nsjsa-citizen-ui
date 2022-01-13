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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
public class EmployersAddressController extends CounterFormController<EmployersAddressForm> {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/address";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/address";

    public EmployersAddressController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/previous-employment/employer-details/address",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/employer-details/%s/expect-payment",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
    }

    @GetMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/address")
    public final String getAddress(@PathVariable final Integer count, final Model model,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    @PostMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/address")
    public final String submitAddress(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @ModelAttribute(FORM_NAME) @Valid final EmployersAddressForm employersAddressForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, employersAddressForm, bindingResult, response, model);
    }

    @Override
    public EmployersAddressForm getForm() {
        EmployersAddressForm form = new EmployersAddressForm();
        form.setEmployersAddressQuestion(new EmployersAddressQuestion());
        return form;
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public EmployersAddressForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final EmployersAddressForm form) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPreviousWork()
                .get(form.getCount() - 1)
                .getEmployerAddress())
                .ifPresent(address -> {
                    form.getQuestion().setAddressLine1(address.getFirstLine());
                    form.getQuestion().setAddressLine2(address.getSecondLine());
                    form.getQuestion().setTownOrCity(address.getTown());
                    form.getQuestion().setPostCode(address.getPostCode());
                });
    }
}
