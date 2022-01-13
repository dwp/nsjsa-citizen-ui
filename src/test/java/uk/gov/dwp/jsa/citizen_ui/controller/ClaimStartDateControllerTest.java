package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.model.form.InvalidClaimDateErrorEnum.INVALID_AFTER_DATE;
import static uk.gov.dwp.jsa.citizen_ui.model.form.InvalidClaimDateErrorEnum.INVALID_BEFORE_DATE;
import static uk.gov.dwp.jsa.citizen_ui.model.form.InvalidClaimDateErrorEnum.INVALID_DATE;

@RunWith(MockitoJUnitRunner.class)
public class ClaimStartDateControllerTest {

    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";
    public static final String IDENTIFIER = "form/claim-start";
    private ClaimStartDateController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimStartDateForm mockPartForm;
    @Mock
    private ClaimStartDateQuestion mockQuestion;
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

    @Before
    public void setUp() {
        Claim claim = new Claim();
        mockQuestion = new ClaimStartDateQuestion(LocalDate.now());
        mockPartForm.setClaimStartDateQuestion(mockQuestion);
        mockPartForm.setQuestion(mockQuestion);
        claim.setClaimStartDateQuestion(mockQuestion);
        claim.setAnswer(ClaimStartDateController.IDENTIFIER, mockQuestion);
        when(mockClaimRepository.findById(CLAIM_ID)).thenReturn(Optional.of(claim));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test(expected = NullPointerException.class)
    public void getClaimStart_wontAcceptNull() {

        sut = createSut();
        sut.claimStartDate(null, CLAIM_ID, mockRequest);

    }

    @Test
    public void getClaimStart_returnsCorrectView() {

        sut = createSut();
        String expected = "form/claim-start";

        String actual = sut.claimStartDate(mockModel, CLAIM_ID, mockRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void getClaimStart_setsNewPartAndQuestionModels() {

        sut = createSut();

        sut.claimStartDate(mockModel, CLAIM_ID, mockRequest);

        ArgumentCaptor<ClaimStartDateForm> argumentCaptor = ArgumentCaptor.forClass(ClaimStartDateForm.class);
        verify(mockModel, times(1)).addAttribute(eq(BaseFormController.FORM_NAME), argumentCaptor.capture());
        assertNotNull(argumentCaptor.getValue());
        assertNotNull(argumentCaptor.getValue().getClaimStartDateQuestion());
    }


    @Test
    public void givenValidClaimStartQuestionResponse_postClaimStart_ReturnsRedirectToNextPart() {
        String expected = "redirect:/form/nino";
        when(mockPartForm.getQuestion()).thenReturn(mockQuestion);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRoutingService.getNext(any())).thenReturn("/form/nino");
        sut = createSut();

        String actual = sut.claimStartDate(mockPartForm, mockBindingResult, CLAIM_ID, mockResponse, mockRequest, mockModel);

        assertEquals(expected, actual);
    }
    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        final Circumstances circumastances = new Circumstances();
        final LocalDate date = LocalDate.now();
        circumastances.setClaimStartDate(date);
        claimDB.setCircumstances(circumastances);

        final ClaimStartDateForm form = new ClaimStartDateForm();

        sut = createSut();
        sut.loadForm(claimDB, form);

        assertEquals("Should match", date.getDayOfMonth(), form.getClaimStartDateQuestion().getDay().intValue());
        assertEquals("Should match", date.getMonthValue(), form.getClaimStartDateQuestion().getMonth().intValue());
        assertEquals("Should match", date.getYear(), form.getClaimStartDateQuestion().getYear().intValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {
        ClaimDB claimDB = new ClaimDB();
        final Circumstances circumstances = new Circumstances();
        claimDB.setCircumstances(circumstances);

        final ClaimStartDateForm form = new ClaimStartDateForm();

        sut = createSut();
        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getClaimStartDateQuestion());
    }

    private ClaimStartDateQuestion createClaimStartDateResponse(final int day, final int month, final int year) {
        ClaimStartDateQuestion response = new ClaimStartDateQuestion();
        response.setDay(day);
        response.setMonth(month);
        response.setYear(year);
        return response;
    }

    private ClaimStartDateController createSut() {
        return new ClaimStartDateController(mockClaimRepository, mockRoutingService);
    }

}
