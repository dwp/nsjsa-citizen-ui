package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class MultipleOptionsQuestion<T> implements Question, QuestionHolder {

    private Map<Integer, Question> answers = new HashMap<>();

    @NotNull
    private T userSelectionValue;

    private boolean isValid;

    @SuppressWarnings("squid:S2637")
    public MultipleOptionsQuestion() {
        // Empty default constructor to avoid SONAR violations.
    }

    public Map<Integer, Question> getAnswers() {
        return answers;
    }

    public T getUserSelectionValue() {
        return userSelectionValue;
    }

    public void setUserSelectionValue(final T userSelectionValue) {
        this.userSelectionValue =  userSelectionValue;
    }

    public final boolean isValid() {
        return isValid;
    }

    public final void setValid(final boolean valid) {
        isValid = valid;
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
