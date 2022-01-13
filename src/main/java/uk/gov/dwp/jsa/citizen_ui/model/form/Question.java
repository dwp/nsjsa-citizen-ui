package uk.gov.dwp.jsa.citizen_ui.model.form;

/**
 * Generic question interface.
 */
public interface Question {
    default boolean isADealBreaker() {
        return false;
    }

    default String trimPostCode(String postCode) {
        if (!postCode.isEmpty()) {
            return postCode.trim();
        }
        return postCode;
    }
}
