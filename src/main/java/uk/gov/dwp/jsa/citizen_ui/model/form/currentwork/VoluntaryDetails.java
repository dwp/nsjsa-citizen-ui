package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class VoluntaryDetails {

    private BooleanQuestion canChooseIfPaid;
    private BooleanQuestion voluntaryPaid;
    private TypeOfWorkQuestion workPaidOrVoluntary;

    public VoluntaryDetails(final BooleanQuestion canChooseIfPaid,
                            final BooleanQuestion voluntaryPaid,
                            final TypeOfWorkQuestion workPaidOrVoluntary) {
        this.canChooseIfPaid = canChooseIfPaid;
        this.voluntaryPaid = voluntaryPaid;
        this.workPaidOrVoluntary = workPaidOrVoluntary;
    }

    public VoluntaryDetails() {
    }

    public TypeOfWorkQuestion getWorkPaidOrVoluntary() {
        if (workPaidOrVoluntary == null) {
            workPaidOrVoluntary = new TypeOfWorkQuestion();
        }
        return workPaidOrVoluntary;
    }

    public void setWorkPaidOrVoluntary(final TypeOfWorkQuestion workPaidOrVoluntary) {
        if (workPaidOrVoluntary == null) {
            this.workPaidOrVoluntary = new TypeOfWorkQuestion();
        }
        this.workPaidOrVoluntary = workPaidOrVoluntary;
    }

    public BooleanQuestion getCanChooseIfPaid() {
        if (canChooseIfPaid == null) {
            canChooseIfPaid = new BooleanQuestion();
        }
        return canChooseIfPaid;
    }

    public void setCanChooseIfPaid(final BooleanQuestion canChooseIfPaid) {
        this.canChooseIfPaid = canChooseIfPaid;
    }

    public BooleanQuestion getVoluntaryPaid() {
        if (voluntaryPaid == null) {
            this.voluntaryPaid = new BooleanQuestion();
        }
        return voluntaryPaid;
    }

    public void setVoluntaryPaid(final BooleanQuestion voluntaryPaid) {
        this.voluntaryPaid = voluntaryPaid;
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
