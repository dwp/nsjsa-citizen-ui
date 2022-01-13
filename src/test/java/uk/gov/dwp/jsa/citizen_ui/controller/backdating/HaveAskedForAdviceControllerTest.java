package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.AskedForAdviceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.AskedForAdviceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HaveAskedForAdviceControllerTest {
    private static final String SUT_ENDPOINT  = "form/backdating/have-you-asked-for-advice";
    private static final String EXPECTED_VIEW = "form/backdating/have-you-asked-for-advice";

    private HaveYouAskedForAdviceController haveYouAskedForAdviceController;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private Claim mockClaim;

    @Mock
    private AskedForAdviceForm askedForAdviceForm;

    @Mock
    private AskedForAdviceQuestion askedForAdviceQuestion;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Mock
    private Step step;

    @Mock
    private StepInstance mockStepInstance;

    @Mock
    private DateFormatterUtils mockDateFormatterUtils;

    private String claimID = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn("6 June 2020");
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(LocalDate.now());

        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockRoutingService.getLastGuard(any(), any())).thenReturn(Optional.of(mockStepInstance));
        haveYouAskedForAdviceController = new HaveYouAskedForAdviceController(mockClaimRepository, mockRoutingService,
                mockCookieLocaleResolver, mockDateFormatterUtils);
    }

    @Test
    public void createNewForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.of(askedForAdviceQuestion));
        AskedForAdviceForm form = haveYouAskedForAdviceController.createNewForm(mockClaim);
        assertNotNull(form);
        assertEquals(askedForAdviceQuestion, form.getQuestion());
    }

    @Test
    public void getHaveYouAskedForAdviceForm() {
        String result = haveYouAskedForAdviceController.getAskedForAdviceView(mockModel, claimID, mockRequest);
        assertEquals(EXPECTED_VIEW, result);
    }

    @Test
    public void getHaveYouAskedForAdvice_returnsCorrectView() {
        doNothing().when(mockRoutingService).arrivedOnPage(any(), any());
        when(mockRoutingService.getKeyValuePairForPageTitles()).thenReturn(new HashMap<>());

        String result = haveYouAskedForAdviceController.getAskedForAdviceView(mockModel, claimID, mockRequest);
        assertEquals(EXPECTED_VIEW, result);
    }

    @Test
    public void submitAskedForAdviceRedirectsToNationalInsurance() {
        when(mockRoutingService.getNext(any())).thenReturn("/form/nino");
        when(askedForAdviceForm.getQuestion()).thenReturn(askedForAdviceQuestion);

        String result = haveYouAskedForAdviceController.submitAskedForAdviceForm(
                claimID,
                askedForAdviceForm,
                mockBindingResult,
                mockRequest,
                mockResponse,
                mockModel
        );
        assertEquals("redirect:/form/nino", result);
    }
}
