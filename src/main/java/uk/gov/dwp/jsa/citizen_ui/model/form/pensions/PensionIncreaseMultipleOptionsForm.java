package uk.gov.dwp.jsa.citizen_ui.model.form.pensions;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class PensionIncreaseMultipleOptionsForm extends MultipleOptionsForm<PensionIncreaseMonthQuestion, Months> {

    private static final int MAX_MONTHS = 6;
    private LocalDate claimStartDate;

    public PensionIncreaseMultipleOptionsForm(final PensionIncreaseMonthQuestion multipleOptionsQuestion,
                                              final Months trueConditionValue) {
        super(multipleOptionsQuestion, trueConditionValue);
    }

    public List<Months> getOptions() {
        int startMonthCount = 0;
        if (claimStartDate != null
                && claimStartDate.getDayOfMonth() == claimStartDate.getMonth().length(claimStartDate.isLeapYear())
                && claimStartDate.getMonth().equals(now().getMonth())) {
            startMonthCount = 1;
        }
        LocalDate now = now();
        List<Months> options = new ArrayList<>();
        for (int i = startMonthCount; i < (startMonthCount + MAX_MONTHS); i++) {
            options.add(Months.valueOf(now.plusMonths(i).getMonth().name()));
        }
        return Collections.unmodifiableList(options);
    }

    public void setClaimStartDate(final LocalDate claimStartDate) {
        this.claimStartDate = claimStartDate;
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
