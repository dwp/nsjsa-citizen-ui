package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/form/eligibility/working")
public class AreYouWorkingController extends BaseFormController<AreYouWorkingForm> {

    public static final String IDENTIFIER = "form/eligibility/working";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    public AreYouWorkingController(final ClaimRepository claimRepository, final RoutingService routingService,
                                   final CookieLocaleResolver cookieLocaleResolver) {

        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                Section.NONE,
                cookieLocaleResolver);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @Override
    public String nextStepBasedOn(final AreYouWorkingForm form, final Claim claim) {
        if (form.getQuestion().getAreYouWorking()) {
            return "/form/eligibility/working-over";
        }
        if  (claim.getResidenceQuestion().getUkResidence()) {
            return "/form/eligibility/eligible";
        }
          return "/form/eligibility/residence/working/ineligible";
    }

    @GetMapping
    public final String getAreYouWorking(final Model model,
                                         @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                         final HttpServletRequest request) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postAreYouWorking(final Model model,
                                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                          @ModelAttribute(FORM_NAME) @Valid final AreYouWorkingForm form,
                                          final BindingResult bindingResult,
                                          final HttpServletResponse response,
                                          final HttpServletRequest request) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public AreYouWorkingForm getForm() {
        return new AreYouWorkingForm();
    }

    @Override
    public AreYouWorkingForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final AreYouWorkingForm form) {
    }

    @Override
    public void setFormAttrs(final AreYouWorkingForm form, final String claimId) {
        form.setTranslationKey("eligibility.working.form.");
        super.setFormAttrs(form, claimId);
    }
}
