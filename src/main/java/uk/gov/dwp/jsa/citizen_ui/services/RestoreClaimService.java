package uk.gov.dwp.jsa.citizen_ui.services;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.DeclarationController;
import uk.gov.dwp.jsa.citizen_ui.controller.SummaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasAnotherCurrentJobController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasAnotherCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.LanguagePreferenceController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.AddWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.RestoreClaim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class RestoreClaimService {

    private static final Map<String, String> ALT_IDENTIFIERS = ImmutableMap.<String, String>builder()
            .put(HasAnotherCurrentJobController.IDENTIFIER_MAX_CURRENT_WORK, HasPreviousWorkController.IDENTIFIER)
            .put(AddWorkController.IDENTIFIER_MAX_CURRENT_WORK, HasOutsideWorkController.IDENTIFIER)
            .put(HasAnotherCurrentPensionController.IDENTIFIER_MAX_CURRENT_WORK,
                    EducationConfirmationController.IDENTIFIER)
            .build();

    public static final Map<String, String> OPT_IDENTIFIERS = ImmutableMap.<String, String>builder()
            .put(AboutAddressController.IDENTIFIER, LanguagePreferenceController.IDENTIFIER)
            .build();

    private final List<? extends BaseFormController> controllers;

    private final RoutingService routingService;

    public RestoreClaimService(final List<? extends BaseFormController> controllers,
                               @Qualifier("routingServiceInMemory") final RoutingService routingService) {
        this.controllers = controllers;
        this.routingService = routingService;
    }


    public void restore(final RestoreClaim restoreClaim) {
        String identifier = ClaimStartDateController.IDENTIFIER;
        String count = "";
        while (!SummaryController.IDENTIFIER.equals(identifier)) {
            final String finalIdentifier = selectNextIfNoPost(identifier);
            final String finalCount = count;
            String result = controllers
                    .stream()
                    .filter(c -> finalIdentifier.equals(c.getIdentifier()))
                    .findFirst()
                    .map(c -> loadData(restoreClaim, c, finalCount))
                    .orElse(SummaryController.IDENTIFIER);
            count = getCount(result);
            final String optFinalCount = count;

            if (OPT_IDENTIFIERS.containsKey(finalIdentifier)) {
                String optResult = controllers
                        .stream()
                        .filter(c -> OPT_IDENTIFIERS.get(finalIdentifier).equals(c.getIdentifier()))
                        .findFirst()
                        .map(c -> loadData(restoreClaim, c, optFinalCount))
                        .orElse(SummaryController.IDENTIFIER);
                count = getCount(optResult);
            }

            identifier = getIdentifier(result);
        }
        controllers.stream()
                .filter(c -> DeclarationController.IDENTIFIER.equals(((BaseFormController) c).getIdentifier()))
                .findFirst()
                .ifPresent(c -> loadData(restoreClaim, c, ""));
        routingService.save(restoreClaim.getClaim().getId());
    }

    private String selectNextIfNoPost(final String identifier) {
        return ALT_IDENTIFIERS
                .keySet()
                .stream()
                .filter(identifier::contains)
                .map(ALT_IDENTIFIERS::get)
                .findFirst()
                .orElse(identifier);
    }

    String getIdentifier(final String result) {
        return result.replace("redirect:/", "").replaceAll("[0-9]/", "");
    }

    String getCount(final String result) {
        return result.replaceAll("[^0-9]+", "");
    }

    String loadData(final RestoreClaim restoreClaim, final BaseFormController controller, final String finalCount) {
        final Claim claim = restoreClaim.getClaim();
        Form form;
        final Step step = routingService.getStep(controller.getIdentifier()).orElse(null);
        Integer counter = 0;
        if (controller instanceof CounterFormController && isNotEmpty(finalCount)) {
            counter = Integer.valueOf(finalCount);
            form = ((CounterFormController) controller).createNewForm(claim, counter);
            form.setCounterForm(true);
        } else {
            form = controller.createNewForm(claim);
        }
        controller.loadForm(restoreClaim.getClaimDB(), form);

        final StepInstance stepInstance = new StepInstance(
                step,
                counter,
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard());

        routingService.arrivedOnPage(claim.getId(), stepInstance);

        routingService.leavePage(claim.getId(), stepInstance);

        Optional<StepInstance> lastGuard = getLastGuard(form, claim, stepInstance);

        saveOrUpdate(controller, claim, form, stepInstance, lastGuard);

        return controller.getNextPath(claim, form, stepInstance);
    }

    private void saveOrUpdate(final BaseFormController controller, final Claim claim, final Form form,
                              final StepInstance stepInstance, final Optional<StepInstance> lastGuard) {
        StepInstance stepInstanceCopy = new StepInstance(stepInstance.getStep(),
                stepInstance.getCounter(),
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard()
        );
        if (form.getQuestion() instanceof LoopEndBooleanQuestion) {
            stepInstanceCopy.setCounter(0);
        }

        controller.saveOrUpdateClaim(stepInstanceCopy, claim, form, lastGuard, false);
    }

    public Optional<StepInstance> getLastGuard(final Form form, final Claim claim, final StepInstance stepInstance) {
        StepInstance stepInstanceCopy = new StepInstance(stepInstance.getStep(),
                stepInstance.getCounter(),
                form.isAGuard(),
                form.isGuardedCondition(),
                form.hasNoGuard()
        );
        stepInstanceCopy.setEdit(form.getEdit());
        return routingService.getLastGuard(claim.getId(), stepInstanceCopy);
    }
}
