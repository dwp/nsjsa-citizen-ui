package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import java.util.Optional;
import java.util.stream.Stream;

public enum IneligibleNextMapping {
    RESIDENCE(Constants.RESIDENCE_URL, Constants.WORKING_URL),
    WORKING_OVER(Constants.WORKING_OVER_URL, Constants.CONTRIBUTIONS_URL),
    CONTRIBUTIONS(Constants.CONTRIBUTIONS_URL, Constants.DEFAULT_CLAIM_START);

    private final String currentUrl;
    private final String nextUrl;

    IneligibleNextMapping(final String currentUrl, final String nextUrl) {
        this.currentUrl = currentUrl;
        this.nextUrl = nextUrl;
    }

    public static String getNext(final String url) {
        Optional<IneligibleNextMapping> nextMapping = Stream.of(
                IneligibleNextMapping.values())
                .filter(value -> value.getCurrentUrl().equalsIgnoreCase(url))
                .findAny();

        if (nextMapping.isPresent()) {
            return nextMapping.get().getNextUrl();
        } else {
            return "";
        }
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    private static class Constants {
        static final String CONTRIBUTIONS_URL = "contributions";
        static final String WORKING_OVER_URL = "working-over";
        static final String WORKING_URL = "working";
        static final String RESIDENCE_URL = "residence";
        static final String DEFAULT_CLAIM_START = "default-claim-start";
    }
}
