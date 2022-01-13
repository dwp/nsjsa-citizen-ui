package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ClaimStartDateResolver implements Resolver {
    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {
        Optional<Question> optionalQuestion = claim.get(ClaimStartDateController.IDENTIFIER);
        if (optionalQuestion.isPresent() && optionalQuestion.get() instanceof ClaimStartDateQuestion) {
            ClaimStartDateQuestion claimStartQuestion = (ClaimStartDateQuestion) optionalQuestion.get();
            circumstances.setClaimStartDate(LocalDate.of(claimStartQuestion.getYear(),
                    claimStartQuestion.getMonth(), claimStartQuestion.getDay()));
        }
    }
}
