package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import uk.gov.dwp.jsa.citizen_ui.model.EmployersDetails;
import uk.gov.dwp.jsa.citizen_ui.model.PreviousEmployment;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.OptionalPhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.CurrentWorkDetails;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.VoluntaryDetails;

import static java.util.Arrays.asList;

public class Helpers {

    public static PreviousEmployment createPreviousEmploymentExpectingPayment(final boolean expectingPayment) {
        PreviousEmployment previousEmployment = new PreviousEmployment();
        EmployersDetails employerDetails1 = getExpectPaymentQuestionWithValue(expectingPayment);
        EmployersDetails employerDetails2 = getExpectPaymentQuestionWithValue(false);
        EmployersDetails employerDetails3 = getExpectPaymentQuestionWithValue(false);
        EmployersDetails employerDetails4 = getExpectPaymentQuestionWithValue(false);
        previousEmployment.setEmployerDetailsList(
                asList(employerDetails1, employerDetails2, employerDetails3, employerDetails4)
        );
        return previousEmployment;
    }

    private static EmployersDetails getExpectPaymentQuestionWithValue(final boolean value) {
        EmployersDetails employerDetails = new EmployersDetails();
        BooleanQuestion expectPaymentQuestion = new BooleanQuestion();
        expectPaymentQuestion.setChoice(value);
        employerDetails.setExpectPaymentQuestion(expectPaymentQuestion);
        return employerDetails;
    }

    public static CurrentWork createPaidCurrentWorkWithFrequency(PaymentFrequency frequency) {
        BooleanQuestion yes = new BooleanQuestion(true);
        TypeOfWorkQuestion typeOfWork = new TypeOfWorkQuestion();
        typeOfWork.setUserSelectionValue(TypeOfWork.PAID);

        PaymentFrequencyQuestion paymentFrequency = new PaymentFrequencyQuestion();
        paymentFrequency.setPaymentFrequency(frequency);

        String employersName = "name";
        StringQuestion employersNameQuestion = new StringQuestion(employersName);
        int averageWorkingHours = 40;
        HoursQuestion hoursQuestion = new HoursQuestion(averageWorkingHours);
        OptionalPhoneQuestion phoneQuestion = new OptionalPhoneQuestion("0123 4567890");
        EmployersAddressQuestion addressQuestion = new EmployersAddressQuestion();
        VoluntaryDetails voluntaryDetails = new VoluntaryDetails(yes, yes, typeOfWork);
        CurrentWorkDetails workDetails = new CurrentWorkDetails(yes, paymentFrequency, yes, addressQuestion,
                employersNameQuestion, hoursQuestion, voluntaryDetails, phoneQuestion);
        return new CurrentWork(yes, workDetails);
    }
}
