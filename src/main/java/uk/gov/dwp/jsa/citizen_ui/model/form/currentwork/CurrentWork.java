package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class CurrentWork {

    /**
     * Q25 Current Work - Are You Working.
     */
    private BooleanQuestion areYouWorking;
    /**
     * Q25a Current Work Has More Than 4 Jobs.
     */
    private boolean hasMoreThan4Jobs;

    private CurrentWorkDetails currentWorkDetails;
    private List<CurrentWorkDetails> currentWorkDetailsList = new ArrayList<>();

    public CurrentWork() {
    }

    public CurrentWork(final BooleanQuestion areYouWorking,
                       final CurrentWorkDetails currentWorkDetails) {
        this.areYouWorking = areYouWorking;
        this.currentWorkDetails = currentWorkDetails;
        currentWorkDetailsList.add(currentWorkDetails);
    }

    public CurrentWorkDetails getCurrentWorkDetails(final Integer count) {
        if (currentWorkDetailsList == null || currentWorkDetailsList.size() < count) {
            currentWorkDetails = new CurrentWorkDetails();
        } else {
            currentWorkDetails = currentWorkDetailsList.get(count - 1);
        }
        return currentWorkDetails;
    }

    public void updateCurrentWorkDetails(final Integer count, final CurrentWorkDetails currentWorkDetails) {
        if (currentWorkDetailsList.size() < count) {
            currentWorkDetailsList.add(currentWorkDetails);
        } else {
            currentWorkDetailsList.set(count - 1, currentWorkDetails);
        }
    }

    public List<CurrentWorkDetails> getCurrentWorkDetailsList() {
        return Collections.unmodifiableList(currentWorkDetailsList);
    }

    public void setCurrentWorkDetailsList(final List<CurrentWorkDetails> currentWorkDetailsList) {
        this.currentWorkDetailsList = new ArrayList<>(currentWorkDetailsList);
    }

    public BooleanQuestion getAreYouWorking() {
        return areYouWorking;
    }

    public void setAreYouWorking(final BooleanQuestion areYouWorking) {
        this.areYouWorking = areYouWorking;
    }

    public boolean isHasMoreThan4Jobs() {
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
