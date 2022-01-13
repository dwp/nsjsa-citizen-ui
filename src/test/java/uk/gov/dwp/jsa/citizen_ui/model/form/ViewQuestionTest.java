package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

public class ViewQuestionTest {

    @Test
    public void bankDetailsQuestion_QuestionIsNull_isEmptyIsTrue() {
        final Claim claim = new Claim();
        ViewQuestion viewQuestion = ViewQuestion.bankDetailsQuestion(BankAccountFormController.IDENTIFIER, claim);

        assertThat(viewQuestion.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void bankDetailsQuestion_QuestionPropertiesAreNull_isEmptyIsTrue() {
        final Claim claim = Mockito.mock(Claim.class);
        given(claim.get(BankAccountFormController.IDENTIFIER)).willReturn(Optional.of(new BankAccountQuestion()));

        ViewQuestion viewQuestion = ViewQuestion.bankDetailsQuestion(BankAccountFormController.IDENTIFIER, claim);

        assertThat(viewQuestion.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void bankDetailsQuestion_QuestionPropertiesAreNotNull_isEmptyIsFalse() {
        final BankAccountQuestion bankAccountQuestion = new BankAccountQuestion();
        bankAccountQuestion.setAccountNumber("12345678");

        final Claim claim = Mockito.mock(Claim.class);
        given(claim.get(BankAccountFormController.IDENTIFIER)).willReturn(Optional.of(bankAccountQuestion));

        ViewQuestion viewQuestion = ViewQuestion.bankDetailsQuestion(BankAccountFormController.IDENTIFIER, claim);

        assertThat(viewQuestion.isEmpty(), is(equalTo(false)));
    }
}
