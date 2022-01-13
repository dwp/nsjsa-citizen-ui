package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;

public class AddressForm extends AbstractForm<AddressQuestion> {

    public static final int ADDRESS_MAX_LENGTH = 27;
    public static final int TOWN_OR_CITY_MAX_LENGTH = ADDRESS_MAX_LENGTH;

    /*
     * The following regex originally came from the following web site, via a best answer from stack overflow.
     * It has been adapted though to exclude characters C,I,K,M,O,V(and lower too) from the inward code, which doesnt
     * seem to be conform to the standard postcode regex pattern.
     *
     *          https://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
     *          https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom#Validation
     */
    public static final String POSTCODE_REGEX =
            "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|"
                + "(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))"
                    + "\\s?[0-9][ABD-Habd-hJLNP-Ujlnp-uW-Zw-z]{2})";

    public static final String ADDRESS_LINE_REGEX = "^[ A-Za-z0-9,'.-]*$|^\\s$*";
    public static final String TOWN_OR_CITY_LINE_REGEX = ADDRESS_LINE_REGEX;

    /*
     * This regex ensures that we dont have two consecutive characters of either
     *  commas (,) apostrophe ('), full stop (.), hyphen and space( )
     */
    public static final String NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX = "^(?!.*([ ,'.-])\\1).*$";

    @Valid
    private AddressQuestion addressQuestion;

    public AddressForm(@Valid final AddressQuestion addressQuestion) {
        this.addressQuestion = addressQuestion;
    }

    public AddressForm() {
    }

    public AddressQuestion getAddressQuestion() {
        return getQuestion();
    }

    public void setAddressQuestion(final AddressQuestion addressQuestion) {
        this.addressQuestion = addressQuestion;
    }

    @Override
    public AddressQuestion getQuestion() {
        return addressQuestion;
    }

    @Override
    public void setQuestion(final AddressQuestion question) {
        this.addressQuestion = question;
    }
}
