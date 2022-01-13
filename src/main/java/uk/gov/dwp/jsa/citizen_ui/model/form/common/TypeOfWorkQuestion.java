package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;

public class TypeOfWorkQuestion extends MultipleOptionsQuestion<TypeOfWork> {
    public TypeOfWorkQuestion(final TypeOfWork typeOfWork) {
        this.setUserSelectionValue(typeOfWork);
    }

    public TypeOfWorkQuestion() {

    }
}
