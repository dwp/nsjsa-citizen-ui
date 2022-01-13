package uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details;

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
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/current-work/details")
public class VoluntaryPaidController extends CounterFormController<GuardForm<GuardQuestion>> {

    public static final String IDENTIFIER = "form/current-work/details/get-paid";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/details/%s/get-paid";

    public VoluntaryPaidController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/current-work/details/%s/name",
                "/form/current-work/details/%s/how-often-paid",
                Section.CURRENT_WORK);
    }

    @GetMapping("/{count:[1-4]+}/get-paid")
    public String getView(@PathVariable final Integer count,
                          final Model model,
                          @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                          final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request, count);
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
        form.setTranslationKey("currentwork.voluntarypaid.");
        super.setFormAttrs(form, claimId);
    }
    @PostMapping("/{count:[1-4]+}/get-paid")
    public String submitForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> form,
                             final BindingResult bindingResult,
                             final HttpServletResponse response,
                             final HttpServletRequest request,
                             final Model model) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm<GuardQuestion> form) {
        resolve(() -> claimDB.getCircumstances().getCurrentWork().get(form.getCount() - 1).isVoluntaryJobPaid())
                .ifPresent(choice -> form.getQuestion().setChoice(choice));
    }
}
