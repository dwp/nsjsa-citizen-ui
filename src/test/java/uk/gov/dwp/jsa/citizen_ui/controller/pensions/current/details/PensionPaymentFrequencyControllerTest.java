package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
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
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.testutils.PensionPaymentFrequencyTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PensionPaymentFrequencyControllerTest {

    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";
    private static final Integer COUNT = 1;
    private static final String BACK_REF = String.format("/form/current-work/details/%d/is-work-paid", COUNT);
    private static final String NEXT_IDENTIFIER = "/form/pensions/current/details/1/payment-increase";
    private static final String EXPECTED_NEXT_IDENTIFIER = "redirect:" + NEXT_IDENTIFIER;
    private static final String VIEW_NAME = "form/pensions/payment-frequency";
    private static final String IDENTIFIER = "form/pensions/current/details/payment-frequency";

    @Mock
    private PaymentFrequencyForm mockForm;
    @Mock
    private PaymentFrequencyQuestion mockQuestion;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockHttpResponse;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    private String viewName;
    private Form gotForm;

    private PensionPaymentFrequencyController sut;

    @Before
    public void beforeEachTest() {
        sut = new PensionPaymentFrequencyController(mockClaimRepository, mockRoutingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void getsView() {
        givenAController();
        whenIDoGet();
        thenTheViewIsReturned();
    }

    @Test
    public void postsForm() {
        givenAController();
        whenIDoPost();
        thenTheFormIsPosted();
    }

    @Test
    public void postFormSetsAttributes() {
        givenAController();
        whenIDoPost();
        thenTheFormHasAttributesSet();
    }

    @Test
    public void GivenInvalidCharacterSubmitted_updatesUIModelWithCorrectLocaleErrors() {
        givenAController();
        when(mockBindingResult.getAllErrors()).thenReturn(PensionPaymentFrequencyTestUtils
                        .generateListOfObjectErrorsContainingTypeMismatch(true));

        sut.postHowOftenPaid(mockForm, mockBindingResult, CLAIM_ID, mockHttpResponse, mockModel);
        PensionPaymentFrequencyTestUtils.verifyUIModel(
                        mockModel, "pensions.current.paymentfrequency.invalid.error", true);

    }

    @Test
    public void GivenValidSubmit_doesNotUpdateUIModelWithLocaleErrors() {
        givenAController();
        when(mockBindingResult.getAllErrors()).thenReturn(PensionPaymentFrequencyTestUtils
                        .generateListOfObjectErrorsContainingTypeMismatch(false));

        sut.postHowOftenPaid(mockForm, mockBindingResult, CLAIM_ID, mockHttpResponse, mockModel);
        PensionPaymentFrequencyTestUtils.verifyUIModel(
                        mockModel, "pensions.current.paymentfrequency.invalid.error", false);
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        PaymentFrequencyForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        PaymentFrequencyForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void updateClaim() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<PaymentFrequencyQuestion> questionCaptor =
                ArgumentCaptor.forClass(PaymentFrequencyQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockQuestion));
    }

    @Test
    public void getsForm() {
        givenAController();
        whenIGetForm();
        thenTheFormIsReturned();
    }

    private void givenAController() {
        when(mockForm.getCount()).thenReturn(COUNT);
        when(mockForm.getQuestion()).thenReturn(mockQuestion);
        when(mockRoutingService.getNext(any())).thenReturn(NEXT_IDENTIFIER);
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn(BACK_REF);
        when(mockClaimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(mockClaim));
        when(mockForm.getCount()).thenReturn(COUNT);
        sut = new PensionPaymentFrequencyController(mockClaimRepository, mockRoutingService);
    }

    private void whenIDoGet() {
        viewName = sut.getHowOftenPaid(COUNT, mockModel, CLAIM_ID, mockRequest);
    }

    private void whenIDoPost() {
        viewName = sut.postHowOftenPaid(mockForm, mockBindingResult, CLAIM_ID, mockHttpResponse, mockModel);
    }

    private void whenIGetForm() {
        gotForm = sut.getForm();
    }

    private void thenTheFormIsReturned() {
        assertThat(gotForm, is(notNullValue()));
    }

    private void thenTheFormHasAttributesSet() {
        verify(mockForm).setBackRef(BACK_REF);
    }

    private void thenTheFormIsPosted() {
        assertThat(viewName, is(EXPECTED_NEXT_IDENTIFIER));
    }

    private void thenTheViewIsReturned() {
        assertThat(viewName, is(VIEW_NAME));
    }
}
