package uk.gov.dwp.jsa.citizen_ui.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RestoreClaim {

    private ClaimDB claimDB;
    private Claim claim;

    public RestoreClaim(final ClaimDB claimDB, final Claim claim) {
        this.claim = claim;
        this.claimDB = claimDB;
    }

    public Claim getClaim() {
        return claim;
    }

    public ClaimDB getClaimDB() {
        return claimDB;
    }

    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
