package uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits;

import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.OtherBenefit;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;

import static java.util.Objects.isNull;

public class OtherBenefitTestHelper {
    public static OtherBenefit addOtherBenefit(ClaimDB claimDB) {
        Circumstances circumstances = claimDB.getCircumstances();
        if (isNull(circumstances)) {
            circumstances = new Circumstances();
            claimDB.setCircumstances(circumstances);
        }
        OtherBenefit otherBenefit = new OtherBenefit();
        claimDB.getCircumstances().setOtherBenefit(otherBenefit);
        return otherBenefit;
    }
}
