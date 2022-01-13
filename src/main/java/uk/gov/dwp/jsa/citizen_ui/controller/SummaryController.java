package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.ServicesProperties;
import uk.gov.dwp.jsa.adaptors.enums.ClaimType;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.SummaryForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;
import uk.gov.dwp.jsa.citizen_ui.services.RequiredDataService;
import uk.gov.dwp.jsa.citizen_ui.services.SummaryMappingService;
import uk.gov.dwp.jsa.citizen_ui.services.UpdateService;
import uk.gov.dwp.jsa.citizen_ui.util.date.MonthYearI8NDateFormat;
import uk.gov.dwp.jsa.citizen_ui.util.date.SimpleI8NDateFormat;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

/**
 * Form controller.
 * Handles to rendering and submission of each part of the JSA form
 */
@Controller
@RequestMapping(path = "/form/summary")
public class SummaryController extends BaseFormController<SummaryForm> {

    public static final String IDENTIFIER = "form/summary";
    private final SummaryMappingService summaryMappingService;
    private final CookieLocaleResolver cookieLocaleResolver;
    private final PensionsService pensionsService;
    private final UpdateService updateService;
    private ServicesProperties servicesProperties;
    private final ClaimRepository claimRepository;
    private final RequiredDataService requiredDataService;
    private static final String AGENT_UPDATE_BACK_REF = "/" + DeclarationController.IDENTIFIER;
    public static final String IS_AGENT = "agent";

    public SummaryController(final ClaimRepository claimRepository,
                             final RoutingService routingService,
                             final SummaryMappingService summaryMappingService,
                             final CookieLocaleResolver cookieLocaleResolver,
                             final PensionsService pensionsService,
                             final UpdateService updateService,
                             final ServicesProperties servicesProperties,
                             final RequiredDataService requiredDataService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.NONE);
        this.summaryMappingService = summaryMappingService;
        this.cookieLocaleResolver = cookieLocaleResolver;
        this.pensionsService = pensionsService;
        this.updateService = updateService;
        this.servicesProperties = servicesProperties;
        this.claimRepository = claimRepository;
        this.requiredDataService = requiredDataService;
    }

    @Override
    public SummaryForm createNewForm(final Claim claim) {
        List<ViewQuestion> map = summaryMappingService.map(claim);
        boolean hasMoreThanMaxAllowed = pensionsService.hasMoreThanMaxAllowed(claim);
        boolean canAddPension = pensionsService.canAddPension(claim);
        return new SummaryForm(map, hasMoreThanMaxAllowed, canAddPension);
    }

    @Override
    public void updateClaim(final SummaryForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        //Nothing to update during summary
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final SummaryForm form) {
        // Nothing to load.
    }

    @GetMapping
    public final String getView(final Model model,
                                @Value("${" + Constants.AGENT_MODE + "}") final boolean agentMode,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
        model.addAttribute("agentMode", agentMode);
        model.addAttribute("updateMode", isUpdateMode(claimId));
        Optional<String> backRef = Optional.ofNullable(getRoutingService().getBackRef(claimId));
        boolean isAgent = request.getParameter(IS_AGENT) != null && request.getParameter(IS_AGENT).equals("1");
        if (backRef.isPresent()) {
            if (backRef.get().equals(AGENT_UPDATE_BACK_REF) || isAgent) {
                model.addAttribute("agentUpdateClaimMode", true);
            }
        }
        getRoutingService().clearSummaryHistory(claimId);
        addDateFormatToModel(model, request);
        addCitizensClaimStartDateAndIsBackDatingToModel(request, model, claimId);

        Claim claim = getOrCreateClaim(claimRepository, claimId);
        String requiredQuestionUrl = requiredDataService.getQuestionUrl(claim);

        return requiredQuestionUrl != null ? requiredQuestionUrl : get(model, claimId, request);
    }

    @PostMapping
    public final String post(@Value("${" + Constants.AGENT_MODE + "}") final boolean agentMode,
                             @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                             @ModelAttribute(FORM_NAME) final SummaryForm summaryForm)
            throws InstanceNotFoundException {

        registerStepInstance(summaryForm, claimId);

        if (agentMode && isUpdateMode(claimId)) {
            UUID claimUUID = UUID.fromString(claimId);
            final UUID claimantId = updateService.updateClaim(claimUUID);
            return "redirect:" + getAgentUiClaimCreatedUrl(claimantId);
        } else {
            return "redirect:/form/declaration";
        }
    }

    public boolean isUpdateMode(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId) {
        return getClaimRepository()
                .findById(claimId)
                .map(Claim::getClaimType)
                .filter(ClaimType.EDIT_CLAIM::equals
                ).isPresent();
    }

    private void addDateFormatToModel(final Model model, final HttpServletRequest request) {
        Locale currentLocale = cookieLocaleResolver.resolveLocale(request);
        model.addAttribute("availabilityDateFormat", new SimpleI8NDateFormat(currentLocale));
        model.addAttribute("monthYearDateFormat", new MonthYearI8NDateFormat(currentLocale));
    }

    private String getAgentUiClaimCreatedUrl(final UUID id) {
        return servicesProperties.getAgentUiServer()
                + format(Constants.AGENT_UI_CLAIM_UPDATED_URL, id);
    }


}
