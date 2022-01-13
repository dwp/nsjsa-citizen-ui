package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.BackStepRuleEngine;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.StepRuleEngine;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoutingServiceTests {
    private static final StepInstance NINO_STEP_INSTANCE = new StepInstance(false, false, new Step("ID3", null, null, Section.NONE));
    private static final String CLAIM_ID = "CLAIM_ID";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Step mockCurrentStep;

    @Mock
    StepInstance mockCurrentStepInstance;


    @Mock
    private StepHistoryService stepHistoryService;

    @Mock
    private RoutingSteps routingSteps;

    private StepRuleEngine stepRuleEngine;
    private BackStepRuleEngine mockBackStepRuleEngine;
    private RoutingService routingService;

    @Before
    public void setup(){
        mockBackStepRuleEngine = new BackStepRuleEngine();
        stepRuleEngine = new StepRuleEngine();
        when(mockCurrentStepInstance.getStep()).thenReturn(mockCurrentStep);
    }

    @Test
    public void deleteHistoryDelagesToStepHistory() {
        givenARoutingService();
        whenICallClearHistory();
        thenClearHistoryIsDelagatedToStepHistory();
    }

    @Test
    public void arrivedOnPageDelagesToStepHistory() {
        givenARoutingService();
        whenICallArrivedOnPage();
        thenArrivedOnPageIsDelagatedToStepHistory();
    }

    @Test
    public void leavePageDelegatesToStepHistory() {
        givenARoutingService();
        whenICallLeavePage();
        thenLeavePageIsDelagatedToStepHistory();
    }

    private void givenARoutingService() {
        routingService = new RoutingServiceImpl(stepHistoryService, stepRuleEngine, mockBackStepRuleEngine,
                routingSteps);
    }

    private void whenICallLeavePage() {
        routingService.leavePage(CLAIM_ID, NINO_STEP_INSTANCE);
    }

    private void whenICallArrivedOnPage() {
        routingService.arrivedOnPage(CLAIM_ID, NINO_STEP_INSTANCE);
    }

    private void whenICallClearHistory() {
        routingService.clearHistory(CLAIM_ID);
    }

    private void thenClearHistoryIsDelagatedToStepHistory() {
        verify(stepHistoryService).clearHistory(CLAIM_ID);
    }

    private void thenArrivedOnPageIsDelagatedToStepHistory() {
        verify(stepHistoryService).arrivedOnPage(CLAIM_ID, NINO_STEP_INSTANCE);
    }

    private void thenLeavePageIsDelagatedToStepHistory() {
        verify(stepHistoryService).registerStep(CLAIM_ID, NINO_STEP_INSTANCE);
    }

    public void leavePage(final StepInstance stepInstance) {
        stepHistoryService.registerStep(CLAIM_ID, stepInstance);
    }

    @Test
    public void givenFormNotGuarded_getNext_returnsNextStep() {
        String expected = "next step";
        when(mockCurrentStep.getNextStepIdentifier()).thenReturn(expected);
        when(mockCurrentStep.getNextAlternateStepIdentifier()).thenReturn(Optional.ofNullable(expected));
        when(mockCurrentStepInstance.isAGuard()).thenReturn(false);
        when(mockCurrentStepInstance.isGuardedCondition()).thenReturn(false);

        RoutingService sut = createSut();
        sut.registerStep(mockCurrentStep);
        String actual = sut.getNext(mockCurrentStepInstance);

        assertThat(actual, is(expected));
    }

    @Test
    public void givenFormIsGuardedAndAlternateProvided_getNext_returnsAlternateStep() {

        String expected = "alternative next step";
        when(mockCurrentStep.getNextAlternateStepIdentifier()).thenReturn(Optional.ofNullable(expected));
        when(mockCurrentStepInstance.isAGuard()).thenReturn(true);
        when(mockCurrentStepInstance.isGuardedCondition()).thenReturn(true);

        RoutingService sut = createSut();
        sut.registerStep(mockCurrentStep);

        String actual = sut.getNext(mockCurrentStepInstance);

        assertThat(actual, is(expected));
    }

    @Test
    public void clearSummaryHistoryDelegatesToStepHistoryService() {
        givenARoutingService();

        routingService.clearSummaryHistory(CLAIM_ID);

        verify(stepHistoryService).clearSummaryHistory(CLAIM_ID);
    }

    @Test
    public void deregisterStep() {
        RoutingService sut = createSut();
        sut.deregisterStep(mockCurrentStep);

        verify(routingSteps).deregister(mockCurrentStep);
    }

    public RoutingService createSut() {
        return new RoutingServiceImpl(stepHistoryService, stepRuleEngine, mockBackStepRuleEngine, routingSteps);
    }
}
