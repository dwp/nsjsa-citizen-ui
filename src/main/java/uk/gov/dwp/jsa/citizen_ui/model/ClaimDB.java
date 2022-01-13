package uk.gov.dwp.jsa.citizen_ui.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.Constants;

/**
 * The DB claim stored in Redis.
 */
@RedisHash(value = "ClaimDB", timeToLive = Constants.REDIS_TTL)
public class ClaimDB {

    @Id
    private String id;

    private Claimant claimant;

    private Circumstances circumstances;

    private BankDetails bankDetails;

    public Claimant getClaimant() {
        return claimant;
    }

    public void setClaimant(final Claimant claimant) {
        this.claimant = claimant;
    }

    public Circumstances getCircumstances() {
        return circumstances;
    }

    public void setCircumstances(final Circumstances circumstances) {
        this.circumstances = circumstances;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(final BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }
}
