package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
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
@RequestMapping("/" + WereYouAvailableForWorkController.IDENTIFIER)
public class WereYouAvailableForWorkController extends AvailableAndSearchingBase {
    public static final String IDENTIFIER = "form/backdating/were-you-available-for-work";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    public WereYouAvailableForWorkController(final ClaimRepository claimRepository,
                                             final RoutingService routingService,
                                             final CookieLocaleResolver cookieLocaleResolver,
                                             final DateFormatterUtils dateFormatterUtils) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/" + WereYouSearchingForWorkController.IDENTIFIER,
                dateFormatterUtils,
                cookieLocaleResolver);
        this.cookieLocaleResolver = cookieLocaleResolver;
    }

        @GetMapping
        public final String getWereYouAvailableForWork(
                final Model model,
                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                final HttpServletRequest request) {
            addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
            useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
            return get(model, claimId, request);
        }

        @PostMapping
        public final String postWereYouAvailableForWork(@ModelAttribute(FORM_NAME) @Valid final BooleanForm<BooleanQuestion> form,
                final BindingResult bindingResult,
                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                final HttpServletRequest request,
                final HttpServletResponse response,
                final Model model) {
            if (bindingResult.hasErrors()) {
                addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);
            }
            useAlternativeWelshTextBooleanPage(cookieLocaleResolver, request, model);
            return post(claimId, form, bindingResult, response, model);
        }

        @Override
        public void loadForm(final ClaimDB claimDB, final BooleanForm<BooleanQuestion> form) {
            resolve(() -> claimDB.getCircumstances().getBackDating().getWereYouAvailableForWork())
                    .ifPresent(availableForWork -> form.getQuestion().setChoice(availableForWork));
        }
}
