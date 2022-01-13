package uk.gov.dwp.jsa.citizen_ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.OfficeSearchServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ValidationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.LocalOffice;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.status.BookingStatus;
import uk.gov.dwp.jsa.adaptors.dto.claim.status.BookingStatusType;
import uk.gov.dwp.jsa.adaptors.dto.claim.status.ValidationStatusRequest;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.NotificationService;
import uk.gov.dwp.jsa.citizen_ui.services.RequiredDataService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.BankDetailsService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.ClaimantService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static uk.gov.dwp.jsa.citizen_ui.Constants.AGENT_MODE;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.security.roles.Role.getFullName;

/**
 * This class is a controller for handling requests from declaration form.
 */
@Controller
public class DeclarationController extends BaseFormController<DeclarationForm> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationController.class);

    public static final String IDENTIFIER = "form/declaration";
    private final ClaimantService claimantService;
    private final ClaimantServiceAdaptor claimantServiceAdaptor;
    private final BankDetailsService bankDetailsService;
    private final BankDetailsServiceAdaptor bankDetailsServiceAdaptor;
    private final CircumstancesService circumstancesService;
    private final CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    private final ValidationServiceAdaptor validationServiceAdaptor;
    private final NotificationService notificationService;
    private final CookieLocaleResolver cookieLocaleResolver;
    private OfficeSearchServiceAdaptor officeSearchAdaptor;
    private final boolean agentMode;
    private Locale locale;
    private final RequiredDataService requiredDataService;
    private final ClaimRepository claimRepository;

    @Autowired
    public DeclarationController(final ClaimRepository claimRepository,
                                 final RoutingService routingService,
                                 final ClaimantService claimantService,
                                 final ClaimantServiceAdaptor claimantServiceAdaptor,
                                 final BankDetailsService bankDetailsService,
                                 final BankDetailsServiceAdaptor bankDetailsServiceAdaptor,
                                 final CircumstancesService circumstancesService,
                                 final CircumstancesServiceAdaptor circumstancesServiceAdaptor,
                                 final ValidationServiceAdaptor validationServiceAdaptor,
                                 final NotificationService notificationService,
                                 final CookieLocaleResolver cookieLocaleResolver,
                                 final OfficeSearchServiceAdaptor officeSearchAdaptor,
                                 @Value("${" + AGENT_MODE + "}") final boolean agentMode,
                                 final RequiredDataService requiredDataService) {
        super(claimRepository,
              IDENTIFIER,
              FORM_NAME,
              routingService,
              IDENTIFIER,
              "/claimant-confirmation",
              NO_ALTERNATIVE_IDENTIFIER,
              Section.DECLARATION);
        this.claimantService = claimantService;
        this.claimantServiceAdaptor = claimantServiceAdaptor;
        this.bankDetailsService = bankDetailsService;
        this.bankDetailsServiceAdaptor = bankDetailsServiceAdaptor;
        this.circumstancesService = circumstancesService;
        this.circumstancesServiceAdaptor = circumstancesServiceAdaptor;
        this.validationServiceAdaptor = validationServiceAdaptor;
        this.notificationService = notificationService;
        this.cookieLocaleResolver = cookieLocaleResolver;
        this.officeSearchAdaptor = officeSearchAdaptor;
        this.agentMode = agentMode;
        this.claimRepository = claimRepository;
        this.requiredDataService = requiredDataService;
    }

    @GetMapping(path = "/form/declaration")
    public final String getDeclaration(final Model model,
                                       @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                       final HttpServletRequest request) {
        locale = this.cookieLocaleResolver.resolveLocale(request);

        Claim claim = getOrCreateClaim(claimRepository, claimId);
        String requiredQuestionUrl = requiredDataService.getQuestionUrl(claim);

        return requiredQuestionUrl != null ? requiredQuestionUrl : get(model, claimId, request);
    }

    @PostMapping(path = "/form/declaration")
    public final String submitDeclaration(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Valid @ModelAttribute(FORM_NAME) final DeclarationForm declarationForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) throws ExecutionException, InterruptedException {

        if (bindingResult.hasErrors()) {
            LOGGER.error("Binding result errors for claimID {}", claimId);
            addTitlePrefix(model, getIdentifier(), true);
            return "form/declaration";
        }

        UUID claimUUID = UUID.fromString(claimId);
        String view = post(claimId, declarationForm, bindingResult, response, model);
        UUID claimantId = postClaimData(claimUUID);
        notificationService.notifyClaimant(claimUUID, claimantId);
        return view;
    }

    private void saveClaimantId(final String claimId,
                                final UUID claimantId) {
        Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
        claim.setClaimantId(claimantId.toString());
        getClaimRepository().save(claim);
    }

    private UUID postClaimData(final UUID claimId)
            throws ExecutionException, InterruptedException {
        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(claimId);
        if (dataFromClaimOptional.isPresent()) {
            return postData(claimId, dataFromClaimOptional.get());
        } else {
            return null;
        }
    }

    private UUID postData(final UUID claimId, final Claimant claimant)
            throws ExecutionException, InterruptedException {
        Optional<UUID> claimantIdOpt = claimantServiceAdaptor.postClaimantData(claimant).get();
        if (claimantIdOpt.isPresent()) {
            UUID claimantId = claimantIdOpt.get();
            saveClaimantId(claimId.toString(), claimantId);
            List<CompletableFuture> futures = new ArrayList<>();

            circumstancesService.getDataFromClaim(claimId).ifPresent(c ->
                futures.add(circumstancesServiceAdaptor.postCircumstancesData(c, claimantId)));
            bankDetailsService.getDataFromClaim(claimId).ifPresent(b -> {
                futures.add(bankDetailsServiceAdaptor.postBankDetailsData(b, claimantId));
            });
            futures.add(validationServiceAdaptor.updateStatus(
                    claimantId,
                    getRequest(claimant.getAddress().getPostCode())));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
            return claimantId;
        }
        return null;
    }

    private ValidationStatusRequest getRequest(final String postCode) {
        String agent = null;
        String jobCentreCode = null;
        try {
            Optional<LocalOffice> localOffice = officeSearchAdaptor.getLocalOffice(postCode).join();
            if (localOffice.isPresent()) {
                jobCentreCode = localOffice.get().getJobCentreId();
            }
        } catch (Exception x) {
            LOGGER.error("Problem communicating with Local Office Search Service", x);
        }
        if (agentMode) {
            // TODO NOTE: This should be replaced by the actual values when we have them
            agent = getFullName().orElse(null);
        }

        BookingStatus bookingStatus = new BookingStatus(
                BookingStatusType.NEW_CLAIM,
                null,
                agent,
                jobCentreCode);
        return new ValidationStatusRequest(bookingStatus, null);
    }


    @Override
    public DeclarationForm getForm() {

        DeclarationQuestion question = new DeclarationQuestion();
        if (locale != null) {
            question.setLocale(locale.getLanguage());
        }
        DeclarationForm form = new DeclarationForm();
        form.setDeclarationQuestion(question);
        return form;
    }

    @Override
    public DeclarationForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model,
                                      final DeclarationForm form) {
        if (form != null && form.getDeclarationQuestion() != null) {
            form.getDeclarationQuestion().setAgreedInError(bindingResult.hasErrors());
        }
        model.addAttribute("declarationForm", form);
    }

    @Override
    public void updateClaim(final DeclarationForm form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {

        super.updateClaim(form, claim, currentStepInstance, lastGuard);
        claim.setInitialDateOfContact(LocalDate.now());
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final DeclarationForm form) {
        Boolean agreed = resolve(() -> claimDB.getCircumstances().isDeclarationAgreed()).orElse(null);
        String currentLocale = resolve(() -> claimDB.getCircumstances().getLocale()).orElse(null);
        DeclarationQuestion question = new DeclarationQuestion();
        question.setLocale(currentLocale);
        question.setAgreed(agreed);
        form.setQuestion(question);
    }
}
