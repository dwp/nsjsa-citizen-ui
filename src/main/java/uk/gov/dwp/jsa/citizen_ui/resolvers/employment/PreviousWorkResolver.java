package uk.gov.dwp.jsa.citizen_ui.resolvers.employment;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PreviousWork;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.AddWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployerWhyJobEndController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersDatesController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.ExpectPaymentController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;
import uk.gov.dwp.jsa.citizen_ui.resolvers.Resolver;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PreviousWorkResolver implements Resolver {

    private final QuestionValueExtractor extractor = new QuestionValueExtractor();


    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {

        List<PreviousWork> currentWorks = new ArrayList<>();
        for (int counter = 1; counter <= Constants.MAX_JOBS_ALLOWED; counter++) {

            PreviousWork previousWork = new PreviousWork();

            Optional<Question> optionalDateQuestion = claim.get(EmployersDatesController.IDENTIFIER, counter);
            if (!optionalDateQuestion.isPresent()) {
                break;
            }
            if (optionalDateQuestion.isPresent() && optionalDateQuestion.get() instanceof EmploymentDurationQuestion) {
                EmploymentDurationQuestion dateRangeQuestion = (EmploymentDurationQuestion) optionalDateQuestion.get();
                DateQuestion startDate = dateRangeQuestion.getStartDate();
                DateQuestion endDate = dateRangeQuestion.getEndDate();
                previousWork.setStartDate(LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDay()));
                previousWork.setEndDate(LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.getDay()));
            }

            Optional<Question> whyJobEndedOptional = claim.get(EmployerWhyJobEndController.IDENTIFIER, counter);
            if (whyJobEndedOptional.isPresent() && whyJobEndedOptional.get() instanceof WhyJobEndQuestion) {
                WhyJobEndQuestion whyJobEndQuestion = (WhyJobEndQuestion) whyJobEndedOptional.get();
                WhyJobEndedReason whyJobEndedReason = whyJobEndQuestion.getWhyJobEndedReason();
                previousWork.setReasonEnded(whyJobEndedReason != null ? whyJobEndedReason.name() : null);
                previousWork.setOtherReasonDetails(whyJobEndQuestion.getDetailedReason());
            }

            previousWork.setEmployerName(extractor.getStringQuestionValueWithIdentifier(claim,
                    uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.
                            employerdetails.EmployersNameController.IDENTIFIER, counter));

            previousWork.setEmployerAddress(extractor.getEmployerAddressQuestionValueWithIdentifier(claim,
                    EmployersAddressController.IDENTIFIER, counter));

            previousWork.setPaymentExpected(extractor.getBooleanValueWithIdentifier(claim,
                    ExpectPaymentController.IDENTIFIER, counter));

            previousWork.setSelfEmployedOrDirector(extractor.getBooleanValueWithIdentifier(claim,
                    uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.
                            employerdetails.EmploymentStatusController.IDENTIFIER, counter));

            currentWorks.add(previousWork);
        }
        circumstances.setHasExtraPreviousWork(extractor.getLoopEndQuestionValue(claim,
                AddWorkController.IDENTIFIER));
        circumstances.setPreviousWork(currentWorks);
    }
}
