package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Keeps claimant's answers for previous employment.
 */
public class PreviousEmployment {

    /**
     * Q40 Has the claimant worked in the last 6 months.
     */
    private BooleanQuestion hasPreviousWorkQuestion;

    private boolean hasMoreThan4Jobs;

    /**
     * Q43 Keeps previous employer's details.
     */
    private EmployersDetails employersDetails;

    private List<EmployersDetails> employerDetailsList = new ArrayList<>();

    public PreviousEmployment(
            final BooleanQuestion hasPreviousWorkQuestion,
            final EmployersDetails employersDetails) {
        this.hasPreviousWorkQuestion = hasPreviousWorkQuestion;
        this.employersDetails = employersDetails;
    }

    public PreviousEmployment() {
    }

    public EmployersDetails getEmployerDetails(final Integer count) {
        if (employerDetailsList == null || employerDetailsList.size() < count) {
            employersDetails = new EmployersDetails();
        } else {
            employersDetails = employerDetailsList.get(count - 1);
        }
        return employersDetails;
    }

    public void updateEmployerDetails(final Integer count, final EmployersDetails employersDetails) {
        if (employerDetailsList.size() < count) {
            employerDetailsList.add(employersDetails);

        } else {
            employerDetailsList.set(count - 1, employersDetails);
        }
    }

    public BooleanQuestion getHasPreviousWorkQuestion() {
        if (hasPreviousWorkQuestion == null) {
            hasPreviousWorkQuestion = new BooleanQuestion();
        }
        return hasPreviousWorkQuestion;
    }

    public void setHasPreviousWorkQuestion(
            final BooleanQuestion hasPreviousWorkQuestion) {
        this.hasPreviousWorkQuestion = hasPreviousWorkQuestion;
    }

    public List<EmployersDetails> getEmployerDetailsList() {
        return Collections.unmodifiableList(employerDetailsList);
    }

    public void setEmployerDetailsList(final List<EmployersDetails> employerDetailsList) {
        this.employerDetailsList = new ArrayList<>(employerDetailsList);
    }

    public boolean hasMoreThan4Jobs() {
        return hasMoreThan4Jobs;
    }

    public void setHasMoreThan4Jobs(final boolean hasMoreThan4Jobs) {
        this.hasMoreThan4Jobs = hasMoreThan4Jobs;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
