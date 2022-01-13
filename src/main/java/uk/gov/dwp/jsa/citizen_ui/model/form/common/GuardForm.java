package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

public class GuardForm<T extends BooleanQuestion> extends BooleanForm<T> {

    public GuardForm(final T question) {
        super(question);
    }

    public GuardForm() {
    }

    @Override
    public boolean isAGuard() {
        return true;
    }

    @Override
    public boolean isGuardedCondition() {
        return resolve(() -> getQuestion().getChoice()).orElse(false);
    }
}
