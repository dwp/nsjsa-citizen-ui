package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.NameStringShortQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionsProviderQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm.TEXT_INPUT_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller("CurrentPensionProviderNameController")
@RequestMapping("/form/pensions/current/details")
public class ProviderNameController extends CounterFormController<StringForm> {

    public static final String IDENTIFIER = "form/pensions/current/details/provider-name";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/details/%s/provider-name";
    public static final String TRANSLATION_KEY = "pensions.current.providers.name.";

    public ProviderNameController(final ClaimRepository claimRepository,
                                   final RoutingService routingService) {
        super(claimRepository,
                TEXT_INPUT_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/pensions/current/details/%s/provider-address",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_PENSIONS);
    }

    /**
     * Renders current Pension Provider name Form.
     *
     * @param count Count
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping(path = "/{count:[1-9]+}/provider-name")
    public final String getProviderName(@PathVariable final Integer count, final Model model,
                                         @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                         final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    /**
     * The method submits the current Pension Provider name form.
     *
     * @param stringForm Stringform
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return String
     */
    @PostMapping(path = "/{count:[1-9]+}/provider-name")
    public final String submitProviderName(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final StringForm stringForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, stringForm, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey(TRANSLATION_KEY);
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm form) {
        resolve(() -> claimDB.getCircumstances().getPensions().getCurrent().get(form.getCount() - 1).getProviderName())
                .ifPresent(name -> form.getQuestion().setValue(name));
    }

    @Override
    public StringForm getForm() {
        StringForm form = new StringForm();
        form.setQuestion(new PensionsProviderQuestion());
        return form;
    }

    @Override
    public StringForm getTypedForm() {
        return getForm();
    }

    @InitBinder
    public void dataBinding(final WebDataBinder binder) {
        binder.registerCustomEditor(StringQuestion.class, new NameStringShortQuestionEditor());
    }
}
