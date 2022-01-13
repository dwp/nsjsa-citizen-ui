package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class PersonalDetails {

    /**
     * The date of birth question response.
     */
    private DateOfBirthQuestion dateOfBirthQuestion;
    /**
     * The nino question response.
     */
    private NinoQuestion ninoQuestion;

    /**
     * Title Question Response.
     */
    private TitleQuestion titleQuestion;
    /**
     * First Name Question Response.
     */
    private NameStringTruncatedQuestion firstNameQuestion;
    /**
     * Last Name Question Response.
     */
    private NameStringTruncatedQuestion lastNameQuestion;
    /**
     * Q9 About you - address.
     */
    private AddressQuestion addressQuestion;
    /**
     * Q10 - Does the claimant require an additional postal address.
     */
    private BooleanQuestion postalQuestion;
    /**
     * Q11 About you - claimant has an additional postal address..
     */
    private PostalAddressQuestion postalAddressQuestion;

    private LanguagePreferenceQuestion languagePreferenceQuestion;

    private ContactPreferences contactPreferences;

    public AddressQuestion getAddressQuestion() {
        if (addressQuestion == null) {
            addressQuestion = new AddressQuestion();
        }
        return addressQuestion;
    }

    public void setAddressQuestion(final AddressQuestion addressQuestion) {
        this.addressQuestion = addressQuestion;
    }

    public BooleanQuestion getPostalQuestion() {
        if (postalQuestion == null) {
            postalQuestion = new BooleanQuestion();
        }
        return postalQuestion;
    }

    public void setPostalQuestion(final BooleanQuestion postalQuestion) {
        this.postalQuestion = postalQuestion;
    }

    public PostalAddressQuestion getPostalAddressQuestion() {
        if (postalAddressQuestion == null) {
            postalAddressQuestion = new PostalAddressQuestion();
        }
        return postalAddressQuestion;
    }

    public void setPostalAddressQuestion(final PostalAddressQuestion postalAddressQuestion) {
        this.postalAddressQuestion = postalAddressQuestion;
    }

    public DateOfBirthQuestion getDateOfBirthQuestion() {
        if (dateOfBirthQuestion == null) {
            dateOfBirthQuestion = new DateOfBirthQuestion();
        }
        return dateOfBirthQuestion;
    }

    public void setDateOfBirthQuestion(final DateOfBirthQuestion dateOfBirthQuestion) {
        this.dateOfBirthQuestion = dateOfBirthQuestion;
    }

    public NinoQuestion getNinoQuestion() {
        if (ninoQuestion == null) {
            ninoQuestion = new NinoQuestion();
        }
        return ninoQuestion;
    }

    public void setNinoQuestion(final NinoQuestion ninoQuestion) {
        this.ninoQuestion = ninoQuestion;
    }

    public TitleQuestion getTitleQuestion() {
        if (titleQuestion == null) {
            titleQuestion = new TitleQuestion();
        }
        return titleQuestion;
    }

    public void setTitleQuestion(final TitleQuestion titleQuestion) {
        this.titleQuestion = titleQuestion;
    }

    public NameStringTruncatedQuestion getFirstNameQuestion() {
        if (firstNameQuestion == null) {
            firstNameQuestion = new NameStringTruncatedQuestion();
        }
        return firstNameQuestion;
    }

    public void setFirstNameQuestion(final NameStringTruncatedQuestion firstNameQuestion) {
        this.firstNameQuestion = firstNameQuestion;
    }

    public NameStringTruncatedQuestion getLastNameQuestion() {
        if (lastNameQuestion == null) {
            lastNameQuestion = new NameStringTruncatedQuestion();
        }
        return lastNameQuestion;
    }

    public void setLastNameQuestion(final NameStringTruncatedQuestion lastNameQuestion) {
        this.lastNameQuestion = lastNameQuestion;
    }

    public ContactPreferences getContactPreferences() {
        if (contactPreferences == null) {
            contactPreferences = new ContactPreferences();
        }
        return contactPreferences;
    }

    public void setContactPreferences(final ContactPreferences contactPreferences) {
        this.contactPreferences = contactPreferences;
    }

    public LanguagePreferenceQuestion getLanguagePreferenceQuestion() {
        if (languagePreferenceQuestion == null) {
            languagePreferenceQuestion = new LanguagePreferenceQuestion();
        }
        return languagePreferenceQuestion;
    }

    public void setLanguagePreferenceQuestion(final LanguagePreferenceQuestion languagePreferenceQuestion) {
        this.languagePreferenceQuestion = languagePreferenceQuestion;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
