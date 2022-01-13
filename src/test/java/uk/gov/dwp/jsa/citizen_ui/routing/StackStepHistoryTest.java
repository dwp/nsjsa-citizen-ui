package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StackStepHistoryTest {

    private static final String IDENTIFIER = "IDENTIFIER ";
    private static final Step NINO_STEP = new StepBuilder().withId("NINO").withSection(Section.PERSONAL_DETAILS).build();
    private static final Step GUARD_STEP_1 = new StepBuilder().withId("GUARD 1").withSection(Section.PERSONAL_DETAILS).build();
    private static final Step GUARD_STEP_2 = new StepBuilder().withId("GUARD 2").withSection(Section.PERSONAL_DETAILS).build();
    private static final Step STEP_CURRENT_WORK = new StepBuilder().withId(IDENTIFIER).withSection(Section.CURRENT_WORK).build();
    private static final Step STEP_GUARD_CURRENT_WORK = new StepBuilder().withId("GUARD" + IDENTIFIER).withSection(Section.CURRENT_WORK).build();
    private static final StepInstance CURRENT_WORK_GUARD_STEP_INSTANCE = new StepInstance(STEP_GUARD_CURRENT_WORK, 1, true, true, false);
    private static final StepInstance CURRENT_WORK_NON_GUARD_STEP_INSTANCE = new StepInstance(STEP_CURRENT_WORK, 1, false, false, false);
    private static final StepInstance CURRENT_WORK_NON_GUARD_COUNTER_3_INSTANCE = new StepInstance(STEP_CURRENT_WORK, 3, false, false, false);
    private static final StepInstance NINO_STEP_INSTANCE = new StepInstance(false, false, NINO_STEP);
    private static final StepInstance GUARD_STEP_1_INSTANCE = new StepInstance(true, true, GUARD_STEP_1);
    private static final StepInstance GUARD_STEP_2_INSTANCE = new StepInstance(true, true, GUARD_STEP_2);
    private static final StepInstance NO_GUARD_STEP_INSTANCE = new StepInstance(NINO_STEP, 0, false, false, true);
    private static final String CLAIM_ID = "CLAIM_ID";

    private StackStepHistory stackStepHistory ;

    @Test
    public void arrivedOnPagePopsOffStackAfterStepBack() {
        givenAStepHistory();
        whenIRegisterStep(NINO_STEP_INSTANCE);
        whenIArriveOnPage(NINO_STEP_INSTANCE);
        thenTheLastStepIs(Optional.empty());
    }

    @Test
    public void arrivedOnPageDoesNotPopIfMovingForwards() {
        givenAStepHistory();
        whenIRegisterStep(NINO_STEP_INSTANCE);
        whenIArriveOnPage(GUARD_STEP_1_INSTANCE);
        thenTheLastStepIs(Optional.of(NINO_STEP_INSTANCE));
    }

    @Test
    public void registerStepPushesStepToStack() {
        givenAStepHistory();
        whenIRegisterStep(NINO_STEP_INSTANCE);
        thenTheLastStepIs(Optional.of(NINO_STEP_INSTANCE));
    }

    @Test
    public void getLastStepReturnsEmptyStepIfStackisEmpty() {
        givenAStepHistory();
        thenTheLastStepIs(empty());
    }

    @Test
    public void getLastGuardGetsExpectedResult() {
        givenAStepHistory();
        whenIRegisterStep(GUARD_STEP_1_INSTANCE);
        whenIRegisterStep(GUARD_STEP_2_INSTANCE);
        whenIRegisterStep(NINO_STEP_INSTANCE);

        thenLastGuardIs(GUARD_STEP_2_INSTANCE, NINO_STEP_INSTANCE);
    }

    @Test
    public void getLastGuardGetsExpectedResultForCounterForm() {
        givenAStepHistory();
        givenStackIsSetForCounterInstance();

        thenLastGuardIs(CURRENT_WORK_GUARD_STEP_INSTANCE, CURRENT_WORK_NON_GUARD_STEP_INSTANCE);
    }

    @Test
    public void getLastGuardDoesNotReturnItself() {
        givenAStepHistory();
        givenStackIsSetForCounterInstance();

        thenLastGuardIsEmpty(CURRENT_WORK_GUARD_STEP_INSTANCE);
    }

    private void givenStackIsSetForCounterInstance() {
        whenIRegisterStep(CURRENT_WORK_GUARD_STEP_INSTANCE);
        whenIRegisterStep(GUARD_STEP_1_INSTANCE);
        whenIRegisterStep(NINO_STEP_INSTANCE);
        whenIRegisterStep(GUARD_STEP_2_INSTANCE);
        whenIRegisterStep(CURRENT_WORK_NON_GUARD_STEP_INSTANCE);
    }

    @Test
    public void getLastGuardGetsGuardNonCounterInstanceInSameSection() {
        givenAStepHistory();
        givenStackIsSetForCounterInstance();
        whenIRegisterStep(CURRENT_WORK_NON_GUARD_COUNTER_3_INSTANCE);

        thenLastGuardIs(CURRENT_WORK_GUARD_STEP_INSTANCE, CURRENT_WORK_NON_GUARD_COUNTER_3_INSTANCE);
    }

    @Test
    public void getLastGuardGetsExpectedResultIfNotLastInStack() {
        givenAStepHistory();
        whenIRegisterStep(GUARD_STEP_1_INSTANCE);
        whenIRegisterStep(NINO_STEP_INSTANCE);

        thenLastGuardIs(GUARD_STEP_1_INSTANCE, NINO_STEP_INSTANCE);
    }

    @Test
    public void getLastGuardReturnsEmptyIfNoGuards() {
        givenAStepHistory();
        whenIRegisterStep(NINO_STEP_INSTANCE);

        thenLastGuardEmpty();
    }

    @Test
    public void clearSummaryHistoryPopsAllSummaryModeSteps() {
        givenAStepHistory();
        givenStepInstancesModeIsSetToSummary();
        whenIRegisterStep(GUARD_STEP_1_INSTANCE);
        whenIRegisterStep(NINO_STEP_INSTANCE);

        whenIClearSummaryHistory();

        thenStepStackIsEmpty();
        resetEditModes();
    }

    private void thenStepStackIsEmpty() {
        assertThat(stackStepHistory.getStepHistory().empty(), is(true));
    }

    private void whenIClearSummaryHistory() {
        stackStepHistory.clearSummaryHistory();
    }

    private void givenStepInstancesModeIsSetToSummary() {
        GUARD_STEP_1_INSTANCE.setEdit(EditMode.SECTION);
        NINO_STEP_INSTANCE.setEdit(EditMode.SECTION);
    }

    private void resetEditModes() {
        GUARD_STEP_1_INSTANCE.setEdit(null);
        NINO_STEP_INSTANCE.setEdit(null);
    }

    private void givenAStepHistory() {
        stackStepHistory = new StackStepHistory(CLAIM_ID);
    }

    private void whenIArriveOnPage(final StepInstance stepInstance) {
        stackStepHistory.arrivedOnPage(stepInstance);
    }

    private void thenLastGuardIs(final StepInstance stepInstance, final StepInstance currentStepInstance) {
        StepInstance lastGuard = stackStepHistory.getLastGuard(currentStepInstance).get();
        assertThat(lastGuard, is(stepInstance));
    }

    private void thenLastGuardIsEmpty(final StepInstance currentStepInstance) {
        assertThat(stackStepHistory.getLastGuard(currentStepInstance).isPresent(), is(false));
    }

    private void thenLastGuardEmpty() {
        Optional<StepInstance> lastGuard = stackStepHistory.getLastGuard(NINO_STEP_INSTANCE);
        assertThat(lastGuard.isPresent(), is(false));
    }


    private void thenTheLastStepIs(final Optional<StepInstance> stepInstance) {
        assertThat(stackStepHistory.getLastStep(), is(stepInstance));
    }

    private void whenIRegisterStep(final StepInstance stepInstance) {
        stackStepHistory.registerStep(stepInstance);
    }

}
