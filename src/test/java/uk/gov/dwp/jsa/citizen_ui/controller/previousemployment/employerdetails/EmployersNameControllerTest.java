package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringShortQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployersNameControllerTest {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/name";
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private StringForm mockEmployersNameForm;
    @Mock
    private NameStringShortQuestion mockEmployersNameQuestion;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private RoutingService routingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    private EmployersNameController sut;

    @Before
    public void setUp() {
        when(mockEmployersNameForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        sut = new EmployersNameController(mockClaimRepository, routingService);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewForm_throwsException() {
        sut.createNewForm(mockClaim);
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockEmployersNameQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        StringForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        StringForm actual = sut.createNewForm(mockClaim,1);
        MatcherAssert.assertThat(actual.getQuestion(), notNullValue());

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).get(argumentCaptor.capture());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockEmployersNameForm.getQuestion()).thenReturn(mockEmployersNameQuestion);

        ArgumentCaptor<NameStringShortQuestion> questionCaptor = ArgumentCaptor.forClass(NameStringShortQuestion.class);
        sut.updateClaim(mockEmployersNameForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockEmployersNameQuestion));
    }

    @Test
    public void getNameReturnsEmployerNameForm() {
        String result = sut.getName(2, mockModel, "claimId", mockRequest);
        assertEquals("form/common/text", result);
    }

    @Test
    public void submitNameRedirectsToAddWorkUrl() {
        givenRoutingServiceReturnsAddWorkUrl();
        when(mockEmployersNameForm.getCount()).thenReturn(1);
        when(mockEmployersNameForm.getQuestion()).thenReturn(mockEmployersNameQuestion);

        String result = sut.submitName("claimId", mockEmployersNameForm, mockBindingResult, mockResponse, mockModel);
        assertEquals("redirect:/form/previous-employment/1/add-work", result);
    }

    private void givenRoutingServiceReturnsAddWorkUrl() {
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/%s/add-work");
    }

    @Test
    public void getNextReturnsExpectedUrl() {
        givenRoutingServiceReturnsAddWorkUrl();
        String nextPath = sut.getNextPath(mockClaim, mockEmployersNameForm, null);

        assertThat(nextPath, is("redirect:/form/previous-employment/1/add-work"));
    }
}
