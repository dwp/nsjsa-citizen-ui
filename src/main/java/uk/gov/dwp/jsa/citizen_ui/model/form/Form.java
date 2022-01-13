package uk.gov.dwp.jsa.citizen_ui.model.form;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

public interface Form<T extends Question> {

    default boolean isAGuard() {
        return false;
    }

    default boolean isGuardedCondition() {
        return false;
    }

    default boolean isCounterForm() {
        return false;
    }

    default void setCounterForm(final boolean counterForm) {
        // Do Nothing
    }

    T getQuestion();

    void setQuestion(final T question);

    void setEdit(EditMode editMode);

    void setBackRef(final String backRef);

    EditMode getEdit();

    default boolean hasNoGuard() {
        return false;
    }
}
