package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class OtherBenefits {

    /**
     * Q85 Other Benefits - Are you waiting to hear.
     */
    private BooleanQuestion areYouWaitingToHear;

    /**
     * Q86 Other Benefit Details.
     */
    private StringQuestion otherBenefitDetails;

    public BooleanQuestion getAreYouWaitingToHear() {
        return areYouWaitingToHear;
    }

    public void setAreYouWaitingToHear(final BooleanQuestion areYouWaitingToHear) {
        this.areYouWaitingToHear = areYouWaitingToHear;
    }

    public StringQuestion getOtherBenefitDetails() {
        return otherBenefitDetails;
    }

    public void setOtherBenefitDetails(final StringQuestion otherBenefitDetails) {
        this.otherBenefitDetails = otherBenefitDetails;
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
