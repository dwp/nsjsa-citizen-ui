package uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits;

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
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.OtherBenefitDetailsQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.OtherBenefitDetailsQuestion;
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
@RequestMapping("/" + OtherBenefitDetailsController.IDENTIFIER)
public class OtherBenefitDetailsController extends BaseFormController<StringForm> {

    public static final String IDENTIFIER = "form/other-benefits/details";

    public OtherBenefitDetailsController(final ClaimRepository claimRepository,
                                         final RoutingService routingService) {
        super(claimRepository,
                StringForm.TEXT_AREA_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/claim-start/jury-service/have-you-been",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.OTHER_BENEFITS);
    }

    /**
     * Renders Other Benefits Details Form.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping
    public final String getOtherBenefitDetails(final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                               final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * The method submits the other benefit details form.
     * @param stringForm String Form
     * @param bindingResult Spring Boot MVC binding result
     * @param model SpringBoot MVC model
     * @param claimId Claimaint Id
     * @param response HttpServletResponse
     * @return String
     */
    @PostMapping
    public final String submitOtherBenefitDetails(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final StringForm stringForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, stringForm, bindingResult, response, model);
    }

    @Override
    public void setFormAttrs(final StringForm form, final String claimId) {
        form.setTranslationKey("other.benefits.details.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final StringForm form) {
        form.setQuestion(new StringQuestion(resolve(() ->
                claimDB.getCircumstances().getOtherBenefit().getDescription())
                .orElse(null)));
    }

    @Override
    public StringForm getForm() {
        StringForm form = new StringForm<OtherBenefitDetailsQuestion>();
        form.setQuestion(new OtherBenefitDetailsQuestion());
        return form;
    }

    @Override
    public StringForm getTypedForm() {
        return getForm();
    }

    @InitBinder
    public void dataBinding(final WebDataBinder binder) {
        binder.registerCustomEditor(StringQuestion.class, new OtherBenefitDetailsQuestionEditor());
    }

}
