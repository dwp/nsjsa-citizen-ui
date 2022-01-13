package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.WhyNotApplySoonerQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.WhyNotApplySoonerQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/" + WhyNotApplySoonerController.IDENTIFIER)
public class WhyNotApplySoonerController extends BaseFormController<StringForm> {
    public static final String IDENTIFIER = "form/backdating/why-not-apply-sooner";

    @Autowired private MessageSource messageSource;
    @Autowired private CookieLocaleResolver cookieLocaleResolver;

    public WhyNotApplySoonerController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/backdating/have-you-been-in-paid-work-since",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING);
    }

    /**
     * Renders the back dating "why did you not apply sooner" question.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping
    public final String getWhyNotApplySoonerPage(final Model model,
                                                 @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                                 final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Captures submission of the "why did you not apply sooner" question.
     *
     * @param form String Form
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @PostMapping
    public final String postWhyDidYouNotApplySoonerPage(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final StringForm form,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey("backdating.whynow.details.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public StringForm getForm() {
        StringForm form = new StringForm<WhyNotApplySoonerQuestion>();
        form.setQuestion(new WhyNotApplySoonerQuestion());
        return form;
    }

    @Override
    public StringForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm form) {
        resolve(() -> claimDB.getCircumstances().getBackDating().getWhyNotApplySooner())
                .ifPresent(applySooner -> form.getQuestion().setValue(applySooner));
    }

    @InitBinder
    public void dataBinding(final WebDataBinder binder) {
        binder.registerCustomEditor(StringQuestion.class, new WhyNotApplySoonerQuestionEditor());
    }
}
