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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressQuestion;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviderAddressControllerTest {
    public static final String IDENTIFIER = ProviderAddressController.IDENTIFIER;
    private ProviderAddressController sut;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProvidersAddressForm mockForm;

    @Mock
    private ProvidersAddressQuestion mockQuestion;

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
        sut = new ProviderAddressController(mockClaimRepository, mockRoutingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void getProviderAddress_returnsView() {
        String result = sut.getProviderAddress(1, mockModel, claimId, mockRequest);
        assertEquals("form/pensions/details/address", result);
    }

    @Test
    public void submitProviderAddress_returnsNextPathFromRoutingService() {
        when(mockForm.getCount()).thenReturn(1);

        String expected = "path";
        when(mockRoutingService.getNext(any())).thenReturn(expected);
        String result = sut.submitProviderAddress(claimId, mockForm, mockBindingResult, mockResponse, mockModel);
        assertThat(result, is("redirect:" + expected));
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        ProvidersAddressForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        ProvidersAddressForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void setFormAttrsSetsTranslationKeyAsExpected() {
        sut.setFormAttrs(mockForm, claimId);

        verify(mockForm).setTranslationKey("pensions.current.providers.address.");
    }
}
