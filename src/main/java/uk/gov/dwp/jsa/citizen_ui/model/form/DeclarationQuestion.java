package uk.gov.dwp.jsa.citizen_ui.model.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class DeclarationQuestion implements Question {
    @AssertTrue
    @NotNull
    private boolean agreed;

    private boolean isAgreedInError;

    private String locale;

    public DeclarationQuestion(final boolean agreed) {
        this.agreed = agreed;
    }

    public DeclarationQuestion() {
        this(false);
    }

    public final boolean isAgreedInError() {
        return isAgreedInError;
    }

    public final void setAgreedInError(final boolean agreedInError) {
        isAgreedInError = agreedInError;
    }

    public final boolean isAgreed() {
        return agreed;
    }

    public final void setAgreed(final boolean agreed) {
        this.agreed = agreed;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }
}
