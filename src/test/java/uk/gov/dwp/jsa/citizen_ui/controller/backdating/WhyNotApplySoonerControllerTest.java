package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.WhyNotApplySoonerQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhyNotApplySoonerControllerTest<T extends StringQuestion> {

    private static final String CLAIM_ID = "b31c9c20-e862-11ea-adc1-0242ac120002";
    public static final String IDENTIFIER = "form/backdating/why-not-apply-sooner";

    private WhyNotApplySoonerController whyNotApplySoonerController;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private WebDataBinder binder;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Step step;
    @Mock
    private StepInstance mockStepInstance;
    @Mock
    private StringForm mockStringForm;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private T mockQuestion;

    @Before
    public void setUp() {
        mockStringForm = mock(StringForm.class);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(any())).thenReturn(Optional.of(step));
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockRoutingService.getLastGuard(any(), any())).thenReturn(Optional.of(mockStepInstance));
        whyNotApplySoonerController = new WhyNotApplySoonerController(mockClaimRepository, mockRoutingService);
    }

    @Test
    public void getWhyNotApplySoonerPage_successfulGetRequest_ReturnsExpectedView() {
        doNothing().when(mockRoutingService).arrivedOnPage(any(), any());
        when(mockRoutingService.getKeyValuePairForPageTitles()).thenReturn(new HashMap<>());

        String view = whyNotApplySoonerController.getWhyNotApplySoonerPage(mockModel, CLAIM_ID, mockRequest);

        assertEquals(view, "form/backdating/why-not-apply-sooner");
    }

    @Test
    public void postWhyDidYouNotApplySoonerPage_successfulPostRequest_redirectsExpectedView() {
        when(mockClaim.getId()).thenReturn(CLAIM_ID);
        when(mockStringForm.getQuestion()).thenReturn(mockQuestion);
        doNothing().when(mockRoutingService).leavePage(any(), any());
        doNothing().when(mockClaim).save(any(), any(), any());
        when(mockRoutingService.getNext(any())).thenReturn("form/nino");

        String view = whyNotApplySoonerController.
                postWhyDidYouNotApplySoonerPage(CLAIM_ID, mockStringForm,mockBindingResult, mockResponse, mockModel);

        verify(mockClaimRepository, times(1)).save(any());
        assertEquals(view, "redirect:form/nino");
    }

    @Test
    public void getForm_ReturnsStringForm() {
        Form form = whyNotApplySoonerController.getForm();
        assertEquals(form.getClass(), StringForm.class);
    }

    @Test
    public void getTypedForm_ReturnsStringForm() {
        Form form = whyNotApplySoonerController.getTypedForm();
        assertEquals(form.getClass(), StringForm.class);
    }

    @Test
    public void loadForm() {
        // TODO
    }

    @Test
    public void dataBinder_shouldBindDefaultEditor() {
        whyNotApplySoonerController.dataBinding(binder);
        verify(binder).registerCustomEditor(eq(StringQuestion.class), any(WhyNotApplySoonerQuestionEditor.class));
    }
}
