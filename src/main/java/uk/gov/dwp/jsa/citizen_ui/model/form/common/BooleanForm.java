package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class BooleanForm<T extends BooleanQuestion> extends AbstractCounterForm<T> {

    public static final String BOOLEAN_VIEW_NAME = "form/common/boolean";
    @NotNull(message = "common.question.bool.required")
    @Valid
    private T question;

    @SuppressWarnings("squid:S2637")
    public BooleanForm() {
        // Default Constructor.
    }

    public BooleanForm(final T question) {
        this.question = question;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    public T getQuestion() {
        return question;
    }

    public void setQuestion(final T question) {
        this.question = question;
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
