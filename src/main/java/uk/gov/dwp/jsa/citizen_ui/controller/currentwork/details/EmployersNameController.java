package uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.CurrentEmployerNameQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm.TEXT_INPUT_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller("currentEmployersNameController")
@RequestMapping("/form/current-work/details")
public class EmployersNameController extends CounterFormController<StringForm> {


    public static final String IDENTIFIER = "form/current-work/details/name";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/details/%s/name";

    public EmployersNameController(final ClaimRepository claimRepository,
                                   final RoutingService routingService) {
        super(claimRepository,
                TEXT_INPUT_VIEW_NAME,
                FORM_NAME,
                routingService,
              IDENTIFIER,
                "/form/current-work/details/%s/address",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_WORK);
    }

    /**
     * Renders current Work employers name Form.
     *
     * @param claimId Claim Id
     * @param count Count
     * @param model Spring MVC Model
     * @param request HttpServletRequest
     * @return Name of view
     */
    @GetMapping(path = "/{count:[1-4]+}/name")
    public final String getEmployersName(@PathVariable final Integer count, final Model model,
                                         @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                         final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    /**
     * The method submits the current Work employers name form.
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param stringForm String form
     * @param bindingResult Spring MVC Binding Result
     * @param response HttpServletResponse
     * @return String
     */
    @PostMapping(path = "/{count:[1-4]+}/name")
    public final String submitEmployersName(
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
        form.setTranslationKey("current.work.employers.name.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm form) {
        resolve(() -> claimDB.getCircumstances().getCurrentWork().get(form.getCount() - 1).getEmployerName())
                .ifPresent(name -> form.getQuestion().setValue(name));
    }

    @Override
    public StringForm getForm() {
        StringForm form = new StringForm();
        form.setQuestion(new CurrentEmployerNameQuestion());
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
