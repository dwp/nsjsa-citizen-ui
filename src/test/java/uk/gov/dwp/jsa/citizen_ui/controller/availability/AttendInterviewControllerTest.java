package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.AvailableForInterview;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.services.InterviewAvailability;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttendInterviewControllerTest {

    public static final String IDENTIFIER = "form/availability/availability";
    private AttendInterviewController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AttendInterviewForm mockForm;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private RoutingService routingService;
    @Mock
    private InterviewAvailability interviewAvailability;
    @Mock
    private CookieLocaleResolver cookieLocaleResolver;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private AttendInterviewQuestion mockedInterviewQuestion;

    @Mock
    private Step step;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";
    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void createSut() {
        sut = new AttendInterviewController(mockClaimRepository, routingService, interviewAvailability, cookieLocaleResolver);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void getAttendInterviewForm() {
        String viewName = sut.getAttendInterview(mockModel, claimId, mockRequest);
        assertEquals("form/availability/availability", viewName);
    }

    @Test
    public void SubmitAttendInterviewForm_UpdatesTheClaim() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(routingService.getNext(any())).thenReturn("/form/some-page");
        when(routingService.getNext(any())).thenReturn("/form/some-page");
        when(mockForm.getQuestion()).thenReturn(mockedInterviewQuestion);


        String result = sut.submitAttendInterview(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        assertEquals("redirect:/form/some-page", result);

        ArgumentCaptor<AttendInterviewQuestion> questionCaptor = ArgumentCaptor.forClass(AttendInterviewQuestion.class);


        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockedInterviewQuestion));

        verify(mockClaimRepository, atLeastOnce()).save(mockClaim);
    }

    @Test
    public void SubmitAttendInterviewForm_WithErrors_ReturnsAttendInterviewForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitAttendInterview(claimId, mockForm, mockBindingResult, mockRequest, mockResponse, mockModel);

        assertEquals("form/availability/availability", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        final Day day = new Day();
        AvailableForInterview availability = new AvailableForInterview();
        availability.setDaysNotAvailable(Arrays.asList(day));
        final Circumstances circumstances = new Circumstances();
        circumstances.setAvailableForInterview(availability);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final AttendInterviewForm form = new AttendInterviewForm();
        sut.loadForm(claimDB, form);


        assertEquals("Should be equal", form.getQuestion().getDaysNotToAttend().get(0), new DayTransformer(new ReasonTransformer()).transform(availability.getDaysNotAvailable().get(0)));
    }

    @Test
    public void testLoadDataShouldAssignEmptyListWhenNoAvailability() {
        AvailableForInterview availability = new AvailableForInterview();
        final Circumstances circumstances = new Circumstances();
        circumstances.setAvailableForInterview(availability);
        ClaimDB claimDB = new ClaimDB();
        claimDB.setCircumstances(circumstances);

        final AttendInterviewForm form = new AttendInterviewForm();
        sut.loadForm(claimDB, form);

        assertTrue("Should be empty list", form.getAttendInterviewQuestion().getDaysNotToAttend().isEmpty());
    }
}
