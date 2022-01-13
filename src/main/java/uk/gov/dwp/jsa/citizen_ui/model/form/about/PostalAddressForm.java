package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;

/**
 * Q11 Postal Address form.
 */
public class PostalAddressForm extends AbstractForm<PostalAddressQuestion> {

    @Valid
    private PostalAddressQuestion postalAddressQuestion;

    public PostalAddressForm() {
    }

    public PostalAddressForm(@Valid final PostalAddressQuestion postalAddressQuestion) {
        this.postalAddressQuestion = postalAddressQuestion;
    }

    public PostalAddressQuestion getPostalAddressQuestion() {
        return getQuestion();
    }

    public void setPostalAddressQuestion(final PostalAddressQuestion postalAddressQuestion) {
        this.postalAddressQuestion = postalAddressQuestion;
    }

    @Override
    public PostalAddressQuestion getQuestion() {
        return postalAddressQuestion;
    }

    @Override
    public void setQuestion(final PostalAddressQuestion question) {
        this.postalAddressQuestion = question;
    }
}
