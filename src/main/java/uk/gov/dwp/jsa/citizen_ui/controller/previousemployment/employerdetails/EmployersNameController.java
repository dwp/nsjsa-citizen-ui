package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

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
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.NameStringShortQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.PreviousEmployerNameQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm.TEXT_INPUT_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q43 Previous employer's name controller.
 */
@Controller
public class EmployersNameController extends CounterFormController<StringForm> {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/name";
    public static final String IDENTIFIER_TEMPLATE = "form/previous-employment/employer-details/%s/name";

    public EmployersNameController(final ClaimRepository claimRepository,
                                   final RoutingService routingService) {
        super(claimRepository,
                TEXT_INPUT_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/previous-employment/employer-details/%s/address",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PREVIOUS_EMPLOYMENT);
    }

    /**
     * Renders previous employer's name form.
     *
     * @param model Page model
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param request HttpServletRequest
     * @param count Count
     * @return Name of view
     */
    @GetMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/name")
    public final String getName(@PathVariable final Integer count, final Model model,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }


    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey("previousemployment.employerdetails.name.");
        super.setFormAttrs(form, claimId);
    }

    /**
     * Captures submission of the previous employer's name form.
     *
     * @param stringForm        Form object used in the form
     * @param bindingResult     Spring Boot MVC binding result
     * @param model Spring MVC Model
     * @param claimId Claim Id
     * @param response HttpServletResponse
     * @return Name of view
     */
    @PostMapping(path = "/form/previous-employment/employer-details/{count:[1-4]+}/name")
    public final String submitName(
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
    public StringForm getForm() {
        StringForm form = new StringForm();
        form.setQuestion(new PreviousEmployerNameQuestion());
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

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm form) {
        resolve(() -> claimDB.getCircumstances().getPreviousWork().get(form.getCount() - 1).getEmployerName())
                .ifPresent(name -> form.getQuestion().setValue(name));
    }
}
