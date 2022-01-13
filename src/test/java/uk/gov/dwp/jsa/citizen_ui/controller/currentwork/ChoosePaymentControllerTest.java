package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

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
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.ChoosePaymentController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;

@RunWith(MockitoJUnitRunner.class)
public class ChoosePaymentControllerTest {
    public static final String IDENTIFIER = "form/current-work/details/choose-payment";
    @Mock
    private Model mockModel;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BooleanForm mockForm;
    @Mock
    BooleanQuestion mockQuestion;
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
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private ChoosePaymentController sut;

    @Before
    public void setUp() {
        when(mockForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new ChoosePaymentController(mockClaimRepository, routingService);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);

    }

    @Test
    public void createNewForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.of(mockQuestion));

        BooleanForm actual = sut.createNewForm(mockClaim, 1);
        assertThat(actual.getQuestion(), is(mockQuestion));

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).get(argumentCaptor.capture());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewForm_throwsException() {
        sut.createNewForm(mockClaim);
    }

    @Test
    public void updateClaim() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void GetChoosePaymentForm_ReturnsChoosePaymentForm() {
        String result = sut.getView(2, mockModel, "claimId", mockRequest);
        assertEquals("form/common/boolean", result);
    }

    @Test
    public void SubmitChoosePaymentForm_WithTrue_ReturnsNextForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockRequest, mockModel);
        assertEquals("redirect:/the/next/page", result);
    }

    @Test
    public void SubmitChoosePaymentForm_WithFalse_ReturnsNextForm() {
        when(mockForm.getQuestion().getChoice()).thenReturn(true);
        when(routingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockRequest, mockModel);
        assertEquals("redirect:/the/next/page", result);
    }

    @Test
    public void SubmitChoosePaymentForm_WithErrors_ReturnsChoosePaymentForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitForm(claimId, mockForm, mockBindingResult, mockResponse, mockRequest, mockModel);
        assertEquals("form/common/boolean", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setCanChooseIfPaid(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenFalse() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setCanChooseIfPaid(false);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork another = addCurrentWork(claimDB);
        another.setCanChooseIfPaid(false);
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setCanChooseIfPaid(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork another = addCurrentWork(claimDB);
        another.setCanChooseIfPaid(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getChoice());
    }
}
