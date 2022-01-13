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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FullTimeEducationControllerTest {

    private static final String SUT_ENDPOINT  = "/form/backdating/have-you-been-in-full-time-education";
    private static final String EXPECTED_VIEW = "form/backdating/have-you-been-in-full-time-education";

    private FullTimeEducationController fullTimeEducationController;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private Claim mockClaim;

    @Mock
    private BooleanForm mockBooleanForm;

    @Mock
    private BooleanQuestion mockQuestion;

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
        fullTimeEducationController = new FullTimeEducationController(mockClaimRepository, mockRoutingService,
                mockCookieLocaleResolver, mockDateFormatterUtils);
    }

    @Test
    public void getHaveBeenInFullTimeEducation_returnsCorrectView() {
        doNothing().when(mockRoutingService).arrivedOnPage(any(), any());
        when(mockRoutingService.getKeyValuePairForPageTitles()).thenReturn(new HashMap<>());

        String result = fullTimeEducationController.getHaveBeenInFullTimeEducation(mockModel, claimID, mockRequest);
        assertEquals(EXPECTED_VIEW, result);
    }

    @Test
    public void postHaveBeenInFullTimeEducation_returnsCorrectView() {
        String expected = "/form/backdating/have-you-asked-for-advice";
        when(mockClaim.getId()).thenReturn(claimID);
        when(mockBooleanForm.getQuestion()).thenReturn(mockQuestion);
        doNothing().when(mockRoutingService).leavePage(any(), any());
        doNothing().when(mockClaim).save(any(), any(), any());
        when(mockRoutingService.getNext(any())).thenReturn(expected);

        String result = fullTimeEducationController.postHaveBeenInFulltimeEducation(
                mockBooleanForm, mockBindingResult, claimID, mockRequest, mockResponse, mockModel);
        assertEquals(result, "redirect:" + expected);
    }

    @Test
    public void postHaveYouBeenInFullTimeEducationSinceInWelsh_withErrors_modelContainsCorrectWelshYesText() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn(SUT_ENDPOINT);

        String result = fullTimeEducationController.postHaveBeenInFulltimeEducation(
                mockBooleanForm, mockBindingResult, claimID, mockRequest, mockResponse, mockModel);

        assertEquals(EXPECTED_VIEW, result);
        verify(mockModel, times(1)).addAttribute("alternativeWelshTextYES",
                "common.question.yesno.choice.true.alternative.ydw");
        verify(mockModel, times(1)).addAttribute("alternativeWelshTextNO",
                "common.question.yesno.choice.false.alternative.na");
    }

    @Test
    public void getFormInWelsh_ReturnsFormWithCorrectWelshText() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn(SUT_ENDPOINT);

        String result = fullTimeEducationController.getHaveBeenInFullTimeEducation(mockModel, claimID, mockRequest);
        assertEquals(result, EXPECTED_VIEW);

        verify(mockModel, times(1)).addAttribute("alternativeWelshTextYES",
                "common.question.yesno.choice.true.alternative.ydw");
        verify(mockModel, times(1)).addAttribute("alternativeWelshTextNO",
                "common.question.yesno.choice.false.alternative.na");
    }

}
