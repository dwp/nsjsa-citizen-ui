package uk.gov.dwp.jsa.citizen_ui.model.form;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class ConfirmationForm extends AbstractForm {

    private boolean hasBeenToJuryService;

    private boolean hasWorkedInLast6Months;

    private boolean expectPaymentInLast6Months;

    private boolean currentlyWorkingWithPay;

    private boolean paymentWeeklyOrFortnightly;

    private boolean paymentMonthlyOrFourWeekly;

    private boolean hasPensions;

    private boolean needsBankDetailsEvidence;

    private boolean hasChangedStartDate;

    private String newStartDate;


    public String getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(final String newStartDate) {
        this.newStartDate = newStartDate;
    }

    public boolean isHasChangedStartDate() {
        return hasChangedStartDate;
    }

    public void setHasChangedStartDate(final boolean hasChangedStartDate) {
        this.hasChangedStartDate = hasChangedStartDate;
    }


    public boolean getNeedsBankDetailsEvidence() {
        return needsBankDetailsEvidence;
    }

    public void setNeedsBankDetailsEvidence(final boolean needsBankDetailsEvidence) {
        this.needsBankDetailsEvidence = needsBankDetailsEvidence;
    }

    public boolean isHasBeenToJuryService() {
        return hasBeenToJuryService;
    }

    public void setHasBeenToJuryService(final boolean hasBeenToJuryService) {
        this.hasBeenToJuryService = hasBeenToJuryService;
    }

    public boolean isHasWorkedInLast6Months() {
        return hasWorkedInLast6Months;
    }

    public void setHasWorkedInLast6Months(final boolean hasWorkedInLast6Months) {
        this.hasWorkedInLast6Months = hasWorkedInLast6Months;
    }

    public boolean isExpectPaymentInLast6Months() {
        return expectPaymentInLast6Months;
    }

    public void setExpectPaymentInLast6Months(final boolean expectPaymentInLast6Months) {
        this.expectPaymentInLast6Months = expectPaymentInLast6Months;
    }

    public boolean isCurrentlyWorkingWithPay() {
        return currentlyWorkingWithPay;
    }

    public void setCurrentlyWorkingWithPay(final boolean currentlyWorkingWithPay) {
        this.currentlyWorkingWithPay = currentlyWorkingWithPay;
    }

    public boolean isPaymentWeeklyOrFortnightly() {
        return paymentWeeklyOrFortnightly;
    }

    public void setPaymentWeeklyOrFortnightly(final boolean paymentWeeklyOrFortnightly) {
        this.paymentWeeklyOrFortnightly = paymentWeeklyOrFortnightly;
    }

    public boolean isPaymentMonthlyOrFourWeekly() {
        return paymentMonthlyOrFourWeekly;
    }

    public void setPaymentMonthlyOrFourWeekly(final boolean paymentMonthlyOrFourWeekly) {
        this.paymentMonthlyOrFourWeekly = paymentMonthlyOrFourWeekly;
    }

    public boolean isHasPensions() {
        return hasPensions;
    }

    public void setHasPensions(final boolean hasPensions) {
        this.hasPensions = hasPensions;
    }

    @Override
    public Question getQuestion() {
        return null; // no question for confirmation so there's nothing to set
    }

    @Override
    public void setQuestion(final Question question) {
        // No question so nothing to set
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
