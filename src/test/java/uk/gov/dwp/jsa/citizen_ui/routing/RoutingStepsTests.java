package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class RoutingStepsTests {

    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String INCORRECT_IDENTIFIER = "incorrect identifier";
    private static final String UNSANITISED_NEXT_STEP_ID = "/next/step/%s/url";

    private static final Step STEP = new StepBuilder().withId(IDENTIFIER).build();
    private static final Step NEXT_STEP = new StepBuilder().withId(StepBuilder.NEXT_STEP).build();
    private static final Step STEP_WITH_UNSANITISED_NEXT_STEP_ID =
            new StepBuilder().withId(IDENTIFIER).withNextStep(UNSANITISED_NEXT_STEP_ID).build();

    private RoutingSteps routingSteps;

    @Before
    public void beforeEachTest() {
        initMocks(this);
        routingSteps = new RoutingSteps();
    }

    @Test
    public void addsStep() {
        whenIRegisterSomeSteps(Arrays.asList(STEP));
        thenTheStepIsFound();
    }
    @Test
    public void handlesGetStepThatDoesntExist() {
        whenIDontRegisterAStep();
        thenTheStepIsNotFound();
    }

    @Test
    public void registerMultipleStepsAndGetTheNextStep() {
        whenIRegisterSomeSteps(Arrays.asList(STEP, NEXT_STEP));
        thenICanGetTheNextStep();
    }

    @Test
    public void registerASingleStepAndGetAnEmptyStep() {
        whenIRegisterSomeSteps(Arrays.asList(STEP));
        thenIGetAnEmptyNextStep();
    }

    @Test
    public void registerASingleStepThenSearchForAnIncorrectIdAndGetAnEmptyStep() {
        whenIRegisterSomeSteps(Arrays.asList(STEP));
        searchForAnIncorrectIdentifierAndthenIGetAnEmptyNextStep();
    }

    @Test
    public void ensureThatISanitiseTheNextStepIdentifierGivenANextStepUrl() {
        String expected = "next/step/url";
        String id = routingSteps.getSanitisedNextStepIdentifier(STEP_WITH_UNSANITISED_NEXT_STEP_ID);
        assertThat(id, is(expected));
    }

    @Test
    public void ensureDeregisterRemovesTheStep() {
        whenIRegisterSomeSteps(Arrays.asList(STEP, NEXT_STEP));
        whenIDeregisterAStep(NEXT_STEP);
        thenTheStepIsNotFound(NEXT_STEP.getIdentifier());
    }

    private void whenIRegisterSomeSteps(final List<Step> steps) {
        steps.forEach(step -> routingSteps.register(step));
    }

    private void whenIDontRegisterAStep() {
        //do nothing
    }

    private void whenIDeregisterAStep(Step step) {
        routingSteps.deregister(step);
    }

    private void thenTheStepIsNotFound(String stepIdentifier) {
        Optional<Step> optionalStep = routingSteps.getStep(stepIdentifier);
        assertThat(optionalStep.isPresent(), is(false));
    }

    private void thenTheStepIsNotFound() {
        Optional<Step> step = routingSteps.getStep(IDENTIFIER);
        assertThat(step.isPresent(), is(false));
    }

    private void thenTheStepIsFound() {
        Optional<Step> step = routingSteps.getStep(IDENTIFIER);
        assertThat(step.isPresent(), is(true));
        assertThat(step.get().getIdentifier(), is(IDENTIFIER));
    }

    private void thenICanGetTheNextStep() {
        Optional<Step> step = routingSteps.getNextStep(IDENTIFIER);
        assertThat(step.isPresent(), is(true));
        assertThat(step.get().getIdentifier(), is(StepBuilder.NEXT_STEP));
    }

    private void thenIGetAnEmptyNextStep() {
        Optional<Step> step = routingSteps.getNextStep(IDENTIFIER);
        assertThat(step.isPresent(), is(false));
        assertThat(step, is(Optional.empty()));
    }

    private void searchForAnIncorrectIdentifierAndthenIGetAnEmptyNextStep() {
        Optional<Step> step = routingSteps.getNextStep(INCORRECT_IDENTIFIER);
        assertThat(step.isPresent(), is(false));
        assertThat(step, is(Optional.empty()));
    }
}
