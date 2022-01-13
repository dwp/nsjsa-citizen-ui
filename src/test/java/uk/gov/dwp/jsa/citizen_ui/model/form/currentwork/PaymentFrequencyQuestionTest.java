package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PaymentFrequencyQuestionTest {

    private static final PaymentAmounts PAYMENT_AMOUNTS = new PaymentAmounts();
    private static final PaymentFrequency PAYMENT_FREQUENCY = PaymentFrequency.MONTHLY;

    @Test
    public void setsWeeklyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setWeeklyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getWeeklyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsFortnightlyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setFortnightlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getFortnightlyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsFourweeklyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setFourweeklyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getFourweeklyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsMonthlyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setMonthlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getMonthlyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsQuaterlyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setQuarterlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getQuarterlyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsBiAnnuallyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setBiannuallyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getBiannuallyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsAnnuallyPaymentAmounts() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setAnnuallyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getAnnuallyPaymentAmounts(), is(PAYMENT_AMOUNTS));
    }

    @Test
    public void setsPaymentFrequency() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PAYMENT_FREQUENCY);
        assertThat(question.getPaymentFrequency(), is(PAYMENT_FREQUENCY));
    }

    @Test
    public void getsSelectedPaymentAmountsForWeekly() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.WEEKLY);
        question.setWeeklyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }
    @Test
    public void getsSelectedPaymentAmountsForMonthly() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.MONTHLY);
        question.setMonthlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }
    @Test
    public void getsSelectedPaymentAmountsForFourWeekly() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.FOURWEEKLY);
        question.setFourweeklyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }
    @Test
    public void getsSelectedPaymentAmountsForFortnightly() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.FORTNIGHTLY);
        question.setFortnightlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }

    @Test
    public void getsSelectedPaymentAmountsForQuaterly() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.QUARTERLY);
        question.setQuarterlyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }

    @Test
    public void getsSelectedPaymentAmountsForBiAnnually() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.BIANNUALLY);
        question.setBiannuallyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }

    @Test
    public void getsSelectedPaymentAmountsForAnnually() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        question.setPaymentFrequency(PaymentFrequency.ANNUALLY);
        question.setAnnuallyPaymentAmounts(PAYMENT_AMOUNTS);
        assertThat(question.getSelectedPaymentAmounts(), is(Optional.of(PAYMENT_AMOUNTS)));
    }
}
