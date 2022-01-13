package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.CURRENT_PENSIONS;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/pensions/current/details")
public class PensionIncreaseController extends CounterFormController<GuardForm<GuardQuestion>> {
    public static final String IDENTIFIER = "form/pensions/current/details/pension-increase";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/details/%s/pension-increase";
    public static final String TRANSLATION_KEY = "pensions.current.pensionincrease.";

    public PensionIncreaseController(final ClaimRepository claimRepository,
                                     final RoutingService routingService) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/pensions/current/%s/has-another-pension",
                "/form/pensions/current/details/%s/increase-date",
                CURRENT_PENSIONS
        );
    }

    @GetMapping("/{count:[1-9]+}/pension-increase")
    public String getView(@PathVariable final Integer count,
                          final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    @PostMapping("/{count:[1-9]+}/pension-increase")
    public String submitForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> form,
                             final BindingResult bindingResult,
                             final HttpServletResponse response,
                             final Model model) {
        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
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
        form.setTranslationKey(TRANSLATION_KEY);
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<GuardQuestion> form) {
        resolve(() -> claimDB.getCircumstances().getPensions().getCurrent().get(form.getCount() - 1)
                .isHasPeriodicIncrease())
                .ifPresent(hasPeriodicIncrease -> form.getQuestion().setChoice(hasPeriodicIncrease));
    }
}
