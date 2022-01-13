package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Country;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q11 Controller to add the additional postal address.
 */
@Controller
public class AboutPostalAddressController extends BaseFormController<PostalAddressForm> {

    public static final String IDENTIFIER = "form/personal-details/postal-address";

    public AboutPostalAddressController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/personal-details/postal-address",
                "postalAddressForm",
                routingService,
                IDENTIFIER,
                "/form/personal-details/contact/telephone",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS);
    }

    /**
     * Add country options.
     *
     * @param model the Spring Boot MVC model
     */
    @ModelAttribute
    public void countries(final Model model) {
        model.addAttribute("countries", Country.values());
    }

    @GetMapping("/form/personal-details/postal-address")
    public final String getAddress(final Model model,
                               @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                               final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping(path = "/form/personal-details/postal-address")
    public final String submitAddress(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid final PostalAddressForm postalAddressForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, postalAddressForm, bindingResult, response, model);
    }

    @Override
    public PostalAddressForm getForm() {
        return new PostalAddressForm(new PostalAddressQuestion());
    }

    @Override
    public PostalAddressForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PostalAddressForm form) {
        resolve(() -> claimDB.getClaimant().getPostalAddress())
                .ifPresent(address -> {
                    AboutAddressController.setAddressToForm(address, form.getQuestion());
                });
    }

}
