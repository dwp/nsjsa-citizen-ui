package uk.gov.dwp.jsa.citizen_ui.model.form;

import javax.validation.Valid;

/**
 * Class to store declaration form Details.
 */
public class DeclarationForm extends AbstractForm<DeclarationQuestion> {

    @Valid
    private DeclarationQuestion declarationQuestion;

    public DeclarationQuestion getDeclarationQuestion() {
        return getQuestion();
    }

    public void setDeclarationQuestion(final DeclarationQuestion declarationQuestion) {
        this.declarationQuestion = declarationQuestion;
    }

    @Override
    public DeclarationQuestion getQuestion() {
        return declarationQuestion;
    }

    @Override
    public void setQuestion(final DeclarationQuestion question) {
        this.declarationQuestion = question;
    }
}
