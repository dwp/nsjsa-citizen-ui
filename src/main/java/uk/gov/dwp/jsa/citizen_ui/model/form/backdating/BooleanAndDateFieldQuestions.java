package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public abstract class BooleanAndDateFieldQuestions implements Question {

    protected Boolean hasProvidedAnswer;
    protected DateRangeQuestionWithBoolean dateRangeQuestion;

    public BooleanAndDateFieldQuestions(final Boolean hasProvidedAnswer,
                                        final DateRangeQuestionWithBoolean dateRangeQuestion) {
        this.hasProvidedAnswer = hasProvidedAnswer;
        this.dateRangeQuestion = dateRangeQuestion;
    }

    public BooleanAndDateFieldQuestions() { }

    public abstract Boolean getHasProvidedAnswer();

    public abstract DateRangeQuestionWithBoolean getDateRangeQuestion();

    public abstract void setHasProvidedAnswer(final Boolean hasProvidedAnswer);

    public abstract void setDateRangeQuestion(final DateRangeQuestionWithBoolean dateRangeQuestion);

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
