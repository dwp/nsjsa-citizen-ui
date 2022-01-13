package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class HasCurrentWorkControllerTest {

    public static final String IDENTIFIER="form/current-work/are-you-working";
    private HasCurrentWorkController sut;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GuardForm mockForm;

    @Mock
    private BooleanQuestion mockQuestion;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Mock
    private Step step;

    @Mock
    private StepInstance stepInstance;

    private String anonymousClaimId = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        sut = new HasCurrentWorkController(mockClaimRepository, mockRoutingService, mockCookieLocaleResolver);
    }

    @Test
    public void getView_returnsView() {
        String result = sut.getAreYouWorking(mockModel, anonymousClaimId, mockRequest);
        assertEquals(BOOLEAN_VIEW_NAME, result);
    }

    @Test
    public void submit_returnsNextPathFromRoutingService() {
        String expected = "path";
        when(mockRoutingService.getNext(any())).thenReturn(expected);
        String result = sut.postAreYouWorking(
                mockForm, mockBindingResult, anonymousClaimId, mockRequest, mockResponse, mockModel);
        assertThat(result, is ("redirect:" + expected));
    }

    @Test
    public void SubmitFormInWelsh_withErrors_modelContainsCorrectWelshYesText() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/current-work/are-you-working");
        String result = sut.postAreYouWorking(
                mockForm, mockBindingResult, anonymousClaimId, mockRequest, mockResponse, mockModel);

        assertEquals("form/common/boolean", result);
        verify(mockModel, times(1)).addAttribute("alternativeWelshTextYES",
                "common.question.yesno.choice.true.alternative.ydw");
    }

    @Test
    public void getFormInWelsh_ReturnsFormWithCorrectWelshYesText() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/current-work/are-you-working");

        String result = sut.getAreYouWorking(mockModel, anonymousClaimId, mockRequest);

        assertEquals(result, "form/common/boolean");
        verify(mockModel, times(1)).addAttribute(
                "alternativeWelshTextYES", "common.question.yesno.choice.true.alternative.ydw");
    }

    @Test
    public void givenPreviousAnswer_createNewForm_returnsFormWithPreviousAnswer() {

        Optional<Question> optionalQuestion = Optional.of(mockQuestion);

        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        GuardForm actual = sut.createNewForm(mockClaim);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void givenNoPreviousAnswer_createNewForm_returnsEmptyForm() {

        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        GuardForm actual = sut.createNewForm(mockClaim);
        assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {

        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }


    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);

        final GuardForm<GuardQuestion> form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenNoMoreJobs() {
        ClaimDB claimDB = new ClaimDB();

        final GuardForm<GuardQuestion> form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }

}
