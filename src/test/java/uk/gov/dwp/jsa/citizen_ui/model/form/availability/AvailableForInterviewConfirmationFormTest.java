package uk.gov.dwp.jsa.citizen_ui.model.form.availability;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AvailableForInterviewConfirmationFormTest {

    private static final BooleanQuestion TRUE_QUESTION = new BooleanQuestion(Boolean.TRUE);
    private static final BooleanQuestion FALSE_QUESTION = new BooleanQuestion(Boolean.FALSE);
    private static final BooleanQuestion NULL_QUESTION = null;
    private static final BooleanQuestion QUESTION_WITH_NULL_VALUE = new BooleanQuestion(null);

    private boolean isAGuard;
    private BooleanQuestion question;
    private boolean isGuardedCondition;
    private GuardForm form;

    @Test
    public void isAGuardedConditionReturnsTrue() {
        givenAFormWithQuestion(TRUE_QUESTION);
        whenIGetIsGuardedCondition();
        thenIsGuardConditionIsTrue();
    }

    @Test
    public void isAGuardedConditionReturnsFalse() {
        givenAFormWithQuestion(FALSE_QUESTION);
        whenIGetIsGuardedCondition();
        thenIsGuardConditionIsFalse();
    }

    @Test
    public void isAGuardedConditionReturnsFalseForNullQuestion() {
        givenAFormWithQuestion(NULL_QUESTION);
        whenIGetIsGuardedCondition();
        thenIsGuardConditionIsFalse();
    }

    @Test
    public void isAGuardedConditionReturnsFalseForQuestionWithNullValue() {
        givenAFormWithQuestion(QUESTION_WITH_NULL_VALUE);
        whenIGetIsGuardedCondition();
        thenIsGuardConditionIsFalse();
    }

    @Test
    public void isAGuardAlwaysReturnsTrue() {
        givenAFormWithQuestion(FALSE_QUESTION);
        whenIGetIsAGuard();
        thenIsAGuardIsAlwaysTrue();
    }

    @Test
    public void constructorSetsQuestion() {
        givenAFormWithQuestion(FALSE_QUESTION);
        whenIGetQuestion();
        thenTheQuestionIsSet();
    }

    @Test
    public void setsQuestion() {
        givenAFormWithQuestion(FALSE_QUESTION);
        whenISetQuestion();
        whenIGetQuestion();
        thenTheQuestionIsSet();
    }

    private void thenTheQuestionIsSet() {
        assertThat(question, is(FALSE_QUESTION));
    }

    private void givenAFormWithQuestion(BooleanQuestion question) {
        form = new GuardForm(question);
    }

    private void whenIGetQuestion() {
        question = form.getQuestion();
    }

    private void whenISetQuestion() {
        form.setQuestion(FALSE_QUESTION);
    }

    private void whenIGetIsAGuard() {
        isAGuard = form.isAGuard();
    }

    private void whenIGetIsGuardedCondition() {
        isGuardedCondition = form.isGuardedCondition();
    }

    private void thenIsAGuardIsAlwaysTrue() {
        assertThat(isAGuard, is(true));
    }

    private void thenIsGuardConditionIsTrue() {
        assertThat(isGuardedCondition, is(true));
    }

    private void thenIsGuardConditionIsFalse() {
        assertThat(isGuardedCondition, is(false));
    }

}
