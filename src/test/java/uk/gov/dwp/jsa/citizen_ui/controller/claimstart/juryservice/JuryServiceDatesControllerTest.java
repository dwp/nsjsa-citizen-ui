package uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.JuryService;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.claimstart.JuryServiceDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController.FOR_EXAMPLE_END_DATE_TEXT;
import static uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController.FOR_EXAMPLE_START_DATE_TEXT;

@RunWith(MockitoJUnitRunner.class)
public class JuryServiceDatesControllerTest {

    private static final DateRangeQuestion DATE_RANGE_QUESTION = new JuryServiceDurationQuestion(new DateQuestion(), new DateQuestion());
    public static final String IDENTIFIER = "form/claim-start/jury-service/start-date";
    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock
    private DateRangeForm mockForm;

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

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private JuryServiceDatesController sut;

    @Before
    public void setUp() {
        sut = new JuryServiceDatesController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void exampleDates() {
        sut.exampleDates(mockModel);

        verify(mockModel).addAttribute(eq("exampleStartDate"), eq(FOR_EXAMPLE_START_DATE_TEXT));
        verify(mockModel).addAttribute(eq("exampleEndDate"), eq(FOR_EXAMPLE_END_DATE_TEXT));
    }

    @Test
    public void getJuryForm() {
        String viewName = sut.getJuryForm(mockModel, claimId, mockRequest);
        assertEquals("form/common/date-range", viewName);
    }

    @Test
    public void submitJuryForm() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/previous-employment");

        when(mockForm.getQuestion()).thenReturn(DATE_RANGE_QUESTION);
        String result = sut.submitJuryForm(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("redirect:/form/previous-employment", result);
    }

    @Test
    public void submitJuryFormHasErrors_returnJuryService() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitJuryForm(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("form/common/date-range", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {

        final Circumstances circumstances = new Circumstances();
        final JuryService juryservice = new JuryService();
        final LocalDate endDate = LocalDate.now();
        juryservice.setEndDate(endDate);
        final LocalDate startDate = LocalDate.now();
        juryservice.setStartDate(startDate);
        circumstances.setJuryService(juryservice);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final DateRangeForm form = new DateRangeForm();
        form.setQuestion(new JuryServiceDurationQuestion(new DateQuestion(), new DateQuestion()));
        sut.loadForm(claimDB, form);

        assertEquals("Should match", new DateQuestion(startDate), form.getQuestion().getStartDate());
        assertEquals("Should match", new DateQuestion(endDate), form.getQuestion().getEndDate());
    }

    @Test
    public void testLoadDataShouldNotAssignWhenDataMissing() {
        final Circumstances circumstances = new Circumstances();
        final JuryService juryservice = new JuryService();
        final LocalDate startDate = LocalDate.now();
        juryservice.setStartDate(startDate);
        circumstances.setJuryService(juryservice);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final DateRangeForm form = new DateRangeForm();
        form.setQuestion(new JuryServiceDurationQuestion(new DateQuestion(), new DateQuestion()));
        sut.loadForm(claimDB, form);

        assertEquals("Should match", new DateQuestion(startDate), form.getQuestion().getStartDate());
        assertNull("Should be null", form.getQuestion().getEndDate().getDay());
        assertNull("Should be null", form.getQuestion().getEndDate().getMonth());
        assertNull("Should be null", form.getQuestion().getEndDate().getYear());
    }

    @Test
    public void testLoadDataShouldNotAssignWhenNoData() {
        final Circumstances circumstances = new Circumstances();
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final DateRangeForm form = new DateRangeForm();
        form.setQuestion(new JuryServiceDurationQuestion(new DateQuestion(), new DateQuestion()));
        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getStartDate().getDay());
        assertNull("Should be null", form.getQuestion().getStartDate().getMonth());
        assertNull("Should be null", form.getQuestion().getStartDate().getYear());
        assertNull("Should be null", form.getQuestion().getEndDate().getDay());
        assertNull("Should be null", form.getQuestion().getEndDate().getMonth());
        assertNull("Should be null", form.getQuestion().getEndDate().getYear());
    }
}
