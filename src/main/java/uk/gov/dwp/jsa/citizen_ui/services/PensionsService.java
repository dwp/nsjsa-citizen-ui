package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasAnotherCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.util.Optional;
import java.util.stream.Stream;

import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;

@Service
public class PensionsService {
    private final ClaimRepository claimRepository;

    private static final String CURRENT_PENSION_IDENTIFIER =
            uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderNameController.IDENTIFIER;

    @Autowired
    public PensionsService(final ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public boolean hasCurrentPension(final String claimId) {
        return hasXPension(HasCurrentPensionController.IDENTIFIER, claimId);
    }

    private boolean hasXPension(final String identifier, final String claimId) {
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isPresent()) {
            Claim claim = optionalClaim.get();
            Optional<GuardQuestion> optionalGuardQuestion = claim.get(identifier, 0, GuardQuestion.class);
            boolean answer = false;
            if (optionalGuardQuestion.isPresent()) {
                answer = optionalGuardQuestion.get().getChoice().equals(Boolean.TRUE);
            }
            return answer;
        } else {
            return false;
        }
    }


    /**
     * Verify if the user is on the penultimate max pensions allowed.
     * @param count form count the user is currently on
     * @return boolean
     */

    public boolean isPenultimatePension(final Integer count) {
        return count == (MAX_PENSIONS_ALLOWED - 1);
    }

    public boolean canAddPension(final Claim claim) {
        return countAnswers(claim) < MAX_PENSIONS_ALLOWED;
    }

    public boolean hasMoreThanMaxAllowed(final Claim claim) {
        if (!canAddPension(claim)) {
            return Stream.of(HasAnotherCurrentPensionController.IDENTIFIER)
                    .filter(id -> new QuestionValueExtractor().getLoopEndQuestionValue(claim, id))
                    .count() > 0;
        } else {
            return false;
        }
    }

    private int countAnswers(final Claim claim) {
        return getAnswers(CURRENT_PENSION_IDENTIFIER, claim);
    }

    private int getAnswers(final String identifier, final Claim claim) {
        return claim.count(identifier, MAX_PENSIONS_ALLOWED);
    }

}
