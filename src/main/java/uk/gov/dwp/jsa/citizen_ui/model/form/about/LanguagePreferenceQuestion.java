package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.NotNull;

public class LanguagePreferenceQuestion implements Question {
    @NotNull
    private Boolean welshContact;
    @NotNull
    private Boolean welshSpeech;

    public LanguagePreferenceQuestion(final Boolean welshContact, final Boolean welshSpeech) {
        this.welshContact = welshContact;
        this.welshSpeech = welshSpeech;
    }

    public LanguagePreferenceQuestion() {
    }

    public Boolean getWelshContact() {
        return welshContact;
    }

    public void setWelshContact(final Boolean welshContact) {
        this.welshContact = welshContact;
    }

    public Boolean getWelshSpeech() {
        return welshSpeech;
    }

    public void setWelshSpeech(final Boolean welshSpeech) {
        this.welshSpeech = welshSpeech;
    }
}
