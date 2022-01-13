package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
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

@RunWith(MockitoJUnitRunner.class)
public class AddWorkControllerTest {

    private AddWorkController sut;
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private Step mockStep;
    @Mock GuardForm mockForm;
    @Mock LoopEndBooleanQuestion mockQuestion;
    private GuardForm workForm = new GuardForm();
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    @Mock
    private StepInstance stepInstance;
    private BooleanQuestion addWorkQuestion = new BooleanQuestion();

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(mockStep));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        sut = new AddWorkController(claimRepository, mockRoutingService);
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
        addWorkQuestion = new BooleanQuestion();
        workForm.setQuestion(addWorkQuestion);
        workForm.setCount(1);
    }

    @Test
    public void getAddWorkWithCounterTwo_ReturnsAddWorkForm() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(mockStep));
        String viewName = sut.getAddWork(2, mockModel, COOKIE, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("form/common/boolean"));
    }

    @Test
    public void getAddWorkWithEditSingle_ReturnsViewName() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(mockStep));
        when(mockRequest.getParameter("edit")).thenReturn("SINGLE");
        String viewName = sut.getAddWork(2, mockModel, COOKIE, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("form/common/boolean"));
    }

    @Test
    public void getAddWorkWithEditSection_ReturnsViewName() {
        when(mockRoutingService.getStep(Mockito.anyString())).thenReturn(Optional.of(mockStep));
        when(mockRequest.getParameter("edit")).thenReturn("SECTION");
        String viewName = sut.getAddWork(2, mockModel, COOKIE, mockRequest);

        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("redirect:/form/summary"));
    }

    @Test
    public void getMaxJobsWarningReturnsMaxJobWarningForm() {
        String viewName = sut.getMaxJobsWarning();

        assertThat(viewName, is("form/previous-employment/max-previous-jobs"));
    }

    @Test
    public void givenCounterIsLessThan4_getNextPathReturnsEmployerQuestion() {
        workForm.setCount(2);
        addWorkQuestion.setChoice(true);

        String nextPath = sut.getNextPath(mockClaim, workForm, null);

        assertThat(nextPath, is("redirect:/form/previous-employment/employer-details/3/dates"));
    }

    @Test
    public void givenCounterIs4_getNextPathReturnsMaxJobsView() {
        workForm.setCount(4);
        addWorkQuestion.setChoice(true);

        String nextPath = sut.getNextPath(mockClaim, workForm, null);

        assertThat(nextPath, is("redirect:/form/previous-employment/max-jobs"));
    }

    @Test
    public void givenCounterIs4_getNextPathReturnsMaxJobsViewWithEditMode() {
        workForm.setCount(4);
        workForm.setEdit(EditMode.SINGLE);
        addWorkQuestion.setChoice(true);

        String nextPath = sut.getNextPath(mockClaim, workForm, null);

        assertThat(nextPath, is("redirect:/form/previous-employment/max-jobs?edit=SINGLE"));
    }

    @Test
    public void givenWorkQuestionReplyIsNo_getNextPathReturnsClaimBenefit() {
        addWorkQuestion.setChoice(false);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/claimed-benefit");

        StepInstance stepInstance = new StepInstance(false, false, new Step("ID2", null, null, Section.NONE));
        String nextPath = sut.getNextPath(mockClaim, workForm, stepInstance);

        assertThat(nextPath, is("redirect:/form/claimed-benefit"));
    }

    @Test
    public void createNewFormReturnsWorkFormWithNewWorkQuestionAndCountSetInController() {

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

}
