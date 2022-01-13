package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.WarningLogger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController.FORM_NAME;
import static uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum.BETWEEN_16_17;

@RunWith(MockitoJUnitRunner.class)
public class DateOfBirthFormControllerTest {

    public static final String IDENTIFIER = "form/date-of-birth";
    private DateOfBirthFormController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private WarningLogger mockWarningLogger;
    @Mock
    private DateOfBirthForm mockDateOfBirthForm;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";
    private DateOfBirthQuestion dateOfBirthQuestion;

    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;

    @Before
    public void createSut() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new DateOfBirthFormController(mockClaimRepository, mockWarningLogger, routingService);
        givenDateOfBirthQuestionIsSet(12, 8, 1980);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    private void givenDateOfBirthQuestionIsSet(final int day, final int month, final int year) {
        dateOfBirthQuestion = new DateOfBirthQuestion();
        dateOfBirthQuestion.setDay(day);
        dateOfBirthQuestion.setMonth(month);
        dateOfBirthQuestion.setYear(year);
        when(mockClaim.getPersonalDetails().getDateOfBirthQuestion()).thenReturn(dateOfBirthQuestion);
        when(mockDateOfBirthForm.getDateOfBirthQuestion()).thenReturn(dateOfBirthQuestion);
    }

    @Test
    public void GetDateOfBirthAndClaimIsNull_returnsCorrectView() {

        String path = sut.getDateOfBirth(mockModel, null, mockRequest);

        assertThat(path, is("form/personal-details/date-of-birth"));
        verify(mockModel).addAttribute(eq("form"),
                any(DateOfBirthForm.class));
        verify(mockClaimRepository).save(Mockito.any(Claim.class));
    }

    @Test
    public void GetDateOfBirthAndClaimNotExists_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());

        String path = sut.getDateOfBirth(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/date-of-birth"));
        verify(mockModel).addAttribute(eq("form"),
                any(DateOfBirthForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetDateOfBirthAndClaimExists_returnsCorrectView() {

        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getDateOfBirth(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/date-of-birth"));
        verify(mockModel).addAttribute(eq("form"),
                any(DateOfBirthForm.class));
        verify(mockClaimRepository).findById(COOKIE);
        verify(mockClaim).get(any(StepInstance.class));
    }

    @Test
    public void SubmitDateOfBirthWithError_returnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(true);

        String path = sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("form/personal-details/date-of-birth"));
    }

    @Test
    public void SubmitDateOfBirthWithBetween1617_ReturnsIneligiblePage() {
        dateOfBirthQuestion.setDateofBirthConditionsEnum(BETWEEN_16_17);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);
        when(routingService.getNext(any())).thenReturn("/form/personal-details/under18");

        String path = sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/personal-details/under18"));
    }

    @Test
    public void SubmitDateOfBirtWithEmptyClaimId_CreatesNewClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/personal-details/address");
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);

        String path = sut.submitDateOfBirth("", mockDateOfBirthForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/personal-details/address"));
        verify(mockClaimRepository,
                times(1)).findById(anyString()); // For Max Date Validation 1 invocation
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void SubmitDateOfBirthWithEmptyClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/personal-details/address");
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);


        String path = sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm,
                mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/personal-details/address"));
        verify(mockClaimRepository, times(2)).findById(eq(COOKIE));
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    @Test
    public void SubmitDateOfBirthWithAbovePensionAge_ReturnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        givenClaimStartDateisSetInClaim();
        givenDateOfBirthQuestionIsSet(9, 5, 1954);
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);

        sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm, mockBindingResult, mockResponse, mockModel);


        FieldError fieldError = new FieldError(FORM_NAME, "dateOfBirthQuestion", "",
                false, new String[]{}, new Object[]{}, "dateofbirth.error.above.pension.age.text"
        );
        verify(mockBindingResult).addError(fieldError);
    }

    @Test
    public void SubmitDateOfBirthWithBelowPensionAge_DoesNotReturnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        givenClaimStartDateisSetInClaim();
        givenDateOfBirthQuestionIsSet(9, 5, 1959);
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);


        sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm, mockBindingResult, mockResponse, mockModel);

        verify(mockBindingResult, never()).addError(any());
    }

    @Test
    public void TwoDigitDateParsing() {
      ArrayList<Integer> results = new ArrayList<>();
      List<Integer> twoDigitDates = Arrays.asList(90,95,98,99,00,01,02,03,04,05,06,07,8,9,10);
      List<Integer> expectedDates =
        Arrays.asList(1990,1995,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010);

       for (Integer date : twoDigitDates) {
         results.add(ReflectionTestUtils.invokeMethod(sut,"parseTwoDigitYear", date));
       }

       assertThat(results, is(expectedDates));
    }

    @Test
    public void SubmitTwoDigitDateOfBirthBelowPensionAge_DoesNotReturnError() {
      when(mockBindingResult.hasErrors()).thenReturn(false);
      when(routingService.getNext(any())).thenReturn("/form/personal-details/address");
      givenClaimStartDateisSetInClaim();
      givenDateOfBirthQuestionIsSet(31, 3, 80);
      when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);

      String path = sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm, mockBindingResult, mockResponse, mockModel);

      assertThat(path, is("redirect:/form/personal-details/address"));
      assertThat(mockDateOfBirthForm.getQuestion().getYear(), is(1980));

      verify(mockBindingResult, never()).addError(any());
    }

    @Test
    public void SubmitTwoDigitDateOfBirthAbovePensionAge_ReturnsError() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        givenClaimStartDateisSetInClaim();
        givenDateOfBirthQuestionIsSet(9, 5, 54);
        when(mockDateOfBirthForm.getQuestion()).thenReturn(dateOfBirthQuestion);

        sut.submitDateOfBirth(COOKIE, mockDateOfBirthForm, mockBindingResult, mockResponse, mockModel);
        assertThat(mockDateOfBirthForm.getQuestion().getYear(), is(1954));

        FieldError fieldError = new FieldError(FORM_NAME, "dateOfBirthQuestion", "",
                false, new String[]{}, new Object[]{}, "dateofbirth.error.above.pension.age.text"
        );
        verify(mockBindingResult).addError(fieldError);
    }


    private void givenClaimStartDateisSetInClaim() {
        Claim claim = new Claim();
        ClaimStartDateQuestion claimStartDateQuestion = new ClaimStartDateQuestion();
        claimStartDateQuestion.setDay(6);
        claimStartDateQuestion.setMonth(10);
        claimStartDateQuestion.setYear(2020);
        Step step = new Step(ClaimStartDateController.IDENTIFIER, "","", Section.NONE);
        claim.save(
                new StepInstance(step, 0, false, false, false),
                claimStartDateQuestion, Optional.empty());
        Optional<Claim> claimOptional = Optional.of(claim);
        when(mockClaimRepository.findById(COOKIE)).thenReturn(claimOptional);
    }
}
