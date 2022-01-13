package uk.gov.dwp.jsa.citizen_ui.model;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AttendInterviewController;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.InterviewAvailability;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.Section.NONE;

@RunWith(JUnitParamsRunner.class)
public class ClaimTests {

    public final String annonymousStepId = "annonymous-step-id";
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    StepInstance mockStepInstance;
    @Mock
    StepInstance mockStepInstance1;
    @Mock
    Step mockStep;
    @Mock
    Question mockQuestion;
    GuardQuestion guardQuestion = new GuardQuestion();
    @Mock
    Question mockUpdatedQuestion;

    private Claim sut;

    @Before
    public void setUp() {
        sut = new Claim();
    }

    @Test
    public void GivenQuestionDoesntExist_SaveAddsNewEntry() {
        sut.save(mockStepInstance, mockQuestion, Optional.empty());

        Optional<Question> actual = sut.get(mockStepInstance);
        assertThat(actual.get(), is(mockQuestion));
    }

    @Test
    public void GivenQuestionExists_SaveUpdatesExistingEntry() {
        sut.save(mockStepInstance, mockQuestion, Optional.empty());
        sut.save(mockStepInstance, mockUpdatedQuestion, Optional.empty());

        Optional<Question> actual = sut.get(mockStepInstance);
        assertThat(actual.get(), is(mockUpdatedQuestion));
    }

    @Test
    public void GivenGuardQuestionExists_SaveUpdatesExistingEntry() {
        sut.save(mockStepInstance, guardQuestion, Optional.empty());
        sut.save(mockStepInstance1, mockUpdatedQuestion, Optional.of(mockStepInstance));
        StringQuestion updatedQuestion = new StringQuestion();
        sut.save(mockStepInstance1, updatedQuestion, Optional.of(mockStepInstance));

        Optional<Question> actual = sut.get(mockStepInstance);
        GuardQuestion actualGuardQues = (GuardQuestion) actual.get();
        assertThat(actualGuardQues.getAnswers().values().iterator().next(), is(updatedQuestion));
    }

