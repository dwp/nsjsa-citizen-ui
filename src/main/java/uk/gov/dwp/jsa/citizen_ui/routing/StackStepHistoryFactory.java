package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.stereotype.Component;

@Component
public class StackStepHistoryFactory {
    public StackStepHistory create(final String claimId) {
        return new StackStepHistory(claimId);
    }
}
