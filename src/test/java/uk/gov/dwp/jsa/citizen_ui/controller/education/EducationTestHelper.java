package uk.gov.dwp.jsa.citizen_ui.controller.education;

import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Education;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;

import static java.util.Objects.isNull;

public class EducationTestHelper {
    public static Education addEducation(ClaimDB claimDB) {
        Circumstances circumstances = claimDB.getCircumstances();
        if (isNull(circumstances)) {
            circumstances = new Circumstances();
            claimDB.setCircumstances(circumstances);
        }
        Education education = new Education();
        claimDB.getCircumstances().setEducation(education);
        return education;
    }
}
