package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode.SINGLE;

@RunWith(MockitoJUnitRunner.class)
public class HasAnotherCurrentJobControllerTest {

    public static final String IDENTIFIER = "form/current-work/has-another-job";
    private HasAnotherCurrentJobController sut;

    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock GuardForm mockForm;
    @Mock LoopEndBooleanQuestion mockQuestion;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private StepInstance mockStepInstance;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private static final String CLAIM_ID = "1234566";

    private GuardForm form = new GuardForm();
    private BooleanQuestion booleanQuestion = new BooleanQuestion();

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        booleanQuestion = new BooleanQuestion();
        form.setQuestion(booleanQuestion);
        form.setCount(1);
        sut = new HasAnotherCurrentJobController(mockClaimRepository, mockRoutingService, mockCookieLocaleResolver);
    }

    @Test
    public void getAddWorkWithEditSingle_ReturnsViewName() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(step));
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockRequest.getParameter("edit")).thenReturn("SINGLE");
        String viewName = sut.getView(2, mockModel, COOKIE, mockRequest);
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("form/common/boolean"));
    }

    @Test
    public void getAddWorkWithEditSection_ReturnsViewName() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(step));
        when(mockRequest.getParameter("edit")).thenReturn("SECTION");
        String viewName = sut.getView(2, mockModel, COOKIE, mockRequest);
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("redirect:/form/summary"));
    }

    @Test
    public void getHasAnotherCurrentJobWithCounterTwo_ReturnsForm() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        String viewName = sut.getView(2, mockModel, COOKIE, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("form/common/boolean"));
    }

    @Test
    public void backRefIsSetCorrectlyOnForm() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn("BACK_URL");
        sut.setFormAttrs(form, CLAIM_ID);

        assertThat(form.getBackRef(), is("BACK_URL"));
    }

    @Test
    public void getMaxJobsWarningReturnsMaxJobWarningForm() {
        String viewName = sut.getMaxJobsWarning(mockModel);

        assertThat(viewName, is("form/current-work/max-jobs"));
    }

    @Test
    public void givenCounterIsLessThan4_getNextPathReturnsCorrectPath() {
        form.setCount(2);
        booleanQuestion.setChoice(true);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/current-work/details/3/is-work-paid"));
    }

    @Test
    public void givenCounterIs4_getNextPathReturnsMaxJobsView() {
        form.setCount(4);
        booleanQuestion.setChoice(true);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/current-work/max-jobs"));
    }

    @Test
    public void givenQuestionReplyIsNo_getNextPathReturnsHasPreviousWork() {
        booleanQuestion.setChoice(false);
        when(mockRoutingService.getNext(mockStepInstance)).thenReturn("/form/previous-employment/has-previous-work");

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/previous-employment/has-previous-work"));
    }

    @Test
    public void createNewFormReturnsQuestionWithCountSet() {
        GuardForm form = sut.createNewForm(mockClaim, 1);
        assertNotNull(form);
        assertThat(form.getQuestion(), is(new LoopEndBooleanQuestion()));
        assertThat(1, is(form.getCount()));
    }

    @Test
    public void givenCountIs4_updateClaimSetsHasMoreLimitTrue() {
        mockForm.setCount(4);

        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(mockClaim.count(any(String.class), any(Integer.TYPE))).thenReturn(4);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        GuardForm actual = sut.createNewForm(mockClaim,1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        GuardForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void GivenEndOfLoopsIsNotReached_UpdateClaim_SetsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockQuestion.getChoice()).thenReturn(true);
        when(mockClaim.count(anyString(), anyInt())).thenReturn(MAX_JOBS_ALLOWED - 1);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
    }

    @Test
    public void givenCounterIs4_getNextPathReturnsMaxJobsViewWithEditMode() {
        when(mockForm.getCount()).thenReturn(4);
        when(mockForm.getEdit()).thenReturn(SINGLE);
        when(mockForm.isGuardedCondition()).thenReturn(true);
        String nextPath = sut.getNextPath(mockClaim, mockForm, null);
        assertThat(nextPath, is("redirect:/form/current-work/max-jobs?edit=SINGLE"));
    }

    @Test
    public void GivenEndOfLoopsIsReached_UpdateClaim_SetsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockQuestion.getChoice()).thenReturn(true);
        when(mockClaim.count(anyString(), anyInt())).thenReturn(MAX_JOBS_ALLOWED);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim, atLeastOnce()).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        claimDB.getCircumstances().setHasExtraCurrentWork(true);

        final GuardForm<LoopEndBooleanQuestion> form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
        assertFalse("Should be false", form.getQuestion().getHasMoreThanLimit());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenNoMoreJobs() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);

        final GuardForm form = sut.getForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter4() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        addCurrentWork(claimDB);
        addCurrentWork(claimDB);
        addCurrentWork(claimDB);

        final GuardForm form = sut.getForm();
        form.setCount(4);

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }
}
