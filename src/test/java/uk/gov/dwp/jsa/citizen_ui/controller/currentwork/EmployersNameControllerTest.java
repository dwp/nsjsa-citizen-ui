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
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringShortQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm.TEXT_INPUT_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class EmployersNameControllerTest {

    public static final String IDENTIFIER = "form/current-work/details/name";
    private EmployersNameController sut;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StringForm mockForm;

    @Mock
    private NameStringShortQuestion mockQuestion;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Step step;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private StepInstance stepInstance;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        sut = new EmployersNameController(mockClaimRepository, mockRoutingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void getEmployersName_returnsView() {
        String result = sut.getEmployersName(1, mockModel, claimId, mockRequest);
        assertEquals(TEXT_INPUT_VIEW_NAME, result);
    }

    @Test
    public void submitEmployersName_returnsNextPathFromRoutingService() {
        when(mockForm.getCount()).thenReturn(1);

        String expected = "path";
        when(mockRoutingService.getNext(any())).thenReturn(expected);
        String result = sut.submitEmployersName(claimId, mockForm, mockBindingResult, mockResponse, mockModel);
        assertThat(result, is("redirect:" + expected));
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        StringForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        StringForm actual = sut.createNewForm(mockClaim, 1);
        assertThat(actual.getQuestion(), notNullValue());

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).get(argumentCaptor.capture());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<NameStringShortQuestion> questionCaptor = ArgumentCaptor.forClass(NameStringShortQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final String name = "name";
        currentWork.setEmployerName(name);

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", name, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        CurrentWork currentWork = addCurrentWork(claimDB);
        final String name = "name";
        currentWork.setEmployerName(name);

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", name, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final String name = "name";
        currentWork.setEmployerName(name);

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }
}
