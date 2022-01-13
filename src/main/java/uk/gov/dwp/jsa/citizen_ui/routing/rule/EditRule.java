package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.function.BiFunction;

public class EditRule implements BiFunction<RoutingSteps, StepInstance, String> {

    public static final String FORM_SUMMARY = "/form/summary";

    @Override
    public String apply(final RoutingSteps routingSteps, final StepInstance stepInstance) {
        if (stepInstance.getEdit() == EditMode.SINGLE) {
            return FORM_SUMMARY;
        } else if (stepInstance.getEdit() == EditMode.SECTION) {
            Step currentStep = stepInstance.getStep();
            Step nextStep = getNextStep(routingSteps, stepInstance, currentStep);
            if (!isSectionsTheSame(currentStep, nextStep)
                || currentStep.isSectionTerminator()) {
                return FORM_SUMMARY;
            }
        }
        return null;
    }

    private Step getNextStep(final RoutingSteps routingSteps, final StepInstance stepInstance, final Step currentStep) {
        return routingSteps.getNextStep(stepInstance.getStep().getIdentifier()).orElse(currentStep);
    }

    private boolean isSectionsTheSame(final Step step, final Step nextStep) {
        return step.getSection() == nextStep.getSection();
    }
}