    @Test(expected = IllegalArgumentException.class)
    public void GivenStepInstanceIsNull_SaveThrowsException() {
        sut.save(null, mockQuestion, Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void GivenQuestionIsNull_SaveThrowsException() {
        sut.save(mockStepInstance, null, Optional.empty());
    }


    @Test
    public void GivenStepInstanceDoesntExist_GetReturnsEmptyOptional() {
        Optional<Question> actual = sut.get(mockStepInstance);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void GivenIdentifierDoesntExist_GetReturnsEmptyOptional() {
        Optional<Question> actual = sut.get("bad/step/identifier");
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void GivenIdentifierExists_GetReturnsCorrectQuestion() {
        String identifier = "bad/step/identifier";
        sut.save(createStepInstance(identifier, 0), mockQuestion, Optional.empty());

        Optional<Question> actual = sut.get(identifier);

        assertThat(actual, is(Optional.of(mockQuestion)));
    }

    @Test
    public void GivenIdentifierExistsForCounter_GetReturnsCorrectQuestion() {
        String identifier = "bad/step/identifier";
        sut.save(createStepInstance(identifier, 1), mockQuestion, Optional.empty());

        Optional<Question> actual = sut.get(identifier, 1);
        assertEquals(actual, Optional.of(mockQuestion));
    }

    private StepInstance createStepInstance(String identifier, int count) {
        Step step = new Step(identifier, "", "", NONE);
        return new StepInstance(step, count, false, false, false);
    }

    @Test
    public void GivenMultipleStepInstancesExist_GetAllReturnsOnlyRelatedQuestions() {

        String annonymousStepId = "annonymous-step-id";

        sut.save(mockStepInstance, mockQuestion, Optional.empty());
        sut.save(createStepInstance(annonymousStepId, 1), mockQuestion, Optional.empty());
        sut.save(createStepInstance(annonymousStepId, 2), mockQuestion, Optional.empty());
        sut.save(createStepInstance("something/else", 1), mockQuestion, Optional.empty());

        List<Question> questions = sut.getAll(annonymousStepId, 4);
        assertEquals(2, questions.size());
    }

    @Test
    public void GivenNoStepInstancesExist_GetAllReturnsEmptyList() {
        List<Question> questions = sut.getAll("no/such/identifier", 4);
        assertEquals(0, questions.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void GivenStepInstanceIsNull_GetAllThrowsException() {
        sut.getAll(null, 4);
    }

    @Test
    public void GivenQuestionHasNotBeenAnswered_GetTypedQuestion_ReturnsEmpty() {

        Optional<ClaimStartDateQuestion> actual = sut.get("step-id", ClaimStartDateQuestion.class);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    @Parameters({
            "step-id, 0",
            "step-2, 2",
            "step-3, 99"
    })
    public void GivenQuestionHasBeenAnswered_GetTypedQuestion_ReturnsCorrectValue(String stepId, int count) {
        Step step = new Step("form/personal-details/date-of-birth", "", "", NONE);
        StepInstance stepInstance = new StepInstance(
                step,
                count,
                false,
                false,
                false);
        ClaimStartDateQuestion dummyClaimStartDateQuestion = new ClaimStartDateQuestion();
        dummyClaimStartDateQuestion.setDay(10);
        dummyClaimStartDateQuestion.setDay(11);
        dummyClaimStartDateQuestion.setDay(2012);
        sut.save(stepInstance, dummyClaimStartDateQuestion, Optional.empty());

        Optional<ClaimStartDateQuestion> actual = sut.get(
                stepInstance.getStep().getIdentifier(),
                count,
                ClaimStartDateQuestion.class);
        assertThat(actual.get(), is(dummyClaimStartDateQuestion));
    }


    @Test
    public void GivenAttendInterviewHasNotBeenAnswered_GetAttendInterview_ReturnsEmpty() {
        Optional<AttendInterviewQuestion> actual = sut.getAttendInterview();
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void GivenAttendInterviewHasBeenAnswered_GetAttendInterview_ReturnsCorrectValue() {
        Step step = new Step(AttendInterviewController.IDENTIFIER, "", "", NONE);
        StepInstance attendInterviewStepInstance = new StepInstance(
                step,
                0,
                false,
                false,
                false
        );
        AttendInterviewQuestion dummyInterviewQuestion = new AttendInterviewQuestion();
        dummyInterviewQuestion.setDaysNotToAttend(new InterviewAvailability().createWorkingDays());
        sut.save(attendInterviewStepInstance, dummyInterviewQuestion, Optional.empty());

        Optional<AttendInterviewQuestion> actual = sut.getAttendInterview();
        assertThat(actual.get(), is(dummyInterviewQuestion));
    }

    @Test
    public void GivenDifferentTypeOfQuestionHasBeenAnswered_GetTypedQuestion_ReturnsEmpty() {
        Step step = new Step("step-id", "", "", NONE);
        StepInstance stepInstance = new StepInstance(
                step,
                0,
                false,
                false,
                false);
        ClaimStartDateQuestion dummyClaimStartDateQuestion = new ClaimStartDateQuestion();
        dummyClaimStartDateQuestion.setDay(10);
        dummyClaimStartDateQuestion.setDay(11);
        dummyClaimStartDateQuestion.setDay(2012);
        sut.save(stepInstance, dummyClaimStartDateQuestion, Optional.empty());

        Optional<NinoQuestion> actual = sut.get(stepInstance.getStep().getIdentifier(), NinoQuestion.class);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void givenAnEmptyMap_searchReturnsOptionlEmpty() {
        Optional<Question> actual = sut.search(mockStepInstance, new HashMap<>());
        assertThat(actual, is(Optional.empty()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullInstance_searchThrowsException() {
        sut.search(null, new HashMap<>());
    }

    @Test
    public void givenStepInstanceOnFirstLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> stepInstanceMap = new HashMap<>();
        int hashKey = 123876;
        when(mockStepInstance.getClaimKey()).thenReturn(hashKey);
        stepInstanceMap.put(hashKey, mockQuestion);

        Question question1 = new BooleanQuestion(true);
        Question question2 = new StringQuestion("test");

        stepInstanceMap.put(1, question1);
        stepInstanceMap.put(2, question2);

        Optional<Question> actual = sut.search(mockStepInstance, stepInstanceMap);

        assertThat(actual.get(), is(mockQuestion));
    }

    @Test
    public void givenStepInstanceOnSecondLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> level1Map = new HashMap<>();
        int hashKey = 123;

        when(mockStepInstance.getClaimKey()).thenReturn(hashKey);

        Question question1 = new BooleanQuestion(true);
        Question question2 = new StringQuestion("test");
        level1Map.put(1, question1);
        level1Map.put(2, question2);

        BooleanQuestion question3 = new BooleanQuestion(false);
        GuardQuestion guardQuestion = new GuardQuestion();
        level1Map.put(4, guardQuestion);

        Map<Integer, Question> level2Map = new HashMap<>();
        level2Map.put(3, question3);
        level2Map.put(hashKey, mockQuestion);
        guardQuestion.setAnswers(level2Map);

        Optional<Question> actual = sut.search(mockStepInstance, level1Map);

        assertThat(actual.get(), is(mockQuestion));
    }

    @Test
    public void givenTwoGuardQuestionsOnSecondLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> level1Map = new LinkedHashMap<>();

        when(mockStepInstance.getClaimKey()).thenReturn(3);

        GuardQuestion guard1 = new GuardQuestion(true);
        GuardQuestion guard2 = new GuardQuestion(true);

        StringQuestion question = new StringQuestion("test-value");

        Map<Integer, Question> guard2Answers = new HashMap<>();
        guard2Answers.put(3, question);
        guard2.setAnswers(guard2Answers);

        level1Map.put(1, guard1);
        level1Map.put(2, guard2);


        Optional<Question> actual = sut.search(mockStepInstance, level1Map);

        assertThat(actual.get(), is(question));
    }

    @Test
    public void givenTwoGuardQuestionsInDiffOrderOnSecondLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> level1Map = new LinkedHashMap<>();

        when(mockStepInstance.getClaimKey()).thenReturn(3);

        GuardQuestion question1 = new GuardQuestion(true);
        GuardQuestion question2 = new GuardQuestion(true);

        StringQuestion question3 = new StringQuestion("testSecondLevel");
        Map<Integer, Question> answers1 = new HashMap<>();
        answers1.put(3, question3);
        question2.setAnswers(answers1);

        level1Map.put(2, question2);
        level1Map.put(1, question1);


        Optional<Question> actual = sut.search(mockStepInstance, level1Map);

        assertThat(actual.get(), is(question3));
    }

    @Test
    public void givenTwoMultipleOptionQuestionsOnSecondLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> level1Map = new LinkedHashMap<>();

        int keyStringQuestion = 3;
        when(mockStepInstance.getClaimKey()).thenReturn(keyStringQuestion);

        MultipleOptionsQuestion multipleOptionsQuestion1 = new MultipleOptionsQuestion();
        MultipleOptionsQuestion multipleOptionsQuestion2 = new MultipleOptionsQuestion();

        StringQuestion stringQuestion = new StringQuestion("testSecondLevel");
        Map<Integer, Question> multipleOption1Answers = new HashMap<>();
        multipleOption1Answers.put(keyStringQuestion, stringQuestion);
        multipleOptionsQuestion2.setAnswers(multipleOption1Answers);

        level1Map.put(1, multipleOptionsQuestion1);
        level1Map.put(2, multipleOptionsQuestion2);

        Optional<Question> actual = sut.search(mockStepInstance, level1Map);

        assertThat(actual.get(), is(stringQuestion));
    }

    @Test
    public void givenTwoMultipleOptionQuestionsInDiffOrderOnSecondLevel_SearchReturnsExpectedQuestion() {
        Map<Integer, Question> level1Map = new LinkedHashMap<>();

        int keyStringQuestion = 3;
        when(mockStepInstance.getClaimKey()).thenReturn(keyStringQuestion);

        MultipleOptionsQuestion multipleOptionsQuestion1 = new MultipleOptionsQuestion();
        MultipleOptionsQuestion multipleOptionsQuestion2 = new MultipleOptionsQuestion();

        StringQuestion stringQuestion = new StringQuestion("testSecondLevel");
        Map<Integer, Question> multipleOption1Answers = new HashMap<>();
        multipleOption1Answers.put(keyStringQuestion, stringQuestion);
        multipleOptionsQuestion2.setAnswers(multipleOption1Answers);

        level1Map.put(2, multipleOptionsQuestion2);
        level1Map.put(1, multipleOptionsQuestion1);

        Optional<Question> actual = sut.search(mockStepInstance, level1Map);

        assertThat(actual.get(), is(stringQuestion));
    }

    @Test
    public void givenQuestionOnFirstLevel_SavePersistsInHashMap() {
        int hashKey = 123876;
        when(mockStepInstance.getClaimKey()).thenReturn(hashKey);

        sut.save(mockStepInstance, mockQuestion, Optional.empty());
        Optional<Question> actualQuestion = sut.search(mockStepInstance, sut.getAnswers());

        assertThat(actualQuestion.get(), is(mockQuestion));
    }

    @Test
    public void givenGuardQuestionInMultiLevelMap_SavePersistsInHashMap() {
        Question question1 = new GuardQuestion(true);
        Question question2 = new StringQuestion("level 1");
        StepInstance stepInstance1 = createStepInstance("ID1", true);
        StepInstance stepInstance2 = createStepInstance("ID2", false);

        sut.save(stepInstance1, question1, Optional.empty());
        sut.save(stepInstance2, question2, Optional.empty());

        Question question3 = new StringQuestion("level 2");
        StepInstance stepInstance3 = createStepInstance("ID3", false);
        sut.save(stepInstance3, question3, Optional.of(stepInstance1));

        Optional<Question> actual = sut.get("ID3");

        assertThat(actual.get(), is(question3));
    }

    @Test
    public void givenMultipleOptionQuestionInMultiLevelMap_SavePersistsInHashMap() {
        Question question1 = new MultipleOptionsQuestion();
        Question question2 = new StringQuestion("level 1");
        StepInstance stepInstance1 = createStepInstance("1", true);
        StepInstance stepInstance2 = createStepInstance("2", false);

        sut.save(stepInstance1, question1, Optional.empty());
        sut.save(stepInstance2, question2, Optional.empty());

        Question question3 = new StringQuestion("level 2");
        StepInstance stepInstance3 = createStepInstance("3", false);
        sut.save(stepInstance3, question3, Optional.of(stepInstance1));

        Optional<Question> actual = sut.search(stepInstance3, sut.getAnswers());

        assertThat(actual.get(), is(question3));
    }

    @Test
    public void givenIdentifier_shouldDeleteInstance() {
        String identifier = "identifier";
        Step step = new Step(identifier, null, null, null);
        StepInstance instance = new StepInstance(false, false, step);

        sut.save(instance, mockQuestion, Optional.empty());

        Optional<Question> question = sut.get(identifier);
        assertTrue(question.isPresent());
        assertThat(question.get(), is(mockQuestion));

        sut.delete(instance, 1);

        question = sut.get(identifier);
        assertFalse(question.isPresent());
    }

    @Test
    public void givenIdentifier_shouldDeleteInstanceByCounter() {
        String identifier = "identifier";
        Step step = new Step(identifier, null, null, null);
        StepInstance instance1 = new StepInstance(step, 0, false, false, false);
        StepInstance instance2 = new StepInstance(step, 1, false, false, false);

        sut.save(instance1, mockQuestion, Optional.empty());
        sut.save(instance2, mockQuestion, Optional.empty());

        sut.delete(instance2, 1);

        Optional<Question> question = sut.get(identifier, 0);
        assertTrue(question.isPresent());

        question = sut.get(identifier, 1);
        assertFalse(question.isPresent());
    }

    @Test
    public void givenAnswerSet_shouldGetAnswer() {
        String identifier = "identiier";
        Optional<Question> optionalQuestion = sut.get(identifier);
        assertFalse(optionalQuestion.isPresent());
        sut.setAnswer(identifier, new LanguagePreferenceQuestion());
        optionalQuestion = sut.get(identifier);
        assertTrue(optionalQuestion.isPresent());
    }

    private StepInstance createStepInstance(final String identifier, final boolean isGuard) {
        return new StepInstance(isGuard, isGuard, new Step(identifier, "", "", Section.NONE));
    }
}
