package uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/" + OtherBenefitsController.IDENTIFIER)
public class OtherBenefitsController extends BaseFormController<GuardForm<GuardQuestion>> {

    private CookieLocaleResolver cookieLocaleResolver;
    public static final String IDENTIFIER = "form/other-benefits/have-you-applied";

    public OtherBenefitsController(final ClaimRepository claimRepository,
                                   final RoutingService routingService,
                                   final CookieLocaleResolver cookieLocaleResolver) {

        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/claim-start/jury-service/have-you-been",
                "/form/other-benefits/details",
                Section.OTHER_BENEFITS);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @Override
    public GuardForm<GuardQuestion> getForm() {
        return new GuardForm<>(new GuardQuestion());
    }

    @Override
    public GuardForm<GuardQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final GuardForm<GuardQuestion> form, final String claimId) {
        form.setTranslationKey("otherbenefits.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<GuardQuestion> form) {
        boolean choice = resolve(() -> claimDB.getCircumstances().getOtherBenefit()).isPresent();
        form.setQuestion(new GuardQuestion(choice));
    }

    @GetMapping
    public final String getAreYouWaiting(
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {

        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postAreYouWaiting(
            @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Model model) {

        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }
}
