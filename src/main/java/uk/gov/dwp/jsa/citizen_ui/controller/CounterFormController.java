package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.util.PaymentFrequencyUtils.isPaymentFrequencyAmountContainInvalidCharacters;

public abstract class CounterFormController<T extends AbstractCounterForm> extends BaseFormController<T> {
    private static final String FORM_POST_URL = "formPostUrl";

    public CounterFormController(final ClaimRepository claimRepository, final String viewName,
                                 final String modelName, final RoutingService routingService,
                                 final String identifier, final String nextStepIdentifier,
                                 final String alternateStepIdentifier, final Section section) {
        super(claimRepository, viewName, modelName, routingService, identifier, nextStepIdentifier,
                alternateStepIdentifier, section);
    }

    public String get(final Model model, final String claimId, final HttpServletRequest request,
                      final Integer counter) {
        String sanitisedClaimId = sanitiseUuid(claimId);
        Claim claim = getOrCreateClaim(getClaimRepository(), sanitisedClaimId);
        T form = createNewForm(claim, counter);
        setFormCounterAsTrue(form);
        StepInstance stepInstance = new StepInstance(
                getRoutingService().getStep(getIdentifier()).orElse(null),
                counter,
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard());
        getRoutingService().arrivedOnPage(sanitisedClaimId, stepInstance);
        setFormAttrs(form, sanitisedClaimId);
        setEditMode(request, form);
        model.addAttribute(getModelName(), form);
        model.addAttribute(FORM_POST_URL, String.format("/" + postUrlTemplate(), counter));
        addTitlePrefix(model, getIdentifier(), false);
        return getViewName();
    }

    @Override
    public String post(final String claimId, final T form, final BindingResult bindingResult,
                       final HttpServletResponse httpServletResponse, final Model model) {
        setFormCounterAsTrue(form);
        model.addAttribute(FORM_POST_URL, String.format("/" + postUrlTemplate(), form.getCount()));
        return super.post(claimId, form, bindingResult, httpServletResponse, model);
    }

    public T createNewForm(final Claim claim, final int counter) {
        T form = getTypedForm();
        form.setCount(counter);
        StepInstance stepInstance = new StepInstance(getRoutingService().getStep(getIdentifier()).orElse(null),
                counter,
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard()
        );

        Optional<Question> questionOpt = claim.get(stepInstance);
        questionOpt.ifPresent(form::setQuestion);
        return form;
    }

    private void setFormCounterAsTrue(final T form) {
        form.setCounterForm(true);
    }

    @Override
    public T createNewForm(final Claim claim) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNextPath(final Claim claim, final T form, final StepInstance stepInstance) {
        return format(super.getNextPath(claim, form, stepInstance), form.getCount());
    }

    public void updateGuardChoice(final String guardIdentifier, final String countIdentifier, final int limit,
                                  final Claim claim) {
        if (claim.count(countIdentifier, limit) == 0) {
            claim.get(guardIdentifier).ifPresent(question -> {
                if (question instanceof GuardQuestion) {
                    ((GuardQuestion) question).setChoice(false);
                }
            });
        }
    }

    public Optional<TypeOfWork> getTypeOfWork(final Claim claim, final int count) {
        Question thisQuestion = claim.get(WorkPaidOrVoluntaryController.IDENTIFIER, count).get();
        TypeOfWorkQuestion typeOfWorkQuestion;
        Optional<TypeOfWork> typeOfWork;
        if (thisQuestion instanceof TypeOfWorkQuestion) {
            typeOfWorkQuestion = ((TypeOfWorkQuestion) claim.get(WorkPaidOrVoluntaryController.IDENTIFIER, count).get());
            typeOfWork = Optional.of(typeOfWorkQuestion.getUserSelectionValue());
            return typeOfWork;
        }
        return Optional.empty();
    }

    public void setModelAttributesIfInvalidPaymentFrequency(final BindingResult bindingResult, final Model model,
                                                            final String propertyString) {
        if (isPaymentFrequencyAmountContainInvalidCharacters(bindingResult)) {
            model.addAttribute("isTypeMismatchErrorPresent", true);
            model.addAttribute("invalidCharsLocale", propertyString);
        }
    }

    public void removeFrom(final boolean remove, final String claimId, final List<String> identifiersToRemove,
                           final int count, final String guardIdentifier, final String countIdentifier,
                           final int maxTo) {
        if (remove) {
            Claim claim = getOrCreateClaim(getClaimRepository(), claimId);
            identifiersToRemove.forEach(
                    identifier -> deleteInstance(identifier, claim, count + 1, maxTo)
            );
            updateGuardChoice(guardIdentifier, countIdentifier, maxTo, claim);
            getClaimRepository().save(claim);
        }
    }
}
