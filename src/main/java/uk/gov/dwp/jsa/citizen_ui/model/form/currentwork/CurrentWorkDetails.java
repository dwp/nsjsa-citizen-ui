package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.OptionalPhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Structure to keep details for current work.
 */
public class CurrentWorkDetails {


    /**
     * Q39 Current work - Can you choose if you are paid?
     */
    private BooleanQuestion canChooseIfPaid;
    private PaymentFrequencyQuestion paymentFrequencyQuestion;
    private BooleanQuestion selfEmployedConfirmation;
    /**
     * Q27 Current Work - Employers name.
     */
    private StringQuestion employersName;

    /**
     * Q29 Current work Employer's address.
     */
    private EmployersAddressQuestion employersAddressQuestion;

    /**
     * Q30 Current Work - Employer's Telephone Number.
     */
    private StringQuestion employersPhone;

    /**
     * Q35 Average work hours question.
     */
    private HoursQuestion averageWorkHoursQuestion;
    private VoluntaryDetails voluntaryDetails;

    public CurrentWorkDetails() {
    }

    public CurrentWorkDetails(final BooleanQuestion canChooseIfPaid,
                              final PaymentFrequencyQuestion paymentFrequency,
                              final BooleanQuestion selfEmployedConfirmation,
                              final EmployersAddressQuestion employersAddressQuestion,
                              final StringQuestion employersName,
                              final HoursQuestion averageWorkHoursQuestion,
                              final VoluntaryDetails voluntaryDetails,
                              final OptionalPhoneQuestion employersPhone) {
        this.canChooseIfPaid = canChooseIfPaid;
        this.paymentFrequencyQuestion = paymentFrequency;
        this.employersAddressQuestion = employersAddressQuestion;
        this.selfEmployedConfirmation = selfEmployedConfirmation;
        this.employersName = employersName;
        this.employersPhone = employersPhone;
        this.averageWorkHoursQuestion = averageWorkHoursQuestion;
        this.voluntaryDetails = voluntaryDetails;
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

    public PaymentFrequencyQuestion getPaymentFrequencyQuestion() {
        if (paymentFrequencyQuestion == null) {
            paymentFrequencyQuestion = new PaymentFrequencyQuestion();
        }
        return paymentFrequencyQuestion;
    }

    public void setPaymentFrequencyQuestion(final PaymentFrequencyQuestion paymentFrequencyQuestion) {
        this.paymentFrequencyQuestion = paymentFrequencyQuestion;
    }


    public EmployersAddressQuestion getEmployersAddressQuestion() {
        if (employersAddressQuestion == null) {
            this.employersAddressQuestion =
                    new EmployersAddressQuestion();
        }
        return employersAddressQuestion;
    }

    public void setEmployersAddressQuestion(
            final EmployersAddressQuestion employersAddressQuestion) {
        this.employersAddressQuestion = employersAddressQuestion;
    }



    public BooleanQuestion getSelfEmployedConfirmation() {
        if (selfEmployedConfirmation == null) {
            this.selfEmployedConfirmation = new BooleanQuestion();
        }
        return selfEmployedConfirmation;
    }

    public void setSelfEmployedConfirmation(final BooleanQuestion selfEmployedConfirmation) {
        this.selfEmployedConfirmation = selfEmployedConfirmation;
    }

    public StringQuestion getEmployersName() {
        if (employersName == null) {
            employersName = new StringQuestion();
        }
        return employersName;
    }

    public void setEmployersName(final StringQuestion employersName) {
        this.employersName = employersName;
    }

    public StringQuestion getEmployersPhone() {
        if (employersPhone == null) {
            employersPhone = new OptionalPhoneQuestion();
        }
        return employersPhone;
    }

    public void setEmployersPhone(final StringQuestion employersPhone) {
        this.employersPhone = employersPhone;
    }

    public HoursQuestion getAverageWorkHoursQuestion() {
        if (averageWorkHoursQuestion == null) {
            this.averageWorkHoursQuestion = new HoursQuestion();
        }
        return averageWorkHoursQuestion;
    }

    public void setAverageWorkHoursQuestion(
            final HoursQuestion averageWorkHoursQuestion) {
        this.averageWorkHoursQuestion = averageWorkHoursQuestion;
    }

    public VoluntaryDetails getVoluntaryDetails() {
        if (voluntaryDetails == null) {
            this.voluntaryDetails = new VoluntaryDetails();
        }
        return voluntaryDetails;
    }

    public void setVoluntaryDetails(final VoluntaryDetails voluntaryDetails) {
        this.voluntaryDetails = voluntaryDetails;
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
