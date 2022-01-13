package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;

import static org.junit.Assert.*;

public class PersonalDetailsTest {
    PersonalDetails sut;

    @Before
    public void setup() {
        sut = new PersonalDetails();
    }

    @Test
    public void getLanguagePreferenceQuestion() {
        LanguagePreferenceQuestion languagePreferenceQuestion = new LanguagePreferenceQuestion();

        sut.setLanguagePreferenceQuestion(languagePreferenceQuestion);

        assertEquals(languagePreferenceQuestion, sut.getLanguagePreferenceQuestion());
    }
}
