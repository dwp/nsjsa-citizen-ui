package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
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
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode.SINGLE;

@RunWith(MockitoJUnitRunner.class)
public class HasAnotherCurrentPensionControllerTest {

    public static final String IDENTIFIER = HasAnotherCurrentPensionController.IDENTIFIER;
    private HasAnotherCurrentPensionController sut;

    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    GuardForm mockForm;
    @Mock
    LoopEndBooleanQuestion mockQuestion;
    @Mock
    private PensionsService mockPensionsService;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    @Mock
    private StepInstance mockStepInstance;
    @Mock
    private Step step;

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
        sut = new HasAnotherCurrentPensionController(mockClaimRepository, mockRoutingService, mockPensionsService,
                mockCookieLocaleResolver);
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
    public void givenCounterIs1AndCannotAddPension_getNextPathReturnsCorrectPath() {
        form.setCount(1);
        booleanQuestion.setChoice(true);
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(true);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/pensions/current/details/2/provider-name"));
    }

    @Test
    public void givenCounterIs8AndCannotAddPension_getNextPathReturnsCorrectPath() {
        form.setCount(MAX_PENSIONS_ALLOWED-1);
        booleanQuestion.setChoice(true);
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(true);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/pensions/current/details/9/provider-name"));
    }


    @Test
    public void givenCounterIsLessThan9_getNextPathReturnsCorrectPath() {
        form.setCount(2);
        booleanQuestion.setChoice(true);
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(true);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/pensions/current/details/3/provider-name"));
    }

    @Test
    public void givenCounterIs9_getNextPathReturnsMaxJobsView() {
        form.setCount(9);
        booleanQuestion.setChoice(true);

        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(false);

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/pensions/max-current-pensions?backUrl=/form/pensions/current/9/has-another-pension"));
    }

    @Test
    public void givenQuestionReplyIsNo_getNextPathReturnsHasPreviousWork() {
        booleanQuestion.setChoice(false);
        when(mockRoutingService.getNext(mockStepInstance)).thenReturn("/form/pension/current/has-current-pension");

        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);

        assertThat(nextPath, is("redirect:/form/pension/current/has-current-pension"));
    }

    @Test
    public void createNewFormReturnsQuestionWithCountSet() {
        GuardForm form = sut.createNewForm(mockClaim, 1);
        assertNotNull(form);
        assertThat(form.getQuestion(), is(new LoopEndBooleanQuestion()));
        assertThat(1, is(form.getCount()));
    }

    @Test
    public void givenCountIs9_updateClaimSetsHasMoreLimitTrue() {
        mockForm.setCount(9);

        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(false);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, mockStepInstance, Optional.empty());

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
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(true);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, mockStepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
    }

    @Test
    public void GivenEndOfLoopsIsReached_UpdateClaim_SetsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockQuestion.getChoice()).thenReturn(true);
        when(mockPensionsService.canAddPension(mockClaim)).thenReturn(false);

        ArgumentCaptor<LoopEndBooleanQuestion> questionCaptor =
                ArgumentCaptor.forClass(LoopEndBooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, mockStepInstance, Optional.empty());

        verify(mockClaim, atLeastOnce()).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void ensureWeCanDeleteACurrentPensionAndReturnToSummary() {
        String viewName = sut.delete(1, COOKIE);

        verify(mockClaimRepository, atLeastOnce()).save(any(Claim.class));
        assertThat(viewName, is("redirect:/form/summary"));
    }

    @Test
    public void getAddWorkWithEditSingle_ReturnsViewName() {
        when(mockRoutingService.getStep(anyString())).thenReturn(Optional.of(step));
        when(mockRequest.getParameter("edit")).thenReturn("SINGLE");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        String viewName = sut.getView(2, mockModel, COOKIE, mockRequest);
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("form/common/boolean"));
    }

    @Test
    public void getAddPensionWithEditSection_ReturnsViewName() {
        when(mockRoutingService.getStep(anyString())).thenReturn(Optional.of(step));
        when(mockRequest.getParameter("edit")).thenReturn("SECTION");
        String viewName = sut.getView(2, mockModel, COOKIE, mockRequest);
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        assertThat(viewName, is("redirect:/form/summary"));
    }

    @Test
    public void givenCounterIs9_getNextPathReturnsMaxJobsViewWithEditMode() {
        when(mockForm.getCount()).thenReturn(9);
        when(mockForm.getEdit()).thenReturn(SINGLE);
        when(mockForm.isGuardedCondition()).thenReturn(true);
        String nextPath = sut.getNextPath(mockClaim, mockForm, mockStepInstance);
        assertThat(nextPath, is("redirect:/form/pensions/max-current-pensions?edit=SINGLE&backUrl=/form/pensions" +
                "/current/9/has-another-pension"));
    }
}
