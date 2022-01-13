package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.util.PostalAddressFormatter;
import uk.gov.dwp.jsa.citizen_ui.util.Strings;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.AddressLineOneConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.PostcodeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.TownOrCityConstraint;

import javax.validation.constraints.Pattern;

import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.ADDRESS_LINE_REGEX;
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.ADDRESS_MAX_LENGTH;
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX;


public class AddressQuestion implements Question {

    @AddressLineOneConstraint(message = "about.address.line1.error")
    private String addressLine1;

    @Pattern(regexp = ADDRESS_LINE_REGEX, message = "about.address.line2.error")
    @Pattern(regexp = NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX, message = "about.address.line2.error")
    private String addressLine2;

    @TownOrCityConstraint(message = "about.address.town.error")
    private String townOrCity;

    @PostcodeConstraint(message = "about.address.postcode.error")
    private String postCode;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(final String addressLine1) {
        this.addressLine1 = Strings.truncate(addressLine1, ADDRESS_MAX_LENGTH);
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(final String addressLine2) {
        this.addressLine2 = Strings.truncate(addressLine2, ADDRESS_MAX_LENGTH);
    }

    public String getTownOrCity() {
        return townOrCity;
    }

    public void setTownOrCity(final String townOrCity) {
        this.townOrCity = Strings.truncate(townOrCity, ADDRESS_MAX_LENGTH);
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(final String postCode) {
        this.postCode = trimPostCode(postCode);
    }

    public String getFormattedValue() {
        return PostalAddressFormatter.format(
                getAddressLine1(),
                getAddressLine2(),
                getTownOrCity(),
                getPostCode()
        );
    }

}

