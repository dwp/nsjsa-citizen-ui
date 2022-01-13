package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.adaptors.dto.claim.LanguagePreference;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
public class LanguagePreferenceController extends BaseFormController<LanguagePreferenceForm> {

    public static final String IDENTIFIER = "form/personal-details/language-preference";

    public LanguagePreferenceController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                "form/personal-details/language-preference",
                "languagePreferenceForm",
                routingService,
                IDENTIFIER,
                "/form/personal-details/address-is-it-postal",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.PERSONAL_DETAILS);
        this.getStep().setSectionTerminator(true);
    }

    @GetMapping("/form/personal-details/language-preference")
    public final String getLanguagePreference(final Model model,
                                   @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletRequest request) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping("/form/personal-details/language-preference")
    public final String submitLanguagePreference(
        @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
        @Valid final LanguagePreferenceForm languagePreferenceForm,
        final BindingResult bindingResult,
        final HttpServletResponse response,
        final Model model,
        final HttpServletRequest request
    ) {
        WelshTextUtils.useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, languagePreferenceForm, bindingResult, response, model);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final LanguagePreferenceForm form) {
        resolve(() -> claimDB.getClaimant().getLanguagePreference())
                .ifPresent(languagePreference -> setLanguagePreferenceToForm(languagePreference, form.getQuestion()));
    }

    public static void setLanguagePreferenceToForm(final LanguagePreference languagePreference,
                                                   final LanguagePreferenceQuestion question) {
        question.setWelshContact(languagePreference.getWelshContact());
        question.setWelshSpeech(languagePreference.getWelshSpeech());
    }

    @Override
    public LanguagePreferenceForm getForm() {
        return new LanguagePreferenceForm(new LanguagePreferenceQuestion());
    }

    @Override
    public LanguagePreferenceForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult, final Model model, final LanguagePreferenceForm form) {
        model.addAttribute("languagePreferenceForm", form);
    }
    @Override
    public Optional<StepInstance> getLastGuard(final LanguagePreferenceForm form, final Claim claim, final StepInstance stepInstance) {
        return Optional.empty();
    }
}
