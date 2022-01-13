package uk.gov.dwp.jsa.citizen_ui.controller.backdating;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.AskedForAdviceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.AskedForAdviceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.useAlternativeWelshTextBooleanPage;

@Controller
@RequestMapping("/" + HaveYouAskedForAdviceController.IDENTIFIER)
public class HaveYouAskedForAdviceController extends BaseFormController<AskedForAdviceForm> {
    public static final String IDENTIFIER = "form/backdating/have-you-asked-for-advice";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @Autowired
    private DateFormatterUtils dateFormatterUtils;

    public HaveYouAskedForAdviceController(final ClaimRepository claimRepository,
                                           final RoutingService routingService,
                                           final CookieLocaleResolver cookieLocaleResolver,
                                           final DateFormatterUtils dateFormatterUtils) {
        super(claimRepository,
                IDENTIFIER,
                "askedForAdviceForm",
                routingService,
                IDENTIFIER,
                "/form/nino",
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING,
                dateFormatterUtils,
                cookieLocaleResolver);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

    @GetMapping
    public final String getAskedForAdviceView(
                                     final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false)
                                     final String claimId,
                                     final HttpServletRequest request) {
        addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return get(model, claimId, request);
    }

    @PostMapping
    public final String submitAskedForAdviceForm(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                        @Valid final AskedForAdviceForm askedForAdviceForm,
                                        final BindingResult bindingResult,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Model model) {
        if (bindingResult.hasErrors()) {
            addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
        }
        useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
        return post(claimId, askedForAdviceForm, bindingResult, response, model);
    }

    @Override
    public AskedForAdviceForm getForm() {
        return new AskedForAdviceForm(new AskedForAdviceQuestion());
    }

    @Override
    public AskedForAdviceForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final AskedForAdviceForm form, final String claimId) {
        form.setTranslationKey("backdating.asked.for.advice.");
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model,
                                      final AskedForAdviceForm form) {
        model.addAttribute(FORM_NAME, form);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final AskedForAdviceForm form) {
        resolve(() -> claimDB.getCircumstances().getBackDating().getAskedForAdvice())
                .ifPresent(askedForAdvice -> {
                    form.getQuestion().setHasHadAdvice(askedForAdvice.getHasAsked());
                    form.getQuestion().setValue(askedForAdvice.getAdvice());
                });
    }

}
