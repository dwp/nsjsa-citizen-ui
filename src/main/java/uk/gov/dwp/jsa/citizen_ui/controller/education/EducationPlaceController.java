package uk.gov.dwp.jsa.citizen_ui.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.UnlimitedStringQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
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
 * Q22 Where did your education take place.
 */
@Controller
public class EducationPlaceController extends BaseFormController<StringForm> {

    public static final String IDENTIFIER = "form/education/place";

    public EducationPlaceController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                TEXT_INPUT_VIEW_NAME,
                FORM_NAME,
                routingService,
              IDENTIFIER,
                "/form/education/course-hours",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.EDUCATION);
    }

    /**
     * Q22 Renders the form for the place of education.
     *
     * @param model                 the Spring Boot MVC model
     * @param claimId               Claim's id in cache
     * @param request               the servlet request
     * @return                      view name to render
     */
    @GetMapping(path = "/form/education/place")
    public final String getView(final Model model,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false)
                                    final String claimId,
                                final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Q22 Captures submission of the place of education.
     *
     * @param stringForm            the place of the education
     * @param bindingResult         Spring Boot MVC binding result
     * @param claimId               Claim's id in cache
     * @param response              The http response
     * @param model                 the Spring Boot MVC model
     * @return                      Name of the view
     */
    @PostMapping(path = "/form/education/place")
    public final String submitForm(@Valid @ModelAttribute(FORM_NAME) final StringForm stringForm,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, stringForm, bindingResult, response, model);
    }

    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey("education.place.form.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm stringForm) {
        resolve(() -> claimDB.getCircumstances().getEducation().getInstitutionName())
                .ifPresent(phone -> stringForm.setQuestion(new StringQuestion(phone)));
    }

    @Override
    public StringForm getForm() {
        return new StringForm();
    }

    @Override
    public StringForm getTypedForm() {
        return getForm();
    }

    @InitBinder
    public void dataBinding(final WebDataBinder binder) {
        binder.registerCustomEditor(StringQuestion.class, new UnlimitedStringQuestionEditor());
    }
}
