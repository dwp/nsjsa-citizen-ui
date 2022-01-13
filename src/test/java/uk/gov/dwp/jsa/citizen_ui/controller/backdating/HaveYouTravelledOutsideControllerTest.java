package uk.gov.dwp.jsa.citizen_ui.controller.backdating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.BooleanAndDateFieldQuestions;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.HaveYouTravelledOutsideForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.HaveYouTravelledOutsideQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.UnableToWorkDueToIllnessForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class HaveYouTravelledOutsideControllerTest {
    private static final String CLAIM_ID = "b31c9c20-e862-11ea-adc1-0242ac120002";
    public static final String IDENTIFIER = "form/backdating/have-you-travelled-outside-england-wales-scotland";
    public static final String NEXT_IDENTIFIER = "form/backdating/have-you-been-in-full-time-education";

    private HaveYouTravelledOutsideController sut;
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
    private HaveYouTravelledOutsideForm mockHaveYouTravelledOutsideForm;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HaveYouTravelledOutsideQuestion mockQuestion;
    @Mock
    private DateFormatterUtils mockDateFormatterUtils;
    @Mock
    private CookieLocaleResolver mockResolver;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(any())).thenReturn(Optional.of(step));
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockRoutingService.getLastGuard(any(), any())).thenReturn(Optional.of(mockStepInstance));
        sut = new HaveYouTravelledOutsideController(mockClaimRepository, mockRoutingService,mockResolver,
                mockDateFormatterUtils);
    }

    @Test
    public void getHaveYouTravelledOutside_successfulGetRequest_ReturnsExpectedView() {
        LocalDate now = LocalDate.now();
        LocalDate oneWeekAgo = LocalDate.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth()).minusWeeks(1);
        String formattedDate = String.format("%s %s $s", oneWeekAgo.getDayOfMonth(), oneWeekAgo.getMonth(), oneWeekAgo.getYear());
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(oneWeekAgo);
        when(mockDateFormatterUtils.formatDate(mockRequest, mockResolver, oneWeekAgo)).thenReturn(formattedDate);
        doNothing().when(mockRoutingService).arrivedOnPage(any(), any());
        when(mockRoutingService.getKeyValuePairForPageTitles()).thenReturn(new HashMap<>());

        String view = sut.getHaveYouTravelledOutside(mockModel, CLAIM_ID, mockRequest);

        assertEquals(view, IDENTIFIER);
    }

    @Test
    public void postHaveYouTravelledOutside_successfulPostRequest_redirectsExpectedView() {
        when(mockClaim.getId()).thenReturn(CLAIM_ID);
        when(mockHaveYouTravelledOutsideForm.getQuestion()).thenReturn(mockQuestion);
        doNothing().when(mockRoutingService).leavePage(any(), any());
        doNothing().when(mockClaim).save(any(), any(), any());
        when(mockRoutingService.getNext(any())).thenReturn(NEXT_IDENTIFIER);

        String view = sut.
                submitHaveYouTravelledOutside(CLAIM_ID,
                        mockHaveYouTravelledOutsideForm,
                        mockBindingResult, mockRequest, mockResponse, mockModel);

        verify(mockClaimRepository, times(1)).save(any());
        assertEquals(view, "redirect:" + NEXT_IDENTIFIER);
    }

    @Test
    public void getForm_ReturnsStringForm() {
        Form form = sut.getForm();
        assertEquals(form.getClass(), HaveYouTravelledOutsideForm.class);
    }

    @Test
    public void getTypedForm_ReturnsStringForm() {
        Form form = sut.getTypedForm();
        assertEquals(form.getClass(), HaveYouTravelledOutsideForm.class);
    }

}
