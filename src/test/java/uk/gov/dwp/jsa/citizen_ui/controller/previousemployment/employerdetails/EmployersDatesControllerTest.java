package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployersDatesControllerTest {
    private static final EmploymentDurationQuestion EMPLOYMENT_DATE_RANGE_QUESTION = new EmploymentDurationQuestion(new DateQuestion(), new DateQuestion());

    public static final String IDENTIFIER = "form/previous-employment/employer-details/dates";
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private DateRangeForm mockForm;
    @Mock
    private EmploymentDurationQuestion mockDateRange;
    @Mock
    private Model mockModel;
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

    EmployersDatesController sut;

    @Before
    public void setUp() {
        when(mockForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        sut = new EmployersDatesController(mockClaimRepository, routingService);
    }

    @Test
    public void exampleDates() {
        sut.exampleDates(mockModel);

        verify(mockModel)
            .addAttribute(eq("exampleStartDate"),
            eq(now().minusMonths(1).format(ofPattern(Constants.EXAMPLE_DATE_FORMAT_EXCLUDING_ZERO_PREFIXES))));
        verify(mockModel)
            .addAttribute(eq("exampleEndDate"),
            eq(now().format(ofPattern(Constants.EXAMPLE_DATE_FORMAT_EXCLUDING_ZERO_PREFIXES))));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewForm_throwsException() {
        sut.createNewForm(mockClaim);
    }


    @Test
    public void getName() {
        String result = sut.getName(1, mockModel, "claimId", mockRequest);
        assertEquals("form/common/date-range", result);
    }

    @Test
    public void submitForm() {
        givenRoutingServiceReturnWhyEndUrl();
        when(mockForm.getQuestion()).thenReturn(EMPLOYMENT_DATE_RANGE_QUESTION);
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/employer-details/why-end");
        String result = sut.submitForm("claimId", mockForm, mockBindingResult, mockResponse, mockModel);
        assertEquals("redirect:/form/previous-employment/employer-details/why-end", result);
    }

    private void givenRoutingServiceReturnWhyEndUrl() {
        when(routingService.getNext(any())).thenReturn("/form/previous-employment/employer-details/why-end");
    }
}
