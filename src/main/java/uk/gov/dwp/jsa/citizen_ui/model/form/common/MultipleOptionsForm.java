package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.springframework.util.Assert.notNull;

public class MultipleOptionsForm<Q extends MultipleOptionsQuestion, T extends Enum> extends AbstractCounterForm<Q> {

    public static final String MULTIPLE_OPTIONS_VIEW_NAME = "form/common/multiple-options";

    private boolean inline;

    private T defaultOption;

    private List<T> options;

    @NotNull
    @Valid
    private Q multipleOptionsQuestion;

    private T trueConditionValue;

    public MultipleOptionsForm(final Q multipleOptionsQuestion, final T trueConditionValue) {
        notNull(multipleOptionsQuestion, "multipleOptionsQuestion");
        notNull(trueConditionValue, "trueConditionValue");
        this.multipleOptionsQuestion = multipleOptionsQuestion;
        this.trueConditionValue = trueConditionValue;
    }

    public Q getMultipleOptionsQuestion() {
        return multipleOptionsQuestion;
    }

    @Override
    public Q getQuestion() {
        return getMultipleOptionsQuestion();
    }

    public void setQuestion(final Q question) {
        setMultipleOptionsQuestion(question);
    }

    public void setMultipleOptionsQuestion(final Q multipleOptionsQuestion) {
        this.multipleOptionsQuestion = multipleOptionsQuestion;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInline(final boolean inline) {
        this.inline = inline;
    }

    public T getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(final T defaultOption) {
        this.defaultOption = defaultOption;
    }

    public List<T> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void setOptions(final List<T> options) {
        this.options = Collections.unmodifiableList(options);
    }

    @Override
    public boolean isAGuard() {
        return true;
    }

    @Override
    public boolean isGuardedCondition() {
        return trueConditionValue.equals(multipleOptionsQuestion.getUserSelectionValue());
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
