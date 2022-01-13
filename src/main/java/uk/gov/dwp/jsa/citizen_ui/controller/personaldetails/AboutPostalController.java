package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;

/**
 * Q10 Controller to confirm the claimant needs an additional postal address.
 */
@Controller
@RequestMapping("/form/personal-details/address-is-it-postal")
public class AboutPostalController extends BaseFormController<GuardForm<GuardQuestion>> {

    public static final String IDENTIFIER = "form/personal-details/address-is-it-postal";

    public AboutPostalController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/personal-details/contact/telephone",
                "/form/personal-details/postal-address",
                Section.PERSONAL_DETAILS);
    }

    @GetMapping
    public final String getPostal(final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {

        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitPostal(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> postalForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, postalForm, bindingResult, response, model);
    }

    @Override
    public GuardForm<GuardQuestion> getForm() {
        return new GuardForm(new GuardQuestion());
    }

    @Override
    public GuardForm<GuardQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final GuardForm<GuardQuestion> form, final String claimId) {
        form.setTranslationKey("personaldetails.postal.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm form) {
        form.getQuestion().setChoice(claimDB.getClaimant().hasAnotherPostalAddress());
    }

}
