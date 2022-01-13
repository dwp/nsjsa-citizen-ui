package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BankAccountQuestionTest {

    private BankAccountQuestion bankAccountQuestion;

    @Before
    public void before() {
        bankAccountQuestion = new BankAccountQuestion();
    }

    @Test
    public void isEmpty_AllValuesAreEmpty_true() {
        assertThat(bankAccountQuestion.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void isEmpty_AccountHolderHasValue_false() {
        bankAccountQuestion.setAccountHolder("AccountHolder");
        assertThat(bankAccountQuestion.isEmpty(), is(equalTo(false)));
    }

    @Test
    public void isEmpty_AccountNumberHasValue_false() {
        bankAccountQuestion.setAccountNumber("AccountNumber");
        assertThat(bankAccountQuestion.isEmpty(), is(equalTo(false)));
    }

    @Test
    public void isEmpty_ReferenceNumberHasValue_false() {
        bankAccountQuestion.setReferenceNumber("ReferenceNumber");
        assertThat(bankAccountQuestion.isEmpty(), is(equalTo(false)));
    }

    @Test
    public void isEmpty_SortCodeHasValue_false() {
        SortCode sortCode = new SortCode("one");
        bankAccountQuestion.setSortCode(sortCode);
        assertThat(bankAccountQuestion.isEmpty(), is(equalTo(false)));
    }

}
