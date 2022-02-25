package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class HasCurrentPensionControllerTest {

    private HasCurrentPensionController sut;

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
    private Step step;
    @Mock
    private PensionsService pensionsService;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    @Mock
    private PensionsService mockPensionsService;
    @Mock
    private StepInstance mockStepInstance;

    private GuardForm form = new GuardForm();
    private BooleanQuestion booleanQuestion = new BooleanQuestion();


    private String anonymousClaimId = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(HasCurrentPensionController.IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        sut = new HasCurrentPensionController(mockClaimRepository, mockRoutingService, pensionsService);
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void givenCounterIs1AndCannotAddPension_getNextPathReturnsCorrectPath() {
        booleanQuestion = new BooleanQuestion();
        form.setQuestion(booleanQuestion);
        booleanQuestion.setChoice(true);
        form.setCount(null);
        String nextPath = sut.getNextPath(mockClaim, form, mockStepInstance);
        Assert.assertThat(nextPath, CoreMatchers.is("redirect:/form/pensions/current/details/1/provider-name"));
    }

    @Test
    public void getView_returnsView() {
        String result = sut.getDoYouHavePensions(mockModel, anonymousClaimId, mockRequest);
        assertEquals(BOOLEAN_VIEW_NAME, result);
    }

    @Test
    public void submit_returnsNextPathFromRoutingService() {
        String expected = "path";
        when(mockRoutingService.getNext(any())).thenReturn(expected);
        String result = sut.postDoYouHavePensions(mockForm, mockBindingResult, anonymousClaimId, mockResponse,
                mockRequest, mockModel);
        assertThat(result, is ("redirect:" + expected));
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
}
