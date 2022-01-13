package uk.gov.dwp.jsa.citizen_ui.controller.claimstart;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultStartDateControllerTest {

    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";
    public static final String IDENTIFIER = "form/default-claim-start";
    private static final String VIEW_VARIABLE = "defaultDate";
    private static final String CY_LOCALE = "cy";
    private static final Map<Integer, String> ENGLISH_MONTHS = new HashMap<>();
    private static final Map<Integer, String> WELSH_MONTHS = new HashMap<>();

    static {
        ENGLISH_MONTHS.put(1, "January");
        ENGLISH_MONTHS.put(2, "February");
        ENGLISH_MONTHS.put(3, "March");
        ENGLISH_MONTHS.put(4, "April");
        ENGLISH_MONTHS.put(5, "May");
        ENGLISH_MONTHS.put(6, "June");
        ENGLISH_MONTHS.put(7, "July");
        ENGLISH_MONTHS.put(8, "August");
        ENGLISH_MONTHS.put(9, "September");
        ENGLISH_MONTHS.put(10, "October");
        ENGLISH_MONTHS.put(11, "November");
        ENGLISH_MONTHS.put(12, "December");
    }

    static {
        WELSH_MONTHS.put(1, "Ionawr");
        WELSH_MONTHS.put(2, "Chwefror");
        WELSH_MONTHS.put(3, "Mawrth");
        WELSH_MONTHS.put(4, "Ebrill");
        WELSH_MONTHS.put(5, "Mai");
        WELSH_MONTHS.put(6, "Mehefin");
        WELSH_MONTHS.put(7, "Gorffennaf");
        WELSH_MONTHS.put(8, "Awst");
        WELSH_MONTHS.put(9, "Medi");
        WELSH_MONTHS.put(10, "Hydref");
        WELSH_MONTHS.put(11, "Tachwedd");
        WELSH_MONTHS.put(12, "Rhagfyr");
    }

    private DefaultStartDateController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private GuardForm<GuardQuestion> mockPartForm;
    @Mock
    private GuardQuestion mockQuestion;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private ClaimStartDateController mockClaimStartDateController;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    @Mock
    private DateFormatterUtils mockDateFormatterUtils;

    @Before
    public void setUp() {
        Claim claim = new Claim();
        claim.setClaimStartDateQuestion(new ClaimStartDateQuestion());
        when(mockClaimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void getClaimStart_returnsCorrectView() {

        sut = createSut();
        String expected = "form/default-claim-start";
        String actual = sut.claimStartDate(mockModel, CLAIM_ID, mockRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void givenYesSelected_ThenRedirectedToNINOForm() {
        String expected = "redirect:/form/nino";
        when(mockPartForm.getQuestion()).thenReturn(mockQuestion);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRoutingService.getNext(any())).thenReturn("/form/nino");
        sut = createSut();

        String actual = sut.claimStartDate(mockPartForm, mockBindingResult, CLAIM_ID, mockResponse,
                mockModel);

        assertEquals(expected, actual);
    }

    @Test
    public void givenNoSelected_ThenRedirectedToClaimStartDateForm() {
        String expected = "redirect:/form/claim-start";
        when(mockPartForm.getQuestion()).thenReturn(mockQuestion);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRoutingService.getNext(any())).thenReturn("/form/claim-start");
        sut = createSut();

        String actual = sut.claimStartDate(mockPartForm, mockBindingResult, CLAIM_ID, mockResponse,
                mockModel);

        assertEquals(expected, actual);
    }

    @Test
    public void defaultDate_inEnglish_setsViewModelWithCorrectlyFormattedDate() {
        final int day = 6;
        final int month = 7;
        final int year = LocalDate.now().getYear();
        final String monthAlpha = ENGLISH_MONTHS.get(7);
        String expected = String.format("%s %s %s", day, monthAlpha, year);
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(LocalDate.of(year, month, day));
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);

        sut = createSut();
        sut.defaultDate(mockModel, mockRequest);

        verify(mockModel, times(1)).addAttribute(VIEW_VARIABLE, expected);
    }

    @Test
    public void defaultDate_inWelsh_setsViewModelWithCorrectlyFormattedDate() {
        final int day = 6;
        final int month = 7;
        final int year = LocalDate.now().getYear();
        final String monthAlpha = WELSH_MONTHS.get(7);
        String expected = String.format("%s %s %s", day, monthAlpha, year);
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(LocalDate.of(year, month, day));
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);

        sut = createSut();
        sut.defaultDate(mockModel, mockRequest);

        verify(mockModel, times(1)).addAttribute(VIEW_VARIABLE, expected);
    }

    private DefaultStartDateController createSut() {
        return new DefaultStartDateController(
                mockClaimRepository, mockRoutingService, mockClaimStartDateController, mockCookieLocaleResolver, mockDateFormatterUtils);
    }

}
