package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import java.util.Map;

public interface QuestionHolder {
    Map<Integer, Question> getAnswers();
}
