package uk.gov.dwp.jsa.citizen_ui.controller.outsidework;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
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
@RequestMapping("/" + HasOutsideWorkController.IDENTIFIER)
public class HasOutsideWorkController extends BaseFormController<BooleanForm<BooleanQuestion>> {

    public static final String IDENTIFIER = "form/outside-work/has-outside-work";
    public HasOutsideWorkController(final ClaimRepository claimRepository, final RoutingService routingService) {

        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/pensions/current/has-pension",
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.OUTSIDE_UK_WORK);
    }

    @Override
    public BooleanForm<BooleanQuestion> getForm() {
        return new BooleanForm(new BooleanQuestion());
    }

    @Override
    public BooleanForm<BooleanQuestion> getTypedForm() {
        return getForm();
    }


    @Override
    public void setFormAttrs(final BooleanForm<BooleanQuestion> form, final String claimId) {
        form.setTranslationKey("work.outside.uk.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final BooleanForm<BooleanQuestion> form) {
        resolve(() -> claimDB.getCircumstances().isHasNonUKWorkBenefit())
                .ifPresent(choice -> form.getQuestion().setChoice(choice));
    }

    @GetMapping
    public final String getHaveYouWorkedAbroad(
            final Model model,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String postHaveYouWorkedAbroad(
            @ModelAttribute(FORM_NAME) @Valid final BooleanForm<BooleanQuestion> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, form, bindingResult, response, model);
    }


}
