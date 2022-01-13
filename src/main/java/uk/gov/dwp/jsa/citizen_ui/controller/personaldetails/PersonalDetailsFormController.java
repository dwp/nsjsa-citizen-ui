package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

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
import uk.gov.dwp.jsa.citizen_ui.controller.editors.NameStringShortQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * PersonalDetails controller. Handles to rendering and submission of each part of the JSA form.
 */
@Controller
public class PersonalDetailsFormController extends BaseFormController<PersonalDetailsForm> {

    public static final String IDENTIFIER = "form/personal-details";

    public PersonalDetailsFormController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/personal-details/personal-details",
                "personalDetailsForm",
                routingService,
                IDENTIFIER,
                "/form/date-of-birth",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS);
    }

    /**
     * Renders the Personal Details Form.
     * @param model   the Spring Boot MVC model
     * @param claimId Claim's id in cache
     * @param request HttpServletRequest
     * @return String
     */
    @GetMapping(path = "/form/personal-details")
    public final String getPersonalDetails(final Model model,
                                           @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                           final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Processed the personal details data and saves them or throws Error.
     * @param personalDetailsForm Model capturing the response of personal details
     *                        step
     * @param bindingResult   Spring Boot MVC binding result
     * @param model SpringBoot MVC model
     * @param claimId Claimaint Id
     * @param response HttpServletResponse
     * @return String
     */
    @PostMapping("/form/personal-details")
    public final String submitPersonalDetails(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @ModelAttribute("personalDetailsForm") @Valid final PersonalDetailsForm personalDetailsForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, personalDetailsForm, bindingResult, response, model);
    }

    @Override
    public PersonalDetailsForm getForm() {
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());

        PersonalDetailsForm personalDetailsForm = new PersonalDetailsForm();
        personalDetailsForm.setPersonalDetailsQuestion(question);
        return personalDetailsForm;
    }

    @Override
    public PersonalDetailsForm getTypedForm() {
        return getForm();
    }

    @InitBinder
    public void dataBinding(final WebDataBinder binder) {
        binder.registerCustomEditor(StringQuestion.class, new NameStringShortQuestionEditor());
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PersonalDetailsForm personalDetailsForm) {
        resolve(() -> claimDB.getClaimant().getName()).ifPresent(name -> {
            personalDetailsForm.getPersonalDetailsQuestion().getFirstNameQuestion().setValue(name.getFirstName());
            personalDetailsForm.getPersonalDetailsQuestion().getLastNameQuestion().setValue(name.getLastName());
            if (isNotEmpty(name.getTitle())) {
                personalDetailsForm.getPersonalDetailsQuestion()
                        .getTitleQuestion().setUserSelectionValue(TitleEnum.valueOf(name.getTitle()));
            }
        });
    }
}
