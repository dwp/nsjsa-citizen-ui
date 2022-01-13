package uk.gov.dwp.jsa.citizen_ui.resolvers.employment;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasAnotherCurrentJobController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.ChoosePaymentController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.CurrentWorkAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.HoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.SelfEmployedConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.VoluntaryPaidController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.resolvers.Resolver;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CurrentWorkResolver implements Resolver {

    private final QuestionValueExtractor extractor = new QuestionValueExtractor();

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {
        List<CurrentWork> currentWorks = new ArrayList<>();
        for (int counter = 1; counter <= Constants.MAX_JOBS_ALLOWED; counter++) {
            CurrentWork currentWork = new CurrentWork();

            Optional<Question> optionalQuestion = claim.get(WorkPaidOrVoluntaryController.IDENTIFIER, counter);
            if (!optionalQuestion.isPresent()) {
                break;
            }
            if (optionalQuestion.isPresent() && optionalQuestion.get() instanceof TypeOfWorkQuestion) {
                TypeOfWorkQuestion question = (TypeOfWorkQuestion) optionalQuestion.get();
                if (TypeOfWork.PAID.equals(question.getUserSelectionValue())) {
                    currentWork.setVoluntary(false);
                    currentWork.setPaid(true);
                } else if (TypeOfWork.VOLUNTARY.equals(question.getUserSelectionValue())) {
                    currentWork.setVoluntary(true);
                    currentWork.setPaid(false);
                }
            }

            currentWork.setCanChooseIfPaid(extractor.getBooleanValueWithIdentifier(claim,
                    ChoosePaymentController.IDENTIFIER, counter));
            currentWork.setVoluntaryJobPaid(extractor.getBooleanValueWithIdentifier(claim,
                    VoluntaryPaidController.IDENTIFIER, counter));

            Optional<Question> optionalPaymtFreqQuestion = claim.get(PaymentFrequencyController.IDENTIFIER, counter);
            if (optionalPaymtFreqQuestion.isPresent()
                    && optionalPaymtFreqQuestion.get() instanceof PaymentFrequencyQuestion) {
                PaymentFrequencyQuestion paymentQuestion = (PaymentFrequencyQuestion) optionalPaymtFreqQuestion.get();
                currentWork.setPaymentFrequency(paymentQuestion.getPaymentFrequency().name());
                paymentQuestion.getSelectedPaymentAmounts()
                        .ifPresent(payment -> currentWork.setNetPay(payment.getNet()));
            }

            currentWork.setEmployerName(extractor.getStringQuestionValueWithIdentifier(claim,
                    EmployersNameController.IDENTIFIER, counter));
            currentWork.setEmployerAddress(extractor.getEmployerAddressQuestionValueWithIdentifier(claim,
                    CurrentWorkAddressController.IDENTIFIER, counter));

            Optional<Question> optionalHoursQues = claim.get(HoursController.IDENTIFIER, counter);
            if (optionalHoursQues.isPresent() && optionalHoursQues.get() instanceof HoursQuestion) {
                HoursQuestion hours = (HoursQuestion) optionalHoursQues.get();
                currentWork.setHoursPerWeek(hours.getHours());
            }

            currentWork.setSelfEmployedOrDirector(extractor.getBooleanValueWithIdentifier(claim,
                    SelfEmployedConfirmationController.IDENTIFIER, counter));

            currentWorks.add(currentWork);
        }
        circumstances.setHasExtraCurrentWork(extractor.getLoopEndQuestionValue(claim,
                HasAnotherCurrentJobController.IDENTIFIER));
        circumstances.setCurrentWork(currentWorks);
    }

}
