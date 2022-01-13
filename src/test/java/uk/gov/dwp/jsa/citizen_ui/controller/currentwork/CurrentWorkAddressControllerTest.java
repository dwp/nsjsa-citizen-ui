package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

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
import uk.gov.dwp.jsa.adaptors.dto.claim.Address;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.CurrentWorkAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.getAddress;

@RunWith(MockitoJUnitRunner.class)
public class CurrentWorkAddressControllerTest {
    public static final String IDENTIFIER = "form/current-work/details/address";
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

    private CurrentWorkAddressController sut;

    @Before
    public void setUp() {
        when(mockEmployersAddressForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new CurrentWorkAddressController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        EmployersAddressForm actual = sut.createNewForm(mockClaim, 1);
        assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockEmployersAddressQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        EmployersAddressForm actual = sut.createNewForm(mockClaim, 1);
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
        assertEquals("form/current-work/details/address", result);
    }

    @Test
    public void submitAddressRedirectsToAddWorkUrl() {
        givenRoutingServiceReturnsAddWorkUrl();
        when(mockEmployersAddressForm.getQuestion()).thenReturn(mockEmployersAddressQuestion);

        String result = sut.submitAddress(
                "claimId",
                mockEmployersAddressForm,
                mockBindingResult,
                mockResponse,
                mockModel
        );

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

    @Test
    public void getForm_returnAddressFor() {
        Form form = sut.getForm();

        assertNotNull(form);
        assertTrue(form instanceof EmployersAddressForm);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Address address = getAddress("Line1", "Line2", "postocde", "town",
                "country");
        currentWork.setEmployerAddress(address);

        final EmployersAddressForm form = new EmployersAddressForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", address.getFirstLine(), form.getQuestion().getAddressLine1());
        assertEquals("Should match", address.getSecondLine(), form.getQuestion().getAddressLine2());
        assertEquals("Should match", address.getPostCode(), form.getQuestion().getPostCode());
        assertEquals("Should match", address.getTown(), form.getQuestion().getTownOrCity());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Address address = getAddress("Line1", "Line2", "postocde", "town",
                "country");
        currentWork.setEmployerAddress(address);

        final EmployersAddressForm form = new EmployersAddressForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", address.getFirstLine(), form.getQuestion().getAddressLine1());
        assertEquals("Should match", address.getSecondLine(), form.getQuestion().getAddressLine2());
        assertEquals("Should match", address.getPostCode(), form.getQuestion().getPostCode());
        assertEquals("Should match", address.getTown(), form.getQuestion().getTownOrCity());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final EmployersAddressForm form = new EmployersAddressForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getAddressLine1());
        assertNull("Should be null", form.getQuestion().getAddressLine2());
        assertNull("Should be null", form.getQuestion().getPostCode());
        assertNull("Should be null", form.getQuestion().getTownOrCity());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Address address = getAddress("Line1", "Line2", "postocde", "town",
                "country");
        addCurrentWork(claimDB);
        currentWork.setEmployerAddress(address);

        final EmployersAddressForm form = new EmployersAddressForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getAddressLine1());
        assertNull("Should be null", form.getQuestion().getAddressLine2());
        assertNull("Should be null", form.getQuestion().getPostCode());
        assertNull("Should be null", form.getQuestion().getTownOrCity());
    }
}
