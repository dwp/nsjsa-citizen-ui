package uk.gov.dwp.jsa.citizen_ui.resolvers;

import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;

public interface Resolver {

    void resolve(final Claim claim, final Circumstances circumstances);

}
