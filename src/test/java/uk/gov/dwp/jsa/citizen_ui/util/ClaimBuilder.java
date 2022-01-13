package uk.gov.dwp.jsa.citizen_ui.util;

import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasCurrentWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.HasPreviousWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.ExpectPaymentController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.controller.Section.NONE;

public class ClaimBuilder {

    HashMap<StepInstance, Question> questions = new HashMap<>();

    private void addBooleanQuestion(final String stepIdentifier, final boolean answer) {
        Step step = new Step(stepIdentifier, "", "", NONE);
        StepInstance stepInstance = new StepInstance(step, 0, false, false, false);
        BooleanQuestion question = new BooleanQuestion(answer);
        questions.put(stepInstance, question);
    }

    private void addGuardQuestion(final String stepIdentifier, final boolean answer) {
        Step step = new Step(stepIdentifier, "", "", NONE);
        StepInstance stepInstance = new StepInstance(step, 0, true, true, false);
        GuardQuestion question = new GuardQuestion(answer);
        questions.put(stepInstance, question);
    }

    private void addPayQuestion(final String identifier, final PaymentFrequencyQuestion question) {
        Step step = new Step(identifier, "", "", NONE);
        StepInstance stepInstance = new StepInstance(
                step,
                0,
                false,
                false,
                false);
        questions.put(stepInstance, question);
    }

    private void addDateQuestion(final String identifier, final ClaimStartDateQuestion question){
        Step step = new Step(identifier, "", "", NONE);
        StepInstance instance = new StepInstance(
                step,
                0,
                false,
                false,
                false);
        questions.put(instance, question);
    }

    public ClaimBuilder withJuryService(final boolean answer) {
        addBooleanQuestion(JuryServiceConfirmationController.IDENTIFIER, answer);
        return this;
    }

    public ClaimBuilder withPreviousJob(final boolean answer) {
        addBooleanQuestion(HasPreviousWorkController.IDENTIFIER, answer);
        return this;
    }

    public ClaimBuilder withExpectedPayment(final boolean answer) {
        addBooleanQuestion(ExpectPaymentController.IDENTIFIER, answer);
        return this;
    }

    public ClaimBuilder withCurrentJob(final boolean answer) {
        addBooleanQuestion(HasCurrentWorkController.IDENTIFIER, answer);
        return this;
    }

    public ClaimBuilder withWeeklyPay() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        PaymentAmounts amounts = new PaymentAmounts();
        amounts.setNet(new BigDecimal(10.32));
        question.setWeeklyPaymentAmounts(amounts);
        question.setPaymentFrequency(PaymentFrequency.WEEKLY);

        addPayQuestion(PaymentFrequencyController.IDENTIFIER, question);
        return this;
    }

    public ClaimBuilder withFortnightlyPay() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        PaymentAmounts amounts = new PaymentAmounts();
        amounts.setNet(new BigDecimal(10.32));
        question.setFortnightlyPaymentAmounts(amounts);
        question.setPaymentFrequency(PaymentFrequency.FORTNIGHTLY);

        addPayQuestion(PaymentFrequencyController.IDENTIFIER, question);
        return this;
    }

    public ClaimBuilder withMonthlyPay() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        PaymentAmounts amounts = new PaymentAmounts();
        amounts.setNet(new BigDecimal(10.32));
        question.setMonthlyPaymentAmounts(amounts);
        question.setPaymentFrequency(PaymentFrequency.MONTHLY);

        addPayQuestion(PaymentFrequencyController.IDENTIFIER, question);
        return this;
    }

    public ClaimBuilder withFourWeeklyPay() {
        PaymentFrequencyQuestion question = new PaymentFrequencyQuestion();
        PaymentAmounts amounts = new PaymentAmounts();
        amounts.setNet(new BigDecimal(10.32));
        question.setFourweeklyPaymentAmounts(amounts);
        question.setPaymentFrequency(PaymentFrequency.FOURWEEKLY);

        addPayQuestion(PaymentFrequencyController.IDENTIFIER, question);
        return this;
    }

    public ClaimBuilder withCurrentPension(final boolean answer) {
        addGuardQuestion(HasCurrentPensionController.IDENTIFIER, answer);
        return this;
    }

    public ClaimBuilder withBackDatedClaim(ClaimStartDateQuestion question){
        addDateQuestion(ClaimStartDateController.IDENTIFIER, question);
            return this;
    }

    public Claim build() {
        Claim claim = new Claim();

        for (StepInstance stepInstance : questions.keySet()) {
            claim.save(stepInstance, questions.get(stepInstance), Optional.empty());
        }

        return claim;
    }
}
