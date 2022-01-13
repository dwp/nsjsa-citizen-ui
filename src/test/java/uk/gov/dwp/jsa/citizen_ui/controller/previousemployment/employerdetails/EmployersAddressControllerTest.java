package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployersAddressControllerTest {
    public static final String IDENTIFIER = "form/previous-employment/employer-details/address";
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private EmployersAddressForm mockEmployersAddressForm;
    @Mock
    private EmployersAddressQuestion mockEmployersAddressQuestion;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    private EmployersAddressController sut;

    @Before
    public void setUp() {
        when(mockEmployersAddressForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new EmployersAddressController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        EmployersAddressForm actual = sut.createNewForm(mockClaim,1);
        assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockEmployersAddressQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        EmployersAddressForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewForm_throwsException() {
        sut.createNewForm(mockClaim);
    }

    @Test
    public void updateClaim() {
        when(mockEmployersAddressForm.getQuestion()).thenReturn(mockEmployersAddressQuestion);

        ArgumentCaptor<EmployersAddressQuestion> questionCaptor = ArgumentCaptor.forClass(EmployersAddressQuestion.class);
        sut.updateClaim(mockEmployersAddressForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), Is.is(mockEmployersAddressQuestion));
    }

    @Test
    public void getAddressReturnsEmployerNameForm() {
        String result = sut.getAddress(2, mockModel, "claimId", mockRequest);
        assertEquals("form/previous-employment/employer-details/address", result);
    }

    @Test
    public void submitAddressRedirectsToAddWorkUrl() {
        givenRoutingServiceReturnsAddWorkUrl();
        when(mockEmployersAddressForm.getQuestion()).thenReturn(mockEmployersAddressQuestion);

        String result = sut.submitAddress("claimId", mockEmployersAddressForm, mockBindingResult, mockResponse, mockModel);

        assertEquals("redirect:/form/previous-employment/1/add-work", result);
    }

    private void givenRoutingServiceReturnsAddWorkUrl() {
        when(mockRoutingService.getNext(any())).thenReturn("/form/previous-employment/%s/add-work");
    }

    @Test
    public void getNextReturnsExpectedUrl() {
        givenRoutingServiceReturnsAddWorkUrl();
        String nextPath = sut.getNextPath(mockClaim, mockEmployersAddressForm, null);

        assertThat(nextPath, is("redirect:/form/previous-employment/1/add-work"));
    }
}
