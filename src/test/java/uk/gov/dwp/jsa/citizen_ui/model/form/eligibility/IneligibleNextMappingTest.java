package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.IneligibleNextMapping.RESIDENCE;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.IneligibleNextMapping.WORKING_OVER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.IneligibleNextMapping.CONTRIBUTIONS;

public class IneligibleNextMappingTest {

    @Test
    public void getNextHandlesNonMatchingArgument() {
        String next = IneligibleNextMapping.getNext("any");

        assertThat(next, is(""));
    }

    @Test
    public void getNextHandlesNullArgument() {
        String next = IneligibleNextMapping.getNext(null);

        assertThat(next, is(""));
    }

    @Test
    public void getNextForResidenceReturnsExpectedUrl() {
        String next = IneligibleNextMapping.getNext(RESIDENCE.getCurrentUrl());

        assertThat(next, is(RESIDENCE.getNextUrl()));
    }

    @Test
    public void getNextForWorkingOverReturnsExpectedUrl() {
        String next = IneligibleNextMapping.getNext(WORKING_OVER.getCurrentUrl());

        assertThat(next, is(WORKING_OVER.getNextUrl()));
    }

    @Test
    public void getNextForContributionsReturnsExpectedUrl() {
        String next = IneligibleNextMapping.getNext(CONTRIBUTIONS.getCurrentUrl());

        assertThat(next, is(CONTRIBUTIONS.getNextUrl()));
    }
}
