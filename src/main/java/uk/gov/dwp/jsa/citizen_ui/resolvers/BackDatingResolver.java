package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.AskedForAdvice;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.BackDating;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.NonWorkingIllness;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.TravelledOutsideUk;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouAvailableForWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouSearchingForWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WhyNotApplySoonerController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.FullTimeEducationController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouBeenInPaidWorkSinceController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouAskedForAdviceController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouBeenUnableToWorkDueToIllnessController;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.HaveYouTravelledOutsideController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.AskedForAdviceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.BooleanAndDateFieldQuestions;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class BackDatingResolver implements Resolver {
    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {
        BackDating backDating = new BackDating();
        Optional<Question> wereYouAvailableToWork = claim.get(WereYouAvailableForWorkController.IDENTIFIER);
        Optional<Question> wereYouSearchingForWork = claim.get(WereYouSearchingForWorkController.IDENTIFIER);
        Optional<Question> applySooner = claim.get(WhyNotApplySoonerController.IDENTIFIER);
        Optional<Question> inEducation = claim.get(FullTimeEducationController.IDENTIFIER);
        Optional<Question> inWork = claim.get(HaveYouBeenInPaidWorkSinceController.IDENTIFIER);
        Optional<Question> askedAdvice = claim.get(HaveYouAskedForAdviceController.IDENTIFIER);
        Optional<Question> illness = claim.get(HaveYouBeenUnableToWorkDueToIllnessController.IDENTIFIER);
        Optional<Question> outsideUk = claim.get(HaveYouTravelledOutsideController.IDENTIFIER);

        if (wereYouAvailableToWork.isPresent()) {
            backDating.setWereYouAvailableForWork(((BooleanQuestion) wereYouAvailableToWork.get()).getChoice());
        }
        if (wereYouSearchingForWork.isPresent()) {
            backDating.setWereYouSearchingForWork(((BooleanQuestion) wereYouSearchingForWork.get()).getChoice());
        }
        if (applySooner.isPresent()) {
            backDating.setWhyNotApplySooner(((StringQuestion) applySooner.get()).getValue());
        }
        if (inEducation.isPresent()) {
            backDating.setInFullTimeEducationSince(((BooleanQuestion) inEducation.get()).getChoice());
        }
        if (inWork.isPresent()) {
            backDating.setInPaidWorkSince(((BooleanQuestion) inWork.get()).getChoice());
        }
        if (askedAdvice.isPresent()) {
            AskedForAdviceQuestion question = (AskedForAdviceQuestion) askedAdvice.get();
            backDating.setAskedForAdvice(new AskedForAdvice(
                    question.getValue(),
                    question.getHasHadAdvice()
            ));
        }
        if (illness.isPresent()) {
            BooleanAndDateFieldQuestions question = (BooleanAndDateFieldQuestions) illness.get();
            LocalDate start = null, end = null;
            if (question.getHasProvidedAnswer() != null && question.getHasProvidedAnswer()) {
                start = question.getDateRangeQuestion().getStartDate().getLocalDate();
                end = question.getDateRangeQuestion().getEndDate().getLocalDate();
            }
            backDating.setNonWorkingIllness(
                    new NonWorkingIllness(question.getHasProvidedAnswer(), start, end)
            );
        }
        if (outsideUk.isPresent()) {
            BooleanAndDateFieldQuestions question = (BooleanAndDateFieldQuestions) outsideUk.get();
            LocalDate start = null, end = null;
            if (question.getHasProvidedAnswer() != null && question.getHasProvidedAnswer()) {
                start = question.getDateRangeQuestion().getStartDate().getLocalDate();
                end = question.getDateRangeQuestion().getEndDate().getLocalDate();
            }
            backDating.setTravelledOutsideUk(
                    new TravelledOutsideUk(question.getHasProvidedAnswer(), start, end)
            );
        }

        circumstances.setBackDating(backDating);
    }
}
