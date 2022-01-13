package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import java.util.Optional;
import java.util.UUID;

import static uk.gov.dwp.jsa.citizen_ui.Constants.DATA_VERSION_KEY;

@Service
public class BankDetailsService {

    private final String appVersion;

    private ClaimRepository claimRepository;

    public BankDetailsService(final ClaimRepository claimRepository,
                              @Value("${" + DATA_VERSION_KEY + "}") final String appVersion) {
        this.claimRepository = claimRepository;
        this.appVersion = appVersion;
    }

    public Optional<BankDetails> getDataFromClaim(final UUID claimId) {
        Claim claim = claimRepository.findById(claimId.toString()).orElse(null);
        if (claim != null) {
            BankDetails bankDetails = new BankDetails();
            final String claimantId = claim.getClaimantId();
            if (null != claimantId) {
                bankDetails.setClaimantId(UUID.fromString(claimantId));
            }

            bankDetails.setServiceVersion(appVersion);

            Optional<Question> optionalQuestion = claim.get(BankAccountFormController.IDENTIFIER);
            if (optionalQuestion.isPresent() && optionalQuestion.get() instanceof BankAccountQuestion) {
                BankAccountQuestion bankAccountQuestion = (BankAccountQuestion) optionalQuestion.get();
                bankDetails.setAccountHolder(bankAccountQuestion.getAccountHolder());
                bankDetails.setAccountNumber(bankAccountQuestion.getAccountNumber());
                final SortCode sortCode = bankAccountQuestion.getSortCode();
                //Ensure we get the sanitised sort code to provide to bank details service
                bankDetails.setSortCode(sortCode == null ? null : sortCode.getSanitisedCode());
                bankDetails.setReference(bankAccountQuestion.getReferenceNumber());
            }
            if (!bankDetails.isEmpty()) {
                return Optional.of(bankDetails);
            }

        }
        return Optional.empty();
    }
}
