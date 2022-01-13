package uk.gov.dwp.jsa.citizen_ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.Uuid;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.thymeleaf.util.StringUtils.isEmpty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.Constants.TITLE_PREFIX;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.NONE;

public abstract class BaseFormController<T extends Form> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFormController.class);

    public static final String FORM_NAME                          = "form";
    protected static final String MAX_LENGTH                      = "maxLength";
    public static final String EDIT_PARAMETER                     = "edit";
    private static final String FORM_POST_URL                     = "formPostUrl";
    private static final String CITIZEN_CLAIM_START_VIEW_VARIABLE = "citizensClaimStartDate";

    private final String modelName;
    private final ClaimRepository claimRepository;
    protected final String viewName;
    protected final RoutingService routingService;
    protected final String identifier;
    private Step step;

    private static final String AGENT_UPDATE_BACK_REF = "/" + DeclarationController.IDENTIFIER;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    protected CookieLocaleResolver cookieLocaleResolver;
    @Autowired
    private DateFormatterUtils dateFormatterUtils;

    protected Claim getOrCreateClaim(final ClaimRepository claimRepository, final String claimId) {
        Claim claim = isEmpty(claimId) ? null : claimRepository.findById(claimId).orElse(null);
        if (claim == null) {
            claim = new Claim();
            claimRepository.save(claim);
        }
        return claim;
    }

    public static void addClaimIdCookie(final Claim claim, final HttpServletResponse httpServletResponse) {
        String claimId = claim.getId();

        addCookie(COOKIE_CLAIM_ID, sanitiseUuid(claimId), httpServletResponse);
    }

    public static void addCookie(final String key, final String value, final HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        httpServletResponse.addCookie(cookie);
    }

    public BaseFormController(
            final ClaimRepository claimRepository,
            final String viewName,
            final String modelName,
            final RoutingService routingService,
            final String identifier,
            final String nextStepIdentifier,
            final String alternateStepIdentifier,
            final Section section) {
        this.claimRepository = claimRepository;
        this.viewName = viewName;
        this.modelName = modelName;
        this.routingService = routingService;
        this.identifier = identifier;
        this.step = new Step(identifier, nextStepIdentifier, alternateStepIdentifier, section);
        routingService.registerStep(step);
    }
    /*
        Use this constructor within nextStepBasedOn
     */
    public BaseFormController(
            final ClaimRepository claimRepository,
            final String viewName,
            final String modelName,
            final RoutingService routingService,
            final String identifier,
            final Section section,
            final CookieLocaleResolver cookieLocaleResolver) {
        this.claimRepository = claimRepository;
        this.viewName = viewName;
        this.modelName = modelName;
        this.routingService = routingService;
        this.identifier = identifier;
        this.step = new Step(identifier, "", "", section);
        this.cookieLocaleResolver = cookieLocaleResolver;
        routingService.registerStep(step);
    }

    public BaseFormController(
            final ClaimRepository claimRepository,
            final String viewName,
            final String modelName,
            final RoutingService routingService,
            final String identifier,
            final String nextStepIdentifier,
            final String alternateStepIdentifier,
            final Section section,
            final DateFormatterUtils dateFormatterUtils,
            final CookieLocaleResolver cookieLocaleResolver) {
        this.claimRepository = claimRepository;
        this.viewName = viewName;
        this.modelName = modelName;
        this.routingService = routingService;
        this.identifier = identifier;
        this.step = new Step(identifier, nextStepIdentifier, alternateStepIdentifier, section);
        this.dateFormatterUtils = dateFormatterUtils;
        this.cookieLocaleResolver = cookieLocaleResolver;
        routingService.registerStep(step);
    }

    @ModelAttribute(FORM_POST_URL)
    public String getFormPostUrl() {
        return "/" + identifier;
    }

    @ModelAttribute(FORM_NAME)
    public Form getForm() {
        return null;
    }

    public T getTypedForm() {
        return null;
    }

    public String get(final Model model, final String claimId, final HttpServletRequest request) {
        String sanitisedClaimId = sanitiseUuid(claimId);
        Claim claim = getOrCreateClaim(claimRepository, sanitisedClaimId);
        T form = createNewForm(claim);
        StepInstance stepInstance = new StepInstance(
                form.isAGuard(),
                form.isGuardedCondition(),
                routingService.getStep(identifier).orElse(null));
        routingService.arrivedOnPage(sanitisedClaimId, stepInstance);
        setFormAttrs(form, sanitisedClaimId);
        setEditMode(request, form);
        model.addAttribute(modelName, form);
        addTitlePrefix(model, identifier, false);
        return viewName;
    }

    public String post(final String claimId, final T form,
                       final BindingResult bindingResult,
                       final HttpServletResponse httpServletResponse, final Model model) {
        return post(claimId, form, bindingResult, httpServletResponse, model, true);
    }

    public String post(final String claimId, final T form,
                       final BindingResult bindingResult,
                       final HttpServletResponse httpServletResponse,
                       final Model model,
                       final boolean trackStep) {

        String santisedClaimId = sanitiseUuid(claimId);
        setFormAttrs(form, santisedClaimId);
        model.addAttribute(modelName, form);
        if (bindingResult.hasErrors()) {
            setErrorFieldsOnModel(bindingResult, model, form);
            addTitlePrefix(model, identifier, true);
            return viewName;
        }

        Claim claim = getOrCreateClaim(claimRepository, santisedClaimId);

        resolveNextSteps(form, claim);

        StepInstance stepInstance = registerStepInstance(form, claim.getId(), trackStep);

        Optional<StepInstance> lastGuard = getLastGuard(form, claim, stepInstance);

        saveOrUpdateClaim(stepInstance, claim, form, lastGuard);

        addClaimIdCookie(claim, httpServletResponse);
        return getNextPath(claim, form, stepInstance);
    }

    public String nextStepBasedOn(final T form, final Claim claim) {
        return null;
    }

    private void resolveNextSteps(final T form, final Claim claim) {
        String nextStep = nextStepBasedOn(form, claim);
        if (StringUtils.isNoneEmpty(nextStep)) {
            this.changeNextStep(
                    nextStep,
                    NO_ALTERNATIVE_IDENTIFIER,
                    step.getSection(),
                    step.isSectionTerminator()
            );
        }
    }

    protected StepInstance registerStepInstance(final T form, final String claimId) {
        return registerStepInstance(form, claimId, true);
    }

    protected StepInstance registerStepInstance(final T form, final String claimId, final boolean trackStep) {
        int count = 0;
        boolean isAGuard = false;
        boolean isGuardedCondition = false;
        boolean hasNoGuard = false;
        EditMode editMode = EditMode.NONE;

        if (form != null) {
            isAGuard = form.isAGuard();
            isGuardedCondition = form.isGuardedCondition();
            editMode = form.getEdit();
            hasNoGuard = form.hasNoGuard();
        }

        if (form instanceof AbstractCounterForm && form.isCounterForm()) {
            count = ((AbstractCounterForm) form).getCount();
        }

        StepInstance stepInstance = new StepInstance(
                routingService.getStep(identifier).orElse(null),
                count,
                isAGuard,
                isGuardedCondition,
                hasNoGuard);

        stepInstance.setEdit(editMode);
        if (trackStep) {
            routingService.leavePage(claimId, stepInstance);
        }
        return stepInstance;
    }

    public Optional<StepInstance> getLastGuard(final T form, final Claim claim, final StepInstance stepInstance) {
        StepInstance stepInstanceCopy = new StepInstance(stepInstance.getStep(),
                stepInstance.getCounter(),
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard()
        );
        stepInstanceCopy.setEdit(form.getEdit());
        return routingService.getLastGuard(claim.getId(), stepInstanceCopy);
    }

    public void saveOrUpdateClaim(final StepInstance stepInstance, final Claim claim, final T form,
                                  final Optional<StepInstance> lastGuard, final boolean save) {
        // check if answer changed before updating
        Optional<Question> optionalQuestion = claim.get(stepInstance);
        boolean shouldUpdate = true;
        if (optionalQuestion.isPresent()) {
            if (optionalQuestion.get() instanceof GuardQuestion) {
                GuardQuestion formQuestion = (GuardQuestion) form.getQuestion();
                GuardQuestion guardQuestion = (GuardQuestion) optionalQuestion.get();
                if (formQuestion.getChoice().equals(guardQuestion.getChoice())) {
                    shouldUpdate = false;
                }
            } else if (optionalQuestion.get() instanceof MultipleOptionsQuestion) {
                MultipleOptionsQuestion formQuestion = (MultipleOptionsQuestion) form.getQuestion();
                MultipleOptionsQuestion guardQuestion = (MultipleOptionsQuestion) optionalQuestion.get();
                if (formQuestion.getUserSelectionValue().equals(guardQuestion.getUserSelectionValue())) {
                    shouldUpdate = false;
                }
            }
        }

        if (shouldUpdate) {
            updateClaim(form, claim, stepInstance, lastGuard);
            if (save) {
                claimRepository.save(claim);
            }
        }
    }

    public void saveOrUpdateClaim(final StepInstance stepInstance, final Claim claim, final T form,
                                  final Optional<StepInstance> lastGuard) {
        saveOrUpdateClaim(stepInstance, claim, form, lastGuard, true);
    }

    public String getNextPath(final Claim claim, final T form, final StepInstance stepInstance) {
        String nextStep = routingService.getNext(stepInstance);
        return "redirect:" + nextStep + getEditParam(form) + getAgentModeParam(form);
    }

    public T createNewForm(final Claim claim) {
        T form = getTypedForm();
        StepInstance stepInstance = new StepInstance(routingService.getStep(identifier).orElse(null),
                0, // Always 0 because we have no loop
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard()
        );

        Optional<Question> questionOpt = claim.get(stepInstance);
        if (questionOpt.isPresent()) {
            form.setQuestion(questionOpt.get());
        }
        return form;
    }

    /**
     * @param form          Form
     * @param model         Model
     * @param bindingResult BindingResult
     * @deprecated Create an empty default as this will be removed soon
     */
    @Deprecated
    public void setErrorFieldsOnModel(
            final BindingResult bindingResult,
            final Model model,
            final T form) {
        // Create an empty default as this will be removed soon
    }

    public void updateClaim(final T form, final Claim claim, final StepInstance currentStepInstance,
                            final Optional<StepInstance> lastGuard) {
        claim.save(currentStepInstance, form.getQuestion(), lastGuard);
    }

    public void setFormAttrs(final T form, final String claimId) {
        form.setBackRef(routingService.getBackRef(claimId));
    }

    public ClaimRepository getClaimRepository() {
        return claimRepository;
    }

    String getModelName() {
        return modelName;
    }

    public String getViewName() {
        return viewName;
    }

    public String postUrlTemplate() {
        return identifier;
    }

    public void deleteInstance(final String identifier, final Claim claim, final int count, final int loopLimit) {
        Step tempStep = new Step(identifier, NO_ALTERNATIVE_IDENTIFIER, NO_ALTERNATIVE_IDENTIFIER, NONE);
        StepInstance stepInstance = new StepInstance(tempStep, count, false, false, false);
        claim.delete(stepInstance, loopLimit);
        claimRepository.save(claim);
    }

    protected void setEditMode(final HttpServletRequest request, final T form) {
        String editMode = request.getParameter(EDIT_PARAMETER);
        if (editMode != null) {
            form.setEdit(EditMode.valueOf(editMode));
        }
    }

    protected String getEditParam(final T form) {
        return form.getEdit() != null ? "?edit=" + form.getEdit().toString() : "";
    }

    protected String getAgentModeParam(final T form) {
        String param = "";
        if (StringForm.class.isAssignableFrom(form.getClass())) {
            String backRef = ((StringForm) form).getBackRef();
            if (backRef != null && backRef.equals(AGENT_UPDATE_BACK_REF)) {
                param = "&agent=1";
            }
        }
        return param;
    }

    protected RoutingService getRoutingService() {
        return routingService;
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract void loadForm(final ClaimDB claimDB, final T form);

    public Step getStep() {
        return this.step;
    }

    public void setStep(final Step step) {
        this.step = step;
    }

    protected static String sanitiseUuid(final String uuid) {
        return Uuid.sanitiseUuid(uuid);
    }

    protected void addTitlePrefix(final Model model, final String endpoint, final boolean isBadPostRequest) {
        Map<String, String> pageTitles = routingService.getKeyValuePairForPageTitles();

        if (pageTitles.containsKey(endpoint)) {
            String localLocation = pageTitles.get(endpoint);
            HttpServletRequest request = getCurrentHttpRequest();
            String message = messageSource.getMessage(localLocation, new Object[0], "",
                    cookieLocaleResolver.resolveLocale(request));
            String titlePrefix = isBadPostRequest ? "Error: " + message : message;
            model.addAttribute(TITLE_PREFIX, titlePrefix);
        }
    }

    protected void changeNextStep(
            final String nextStepIdentifier,
            final String alternateStepIdentifier,
            final Section section,
            final boolean sectionTerminator
    ) {
        routingService.deregisterStep(this.step);
        this.step = new Step(identifier, nextStepIdentifier, alternateStepIdentifier, section);
        this.step.setSectionTerminator(sectionTerminator);
        routingService.registerStep(step);
    }

    public static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    protected void addCitizensClaimStartDateAndIsBackDatingToModel(final HttpServletRequest request, final Model model,
                                                                   final String claimId) {
        LocalDate claimStartDate;
        String claimStartDateFormatted;
        Claim claim = getOrCreateClaim(claimRepository, claimId);
        Optional<ClaimStartDateQuestion> question = claim.getClaimStartDate();

        if (!question.isPresent()) {
            claimStartDate = dateFormatterUtils.getTodayDate();
        } else {
            claimStartDate = LocalDate.of(question.get().getYear(), question.get().getMonth(), question.get().getDay());
        }

        LocalDate dateOfClaim = LocalDate.now();
        if (claim.getInitialDateOfContact() != null) {
            dateOfClaim = claim.getInitialDateOfContact();
        }

        model.addAttribute("isBackDating", claimStartDate.compareTo(dateOfClaim) < 0);

        claimStartDateFormatted = dateFormatterUtils.formatDate(request, cookieLocaleResolver, claimStartDate);
        model.addAttribute(CITIZEN_CLAIM_START_VIEW_VARIABLE, claimStartDateFormatted);
    }
}
