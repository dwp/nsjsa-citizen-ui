package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PaymentFrequencyForm extends AbstractCounterForm<PaymentFrequencyQuestion> {

    @NotNull
    @Valid
    private PaymentFrequencyQuestion paymentFrequencyQuestion;

    public PaymentFrequencyQuestion getPaymentFrequencyQuestion() {
        return paymentFrequencyQuestion;
    }

    @Override
    public PaymentFrequencyQuestion getQuestion() {
        return getPaymentFrequencyQuestion();
    }

    @Override
    public void setQuestion(final PaymentFrequencyQuestion question) {
        setPaymentFrequencyQuestion(question);
    }

    public void setPaymentFrequencyQuestion(final PaymentFrequencyQuestion paymentFrequencyQuestion) {
        this.paymentFrequencyQuestion = paymentFrequencyQuestion;
    }
}
