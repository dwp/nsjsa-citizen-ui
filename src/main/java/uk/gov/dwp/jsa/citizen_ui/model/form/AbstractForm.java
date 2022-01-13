package uk.gov.dwp.jsa.citizen_ui.model.form;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

public abstract class AbstractForm<T extends Question> implements Form<T> {
    private String backRef;

    private String translationKey;

    private boolean counterForm;

    private EditMode editMode;

    public String getBackRef() {
        return backRef;
    }

    public void setBackRef(final String backRef) {
        this.backRef = backRef;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(final String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public boolean isCounterForm() {
        return counterForm;
    }

    @Override
    public void setCounterForm(final boolean counterForm) {
        this.counterForm = counterForm;
    }

    @Override
    public void setEdit(final EditMode editMode) {
        this.editMode = editMode;
    }

    @Override
    public EditMode getEdit() {
        return editMode;
    }
}
