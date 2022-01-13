package uk.gov.dwp.jsa.citizen_ui.model.form.about;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LanguagePreferenceFormTest {

    LanguagePreferenceForm languagePreferenceForm;
    LanguagePreferenceQuestion languagePreferenceQuestion = new LanguagePreferenceQuestion(false, false);

    @Before
    public void setup() {
        languagePreferenceForm = new LanguagePreferenceForm();
        languagePreferenceForm.setLanguagePreferenceQuestion(languagePreferenceQuestion);
    }

    @Test
    public void getLanguagePreferenceQuestion() {
        LanguagePreferenceQuestion question = languagePreferenceForm.getLanguagePreferenceQuestion();
        assertThat(question, is(languagePreferenceQuestion));
    }

    @Test
    public void setLanguagePreferenceQuestion() {
        LanguagePreferenceQuestion newQuestion = new LanguagePreferenceQuestion();
        newQuestion.setWelshContact(true);
        newQuestion.setWelshSpeech(true);
        languagePreferenceForm.setLanguagePreferenceQuestion(newQuestion);

        assertThat(languagePreferenceForm.getLanguagePreferenceQuestion(), is(newQuestion));
        assertThat(newQuestion.getWelshContact(), is(true));
        assertThat(newQuestion.getWelshSpeech(), is(true));
    }

    @Test
    public void getQuestion() {
        LanguagePreferenceQuestion question = languagePreferenceForm.getQuestion();
        assertThat(question, is(languagePreferenceQuestion));
    }

    @Test
    public void setQuestion() {
        LanguagePreferenceQuestion newQuestion = new LanguagePreferenceQuestion();
        newQuestion.setWelshContact(true);
        newQuestion.setWelshSpeech(true);
        languagePreferenceForm.setQuestion(newQuestion);

        assertThat(languagePreferenceForm.getLanguagePreferenceQuestion(), is(newQuestion));
        assertThat(newQuestion.getWelshContact(), is(true));
        assertThat(newQuestion.getWelshSpeech(), is(true));
    }

    @Test
    public void radioOptions() {
        assertThat(languagePreferenceForm.radioOptions(), is(Arrays.asList(true, false)));
    }
}
