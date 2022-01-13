package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;

import java.math.BigDecimal;
import java.util.Optional;

public class PaymentFrequencyQuestion implements Question {

    private PaymentFrequency paymentFrequency;

    private PaymentAmounts weeklyPaymentAmounts;
    private PaymentAmounts fortnightlyPaymentAmounts;
    private PaymentAmounts fourweeklyPaymentAmounts;
    private PaymentAmounts monthlyPaymentAmounts;
    private PaymentAmounts quarterlyPaymentAmounts;
    private PaymentAmounts biannuallyPaymentAmounts;
    private PaymentAmounts annuallyPaymentAmounts;


    public PaymentAmounts getWeeklyPaymentAmounts() {
        return weeklyPaymentAmounts;
    }

    public void setWeeklyPaymentAmounts(final PaymentAmounts weeklyPaymentAmounts) {
        this.weeklyPaymentAmounts = weeklyPaymentAmounts;
    }

    public PaymentAmounts getFortnightlyPaymentAmounts() {
        return fortnightlyPaymentAmounts;
    }

    public void setFortnightlyPaymentAmounts(final PaymentAmounts fortnightlyPaymentAmounts) {
        this.fortnightlyPaymentAmounts = fortnightlyPaymentAmounts;
    }

    public PaymentAmounts getFourweeklyPaymentAmounts() {
        return fourweeklyPaymentAmounts;
    }

    public void setFourweeklyPaymentAmounts(final PaymentAmounts fourweeklyPaymentAmounts) {
        this.fourweeklyPaymentAmounts = fourweeklyPaymentAmounts;
    }

    public PaymentAmounts getMonthlyPaymentAmounts() {
        return monthlyPaymentAmounts;
    }

    public void setMonthlyPaymentAmounts(final PaymentAmounts monthlyPaymentAmounts) {
        this.monthlyPaymentAmounts = monthlyPaymentAmounts;
    }

    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }


    public void setPaymentFrequency(final PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public PaymentAmounts getQuarterlyPaymentAmounts() {
        return quarterlyPaymentAmounts;
    }

    public void setQuarterlyPaymentAmounts(final PaymentAmounts quarterlyPaymentAmounts) {
        this.quarterlyPaymentAmounts = quarterlyPaymentAmounts;
    }

    public PaymentAmounts getBiannuallyPaymentAmounts() {
        return biannuallyPaymentAmounts;
    }

    public void setBiannuallyPaymentAmounts(final PaymentAmounts biannuallyPaymentAmounts) {
        this.biannuallyPaymentAmounts = biannuallyPaymentAmounts;
    }

    public PaymentAmounts getAnnuallyPaymentAmounts() {
        return annuallyPaymentAmounts;
    }

    public void setAnnuallyPaymentAmounts(final PaymentAmounts annuallyPaymentAmounts) {
        this.annuallyPaymentAmounts = annuallyPaymentAmounts;
    }

    public Optional<PaymentAmounts> getSelectedPaymentAmounts() {
        if (paymentFrequency == null) {
            return Optional.empty();
        }
        switch (paymentFrequency) {
            case FORTNIGHTLY:
                return Optional.of(fortnightlyPaymentAmounts);
            case FOURWEEKLY:
                return Optional.of(fourweeklyPaymentAmounts);
            case MONTHLY:
                return Optional.of(monthlyPaymentAmounts);
            case WEEKLY:
                return Optional.of(weeklyPaymentAmounts);
            case QUARTERLY:
                return Optional.of(quarterlyPaymentAmounts);
            case BIANNUALLY:
                return Optional.of(biannuallyPaymentAmounts);
            case ANNUALLY:
                return Optional.of(annuallyPaymentAmounts);
            default:
                return Optional.of(weeklyPaymentAmounts);
        }
    }

    public void setSelectedPaymentAmounts(final BigDecimal netPay) {
        if (paymentFrequency == null) {
            return;
        }
        final PaymentAmounts paymentAmounts = new PaymentAmounts(netPay);
        switch (paymentFrequency) {
            case FORTNIGHTLY:
                this.fortnightlyPaymentAmounts = paymentAmounts;
                break;
            case FOURWEEKLY:
                fourweeklyPaymentAmounts = paymentAmounts;
                break;
            case MONTHLY:
                monthlyPaymentAmounts = paymentAmounts;
                break;
            case WEEKLY:
                weeklyPaymentAmounts = paymentAmounts;
                break;
            case QUARTERLY:
                quarterlyPaymentAmounts = paymentAmounts;
                break;
            case BIANNUALLY:
                biannuallyPaymentAmounts = paymentAmounts;
                break;
            case ANNUALLY:
                annuallyPaymentAmounts = paymentAmounts;
                break;
            default:
                weeklyPaymentAmounts = paymentAmounts;
                break;
        }
    }

    public boolean isWeeklyOrFortnightlyPayments() {
        return paymentFrequency == PaymentFrequency.WEEKLY || paymentFrequency == PaymentFrequency.FORTNIGHTLY;
    }

    public boolean isMonthlyOrFourWeeklyPayments() {
        return paymentFrequency == PaymentFrequency.MONTHLY || paymentFrequency == PaymentFrequency.FOURWEEKLY;
    }

}
