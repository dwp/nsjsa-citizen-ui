package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

import uk.gov.dwp.jsa.adaptors.dto.claim.Address;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class CurrentWorkTestHelper {

    public static CurrentWork addCurrentWork(ClaimDB claimDB) {
        Circumstances circumstances = claimDB.getCircumstances();
        if (isNull(circumstances)) {
            circumstances = new Circumstances();
            claimDB.setCircumstances(circumstances);
        }
        List<CurrentWork> currentWorkList = circumstances.getCurrentWork();
        if (isNull(currentWorkList)) {
            currentWorkList = new ArrayList<>();
            circumstances.setCurrentWork(currentWorkList);
        }
        final CurrentWork currentWork = new CurrentWork();
        currentWorkList.add(currentWork);
        return currentWork;
    }

    public static Address getAddress(final String line1, final String line2, final String postocde, final String town,
                                     String country) {
        Address address = new Address();
        address.setFirstLine(line1);
        address.setSecondLine(line2);
        address.setPostCode(postocde);
        address.setTown(town);
        address.setCountry(country);
        return address;
    }

}
