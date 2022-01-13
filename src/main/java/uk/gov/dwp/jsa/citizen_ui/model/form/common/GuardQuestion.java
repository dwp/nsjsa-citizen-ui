package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class GuardQuestion extends BooleanQuestion implements QuestionHolder {
    private Map<Integer, Question> answers = new HashMap<>();

    public GuardQuestion(final Boolean choice) {
        super(choice);
    }

    public GuardQuestion() {
    }

    public Map<Integer, Question> getAnswers() {
        return answers;
    }

    public void setAnswers(final Map<Integer, Question> answers) {
        this.answers = answers;
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
