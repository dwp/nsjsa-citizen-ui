package uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.JuryService;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
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
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;


/**
 * Q16 Jury service controller.
 */
@Controller
@RequestMapping(path = "/" + JuryServiceConfirmationController.IDENTIFIER)
public class JuryServiceConfirmationController extends BaseFormController<GuardForm<GuardQuestion>> {

    public static final String IDENTIFIER = "form/claim-start/jury-service/have-you-been";

    public JuryServiceConfirmationController(
            final ClaimRepository claimRepository,
            final RoutingService routingService) {
        super(claimRepository,
                BOOLEAN_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/current-work/are-you-working",
                "/form/claim-start/jury-service/start-date",
                Section.JURY_SERVICE);
    }

    /**
     * Q16 Renders the form for Jury service confirmation.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return view name to render
     */
    @GetMapping
    public final String getJuryForm(final Model model,
                                    @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                    final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);

        return get(model, claimId, request);
    }

    /**
     * Q16 Captures submission of the Jury service form.
     *
     * @param juryServiceConfirmationForm the jury service from
     * @param bindingResult               Spring Boot MVC binding result
     * @param claimId                     Claim's id in cache
     * @param response                    The http response
     * @param model                       the Spring Boot MVC model
     * @return Name of the view
     */
    @PostMapping
    public final String submitJuryForm(
            @ModelAttribute(FORM_NAME) @Valid final GuardForm<GuardQuestion> juryServiceConfirmationForm,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {

        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, juryServiceConfirmationForm, bindingResult, response, model);
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
        form.setTranslationKey("juryservice.haveyoubeen.form.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final GuardForm form) {
        Optional<JuryService> juryService = resolve(() -> claimDB.getCircumstances().getJuryService());
        form.getQuestion().setChoice(juryService.isPresent());

    }

}
