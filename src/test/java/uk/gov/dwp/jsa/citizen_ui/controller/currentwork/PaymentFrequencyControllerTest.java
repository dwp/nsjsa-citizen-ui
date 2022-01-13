package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

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
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;

@RunWith(MockitoJUnitRunner.class)
public class PaymentFrequencyControllerTest {

    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";
    private static final String VIEW_NAME = "form/current-work/how-often-paid";
    private static final Integer COUNT = 1;
    private static final String NEXT_IDENTIFIER = "form/current-work/payroll-reference-number";
    private static final String BACK_REF = String.format("/form/current-work/details/%d/is-work-paid", COUNT);
    private static final String EXPECTED_NEXT_IDENTIFIER = "redirect:" + NEXT_IDENTIFIER;
    public static final String IDENTIFIER = "form/current-work/details/how-often-paid";

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

    private PaymentFrequencyController sut;

    @Before
    public void beforeEachTest() {
        sut = new PaymentFrequencyController(mockClaimRepository, mockRoutingService);
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

        ArgumentCaptor<PaymentFrequencyQuestion> questionCaptor = ArgumentCaptor.forClass(PaymentFrequencyQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockQuestion));
    }


    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);

        final PaymentFrequency monthly = PaymentFrequency.MONTHLY;
        currentWork.setPaymentFrequency(monthly.name());
        final BigDecimal netPay = new BigDecimal(100).setScale(2, RoundingMode.HALF_UP);
        currentWork.setNetPay(netPay);

        final PaymentFrequencyForm form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", monthly, form.getQuestion().getPaymentFrequency());
        assertTrue("Should be present", form.getQuestion().getSelectedPaymentAmounts().isPresent());
        assertEquals("Should match", netPay, form.getQuestion().getSelectedPaymentAmounts().get().getNet());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        CurrentWork currentWork = addCurrentWork(claimDB);

        final PaymentFrequency monthly = PaymentFrequency.MONTHLY;
        currentWork.setPaymentFrequency(monthly.name());
        final BigDecimal netPay = new BigDecimal(100).setScale(2, RoundingMode.HALF_UP);
        currentWork.setNetPay(netPay);

        final PaymentFrequencyForm form = sut.getForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", monthly, form.getQuestion().getPaymentFrequency());
        assertTrue("Should be present", form.getQuestion().getSelectedPaymentAmounts().isPresent());
        assertEquals("Should match", netPay, form.getQuestion().getSelectedPaymentAmounts().get().getNet());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final PaymentFrequencyForm form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getPaymentFrequency());
        assertFalse("Should not be present", form.getQuestion().getSelectedPaymentAmounts().isPresent());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final PaymentFrequency monthly = PaymentFrequency.MONTHLY;
        currentWork.setPaymentFrequency(monthly.name());
        final BigDecimal netPay = new BigDecimal(100);
        currentWork.setNetPay(netPay);

        final PaymentFrequencyForm form = sut.getForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getPaymentFrequency());
        assertFalse("Should not be present", form.getQuestion().getSelectedPaymentAmounts().isPresent());
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
        sut = new PaymentFrequencyController(mockClaimRepository, mockRoutingService);
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
