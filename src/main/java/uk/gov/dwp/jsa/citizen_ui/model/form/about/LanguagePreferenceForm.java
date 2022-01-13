package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class LanguagePreferenceForm extends AbstractForm<LanguagePreferenceQuestion> {

    @Valid
    @NotNull
    private LanguagePreferenceQuestion languagePreferenceQuestion;

    public LanguagePreferenceForm() { }

    public LanguagePreferenceForm(@Valid final LanguagePreferenceQuestion languagePreferenceQuestion) {
        this.languagePreferenceQuestion = languagePreferenceQuestion;
    }

    public LanguagePreferenceQuestion getLanguagePreferenceQuestion() {
        return languagePreferenceQuestion;
    }

    public void setLanguagePreferenceQuestion(final LanguagePreferenceQuestion languagePreferenceQuestion) {
        this.languagePreferenceQuestion = languagePreferenceQuestion;
    }

    @Override
    public LanguagePreferenceQuestion getQuestion() {
        return languagePreferenceQuestion;
    }

    @Override
    public void setQuestion(final LanguagePreferenceQuestion question) {
        this.languagePreferenceQuestion = question;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

}
