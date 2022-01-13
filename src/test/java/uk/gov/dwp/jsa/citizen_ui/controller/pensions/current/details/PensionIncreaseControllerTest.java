package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PensionIncreaseControllerTest {
    public static final String IDENTIFIER = PensionIncreaseController.IDENTIFIER;
    private PensionIncreaseController sut;

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
    private GuardQuestion mockQuestion;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Step step;
    @Mock
    private HttpServletRequest mockRequest;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        sut = new PensionIncreaseController(mockClaimRepository, mockRoutingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        GuardForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        GuardForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void GetPensionIncreaseForm_ReturnsPensionIncreaseForm() {
        String result = sut.getView(2, mockModel, "claimId", mockRequest);
        assertEquals("form/common/boolean", result);
    }

    @Test
    public void SubmitPensionIncreaseForm_WithTrue_ReturnsNextForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(mockRoutingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockModel);
        assertEquals("redirect:/the/next/page", result);
    }

    @Test
    public void SubmitPensionIncreaseForm_WithFalse_ReturnsNextForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(mockRoutingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockModel);
        assertEquals("redirect:/the/next/page", result);
    }

    @Test
    public void SubmitPensionIncreaseForm_WithErrors_ReturnsPensionIncreaseForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockModel);
        assertEquals("form/common/boolean", result);
    }
}
