package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.thymeleaf.util.StringUtils;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BankAccountNumberConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BankAccountReferenceConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.SortCodeConstraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm.ACCOUNT_HOLDER_MAX_LENGTH;
import static uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm.ACCOUNT_HOLDER_VALIDATION_REGEX;


/**
 * Q84 Bank Details question.
 */
public class BankAccountQuestion implements Question {

    @NotEmpty(message = "bankaccount.holder.blank")
    @Size(max = ACCOUNT_HOLDER_MAX_LENGTH, message = "bankaccount.holder.length.error")
    @Pattern(regexp = ACCOUNT_HOLDER_VALIDATION_REGEX, message = "bankaccount.holder.pattern.error")
    private String accountHolder;

    @SortCodeConstraint
    private SortCode sortCode;

    @BankAccountNumberConstraint
    private String accountNumber;

    @BankAccountReferenceConstraint
    private String referenceNumber;

    public BankAccountQuestion() {
    }

    public BankAccountQuestion(final String accountHolder, final SortCode sortCode, final String accountNumber) {
        this.accountHolder = accountHolder;
        this.sortCode = sortCode;
        this.accountNumber = sanitiseAccountNumer(accountNumber);
    }

    private String sanitiseAccountNumer(final String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return accountNumber;
        } else {
            return accountNumber.replace(" ", "");
        }
    }

    public BankAccountQuestion(final String accountHolder,
                               final SortCode sortCode,
                               final String accountNumber,
                               final String referenceNumber) {
        this.accountHolder = accountHolder;
        this.sortCode = sortCode;
        this.accountNumber = sanitiseAccountNumer(accountNumber);
        this.referenceNumber = referenceNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(final String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public SortCode getSortCode() {
        return sortCode;
    }

    public void setSortCode(final SortCode sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = sanitiseAccountNumer(accountNumber);
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(final String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(getAccountHolder())
                && StringUtils.isEmpty(getAccountNumber())
                && StringUtils.isEmpty(getReferenceNumber())
                && (getSortCode() == null || getSortCode().isEmpty());
    }

}
