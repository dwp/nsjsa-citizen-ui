package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class StringForm<T extends StringQuestion> extends AbstractCounterForm<T> {

    public static final String TEXT_AREA_VIEW_NAME = "form/common/text-area";
    public static final String TEXT_INPUT_VIEW_NAME = "form/common/text";
    @Valid
    @NotNull
    private T stringQuestion;

    private boolean showLabel = false;

    public T getStringQuestion() {
        return stringQuestion;
    }

    public T getQuestion() {
        return getStringQuestion();
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(final boolean showLabel) {
        this.showLabel = showLabel;
    }

    @Override
    public void setQuestion(final T question) {
        setStringQuestion(question);
    }

    public void setStringQuestion(final T question) {
        this.stringQuestion = question;
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
