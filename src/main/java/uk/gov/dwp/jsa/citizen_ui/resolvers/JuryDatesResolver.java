package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.JuryService;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

@Component
public class JuryDatesResolver implements Resolver {

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {

        boolean hasJuryDates = new QuestionValueExtractor().getBooleanValueWithIdentifier(
                claim, JuryServiceConfirmationController.IDENTIFIER, 0
        );
        if (hasJuryDates) {
            JuryService juryService = new JuryService();

            claim.get(JuryServiceDatesController.IDENTIFIER)
                    .ifPresent(question -> fillAnswerToEducation(question, juryService));

            circumstances.setJuryService(juryService);
        }
    }

    private void fillAnswerToEducation(final Question question,
                                       final JuryService juryService) {
        if (question instanceof DateRangeQuestion) {
            juryService.setStartDate(((DateRangeQuestion) question).getStartDate().getLocalDate());
            juryService.setEndDate(((DateRangeQuestion) question).getEndDate().getLocalDate());
        } else {
            throw new UnsupportedOperationException(
                    String.format("Unsupported controller with idntifier %s", JuryServiceDatesController.IDENTIFIER)
            );
        }
    }

}
