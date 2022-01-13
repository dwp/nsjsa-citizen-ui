package uk.gov.dwp.jsa.citizen_ui.model.form.claimStart;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JuryServiceConfirmationFormTests {

    private GuardForm sut;

    @Test
    public void isAGuard_returnsTrue() {
        sut = createSut(false);
        boolean actual = sut.isAGuard();
        assertThat(actual, is(true));
    }

    @Test
    public void givenHaveYouBeenIsFalse_isGuardedCondition_returnsTrue() {
        sut = createSut(true);
        boolean actual = sut.isGuardedCondition();
        assertThat(actual, is(true));
    }

    @Test
    public void givenHaveYouBeenIsTrue_isGuardedCondition_returnsFalse() {
        sut = createSut(false);
        boolean actual = sut.isGuardedCondition();
        assertThat(actual, is(false));
    }

    @Test
    public void givenJuryQuestionIsNull_isGuardedCondition_returnsFalse() {
        sut = new GuardForm();
        boolean actual = sut.isGuardedCondition();
        assertThat(actual, is(false));
    }

    private GuardForm createSut(boolean haveYouBeen) {
        return new GuardForm(new BooleanQuestion(haveYouBeen));
    }
}
