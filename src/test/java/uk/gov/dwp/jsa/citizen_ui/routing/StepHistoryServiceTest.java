package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.repository.StackStepHistoryRepository;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StepHistoryServiceTest {

    private static final String CLAIM_ID = "CLAIM_ID";
    private static final String STEP_IDENTIFIER = "STEP_IDENTIFIER";
    private static final Step STEP = new StepBuilder().withId(STEP_IDENTIFIER).build();
    private static final StepInstance STEP_INSTANCE = new StepInstance(false, false, STEP);

    @Mock
    private StackStepHistory stackStepHistory;
    @Mock
    private StackStepHistoryFactory stackStepHistoryFactory;
    @Mock
    private StackStepHistoryRepository stackStepHistoryRepository;

    private StepHistoryService stepHistoryService;
    private StepInstance returnedStepInstance;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void registersStep() {
        givenAStepHistoryService();
        whenIRegisterStep();
        thenTheStepIsRegistered();
    }

    @Test
    public void getsLastGuard() {
        givenAStepHistoryService();
        whenIRegisterStep();
        whenIGetLastGuard();
        thenTheLastGuardIsReturned();
    }

    @Test
    public void getsLastStep() {
        givenAStepHistoryService();
        whenIRegisterStep();
        whenIGetLastStep();
        thenTheLastStepIsReturned();
    }

    @Test
    public void arrivedOnPageDelegatesToStepHistory() {
        givenAStepHistoryService();
        whenIArriveOnPage();
        thenArriveOnPageIsDelagated();
    }

    @Test
    public void clearHistoryDeletesHistoryOnRepository() {
        givenAStepHistoryService();
        whenIClearHistory();
        thenHistoryIsDeletedFromRepository();
    }


    private void givenAStepHistoryService() {
        stepHistoryService = new StepHistoryService(stackStepHistoryFactory, stackStepHistoryRepository);
        when(stackStepHistoryRepository.findById(CLAIM_ID)).thenReturn(Optional.of(stackStepHistory));
        when(stackStepHistoryFactory.create(CLAIM_ID)).thenReturn(stackStepHistory);
        when(stackStepHistory.getLastGuard(any(StepInstance.class))).thenReturn(Optional.of(STEP_INSTANCE));
        when(stackStepHistory.getLastStep()).thenReturn(Optional.of(STEP_INSTANCE));
    }

    private void whenIRegisterStep() {
        stepHistoryService.registerStep(CLAIM_ID, STEP_INSTANCE);
    }

    private void whenIGetLastGuard() {
        returnedStepInstance = stepHistoryService.getLastGuard(CLAIM_ID, STEP_INSTANCE).get();
    }

    private void whenIArriveOnPage() {
        stepHistoryService.arrivedOnPage(CLAIM_ID, STEP_INSTANCE);
    }

    private void whenIGetLastStep() {
        returnedStepInstance = stepHistoryService.getLastStep(CLAIM_ID).get();
    }


    private void whenIClearHistory() {
        stepHistoryService.clearHistory(CLAIM_ID);
    }

    private void thenHistoryIsDeletedFromRepository() {
        verify(stackStepHistoryRepository).deleteById(CLAIM_ID);
    }

    private void thenTheLastStepIsReturned() {
        assertThat(returnedStepInstance, is(STEP_INSTANCE));
    }

    private void thenArriveOnPageIsDelagated() {
        verify(stackStepHistoryRepository).save(stackStepHistory);
        verify(stackStepHistory).arrivedOnPage(STEP_INSTANCE);
    }

    private void thenTheLastGuardIsReturned() {
        assertThat(returnedStepInstance, is(STEP_INSTANCE));
    }

    private void thenTheStepIsRegistered() {
        verify(stackStepHistoryRepository).save(stackStepHistory);
        verify(stackStepHistory).registerStep(STEP_INSTANCE);
    }


}
