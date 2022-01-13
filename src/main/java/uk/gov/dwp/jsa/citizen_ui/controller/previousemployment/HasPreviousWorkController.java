package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment;

import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
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
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/" + HasPreviousWorkController.IDENTIFIER)
public class HasPreviousWorkController extends BaseFormController<GuardForm<GuardQuestion>> {

    public static final String IDENTIFIER = "form/previous-employment/has-previous-work";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    public HasPreviousWorkController(final ClaimRepository claimRepository, final RoutingService routingService,
                                     final CookieLocaleResolver cookieLocaleResolver) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/" + HasOutsideWorkController.IDENTIFIER,
                "/form/previous-employment/employer-details/1/dates",
                Section.PREVIOUS_EMPLOYMENT);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @GetMapping
    public String getView(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final Model model,
            final HttpServletRequest request) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public String setHasPreviousWork(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid
            @ModelAttribute(FORM_NAME) final GuardForm<GuardQuestion> hasPreviousWorkForm,
            final BindingResult bindingResult,
            final HttpServletRequest request,
            final HttpServletResponse httpServletResponse,
            final Model model) {
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, hasPreviousWorkForm, bindingResult, httpServletResponse, model);
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
        form.setTranslationKey("previousemployment.hasPreviousWork.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm form) {
        form.getQuestion().setChoice(!claimDB.getCircumstances().getPreviousWork().isEmpty());
    }
}
