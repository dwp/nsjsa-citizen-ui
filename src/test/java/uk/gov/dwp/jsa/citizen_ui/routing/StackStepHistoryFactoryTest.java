package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class StackStepHistoryFactoryTest {
    private static final String CLAIM_ID = "CLAIM_ID";

    private StackStepHistoryFactory factory;
    private StackStepHistory stackStepHistory;

    @Test
    public void createStackStepHistory() {
        givenAFactory();
        whenICallCreate();
        thenTheStackStepHistoryIsCreated();
    }

    private void givenAFactory() {
        factory = new StackStepHistoryFactory();
    }

    private void whenICallCreate() {
        stackStepHistory = factory.create(CLAIM_ID);
    }

    private void thenTheStackStepHistoryIsCreated() {
        String claimId = (String) ReflectionTestUtils.getField(stackStepHistory, "claimId");
        assertThat(claimId, is(CLAIM_ID));
        assertThat(stackStepHistory, is(notNullValue()));
    }
}
