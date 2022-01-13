package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.adaptors.dto.claim.Address;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Country;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.services.WelshPostcodeService;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Controller for rendering address page for the about you.
 */
@Controller
public class AboutAddressController extends BaseFormController<AddressForm> {

    public static final String IDENTIFIER = "form/personal-details/address";

    public AboutAddressController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/personal-details/address",
                "addressForm",
                routingService,
                IDENTIFIER,
                "/form/personal-details/address-is-it-postal",
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

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult, final Model model, final AddressForm form) {
        model.addAttribute("addressForm", form);
    }

    @GetMapping("/form/personal-details/address")
    public final String getAddress(final Model model,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping(path = "/form/personal-details/address")
    public final String submitAddress(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid final AddressForm addressForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model,
            final HttpServletRequest request) {

        String nextStepIdentifier;
        boolean isSectionTerminator;

        if (hasWelshPostcode(addressForm.getAddressQuestion().getPostCode())) {
            nextStepIdentifier = "/form/personal-details/language-preference";
            isSectionTerminator = false;
        } else {
            clearLanguagePreferenceQuestion(claimId);
            nextStepIdentifier = "/form/personal-details/address-is-it-postal";
            isSectionTerminator = true;
        }

        changeNextStep(nextStepIdentifier, NO_ALTERNATIVE_IDENTIFIER, Section.PERSONAL_DETAILS, isSectionTerminator);
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, addressForm, bindingResult, response, model);
    }

    @Override
    public AddressForm getForm() {
        return new AddressForm(new AddressQuestion());
    }

    @Override
    public AddressForm getTypedForm() {
        return getForm();
    }


    @Override
    public void loadForm(final ClaimDB claimDB, final AddressForm form) {
        resolve(() -> claimDB.getClaimant().getAddress())
                .ifPresent(address -> setAddressToForm(address, form.getQuestion()));
    }

    public static void setAddressToForm(final Address address, final AddressQuestion question) {
        question.setAddressLine1(address.getFirstLine());
        question.setAddressLine2(address.getSecondLine());
        question.setTownOrCity(address.getTown());
        question.setPostCode(address.getPostCode());
    }

    private boolean hasWelshPostcode(final String postcode) {
        return WelshPostcodeService.isWelshPostCode(postcode);
    }

    private void clearLanguagePreferenceQuestion(final String claimId) {
        ClaimRepository claimRepository = getClaimRepository();
        Claim claim = getOrCreateClaim(claimRepository, claimId);
        claim.setAnswer(LanguagePreferenceController.IDENTIFIER, new LanguagePreferenceQuestion());
        claimRepository.save(claim);
    }
}
