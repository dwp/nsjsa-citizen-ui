package uk.gov.dwp.jsa.citizen_ui.model.form;

import javax.validation.Valid;

public class BankAccountForm extends AbstractForm<BankAccountQuestion> {
    /**
     * Q84 Bank Details question response.
     */
    @Valid private BankAccountQuestion bankAccountQuestion;

    public static final String ACCOUNT_HOLDER_VALIDATION_REGEX
            = "^(((\\p{IsLatin}+[. '\\-]*)+\\p{IsLatin}+[. '\\-]*)|(\\p{IsLatin}+)|^$)";

    public static final int ACCOUNT_HOLDER_MAX_LENGTH = 81;

    public static final String ACCOUNT_NUMBER_REGEX = "[0-9]{8}";

    public static final String SORT_CODE_REGEX = "(^[0-9]{2}-[0-9]{2}-[0-9]{2})|(^[0-9]{6})";

    public static final String REFERENCE_NUMBER_REGEX = "^[a-zA-Z0-9]*$";

    public BankAccountForm(@Valid final BankAccountQuestion bankAccountQuestion) {
        this.bankAccountQuestion = bankAccountQuestion;
    }

    public BankAccountQuestion getBankAccountQuestion() {
        return bankAccountQuestion;
    }

    public void setBankAccountQuestion(final BankAccountQuestion bankAccountQuestion) {
        this.bankAccountQuestion = bankAccountQuestion;
    }

    @Override
    public BankAccountQuestion getQuestion() {
        return getBankAccountQuestion();
    }

    @Override
    public void setQuestion(final BankAccountQuestion question) {
        setBankAccountQuestion(question);
    }
}
