package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionsPaymentFrequencyQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PaymentFrequencyFormTest {
    private static final PaymentFrequencyQuestion CURRENT_WORK_QUESTION = new EmploymentPaymentFrequencyQuestion();
    private static final PaymentFrequencyQuestion PENSIONS_QUESTION = new PensionsPaymentFrequencyQuestion();

    @Test
    public void setsPaymentFrequencyQuestionForEmployment() {
        PaymentFrequencyForm form = new PaymentFrequencyForm();
        form.setPaymentFrequencyQuestion(CURRENT_WORK_QUESTION);
        assertThat(form.getPaymentFrequencyQuestion(), is(CURRENT_WORK_QUESTION));
    }

    @Test
    public void setsPaymentFrequencyQuestionForPensions() {
        PaymentFrequencyForm form = new PaymentFrequencyForm();
        form.setPaymentFrequencyQuestion(PENSIONS_QUESTION);
        assertThat(form.getPaymentFrequencyQuestion(), is(PENSIONS_QUESTION));
    }
}
