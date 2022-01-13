package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonalDetailsFormControllerTest {

    public static final String IDENTIFIER = "form/personal-details";
    private PersonalDetailsFormController sut;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private PersonalDetailsForm mockPersonalDetailsForm;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;

    private static final String COOKIE = "123e4567-e89b-12d3-a456-426655440000";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";

    @Before
    public void createSut() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new PersonalDetailsFormController(mockClaimRepository, routingService);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GetPersonalDetailsAndClaimIsNull_returnsCorrectView() {
        String path = sut.getPersonalDetails(mockModel, null, mockRequest);

        assertThat(path, is("form/personal-details/personal-details"));
        verify(mockModel).addAttribute(eq("personalDetailsForm"), any(PersonalDetailsForm.class));
    }

    @Test
    public void GetPersonalDetailsAndClaimDoesNotExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.empty());
        String path = sut.getPersonalDetails(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/personal-details"));
        verify(mockModel).addAttribute(eq("personalDetailsForm"), any(PersonalDetailsForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void GetPersonalDetailsAndClaimExist_returnsCorrectView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        String path = sut.getPersonalDetails(mockModel, COOKIE, mockRequest);

        assertThat(path, is("form/personal-details/personal-details"));
        verify(mockModel).addAttribute(eq("personalDetailsForm"), any(PersonalDetailsForm.class));
        verify(mockClaimRepository).findById(COOKIE);
    }

    @Test
    public void SubmitPersonalDetailsWithValidRequest_returnCorrectView() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        StepInstance stepInstance = generateStepInstance();
        when(routingService.getStep(anyString())).thenReturn(Optional.of(stepInstance.getStep()));
        doNothing().when(routingService).leavePage(any(), any());

        when(mockClaimRepository.save(any(Claim.class))).thenReturn(null);
        when(routingService.getNext(stepInstance)).thenReturn("/form/date-of-birth");

        PersonalDetailsForm personalDetailsForm = generatePersonalDetailsForm();

        String path = sut
                .submitPersonalDetails(COOKIE, personalDetailsForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/date-of-birth"));
    }

    @Test
    public void SubmitPersonalDetailsWithWhiteSpaceInAnswers_returnsCorrectView() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));

        StepInstance stepInstance = generateStepInstance();
        when(routingService.getStep(anyString())).thenReturn(Optional.of(stepInstance.getStep()));
        doNothing().when(routingService).leavePage(any(), any());

        when(mockClaimRepository.save(any(Claim.class))).thenReturn(null);
        when(routingService.getNext(stepInstance)).thenReturn("/form/date-of-birth");

        PersonalDetailsForm personalDetailsForm = generatePersonalDetailsForm();
        NameStringTruncatedQuestion firstname = new NameStringTruncatedQuestion();
        firstname.setValue("      " + FIRST_NAME + "    ");
        personalDetailsForm.getQuestion().setFirstNameQuestion(firstname);
        NameStringTruncatedQuestion lastname = new NameStringTruncatedQuestion();
        lastname.setValue("       " + LAST_NAME + "    ");
        personalDetailsForm.getQuestion().setLastNameQuestion(lastname);

        String path = sut
                .submitPersonalDetails(COOKIE, personalDetailsForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/date-of-birth"));
    }

    @Test
    public void SubmitPersonalDetailsWithError_returnsErrorForAllFields() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());

        PersonalDetailsForm personalDetailsForm = new PersonalDetailsForm();
        personalDetailsForm.setPersonalDetailsQuestion(question);
        String path = sut
            .submitPersonalDetails(COOKIE, personalDetailsForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("form/personal-details/personal-details"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void SubmitPersonalDetailsWithEmptyClaimId_ThrowsIllegalArgumentException() {
        when(mockBindingResult.hasErrors()).thenReturn(false);

        String path = sut
            .submitPersonalDetails("", mockPersonalDetailsForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/date-of-birth"));
        verify(mockClaimRepository, times(0)).findById(anyString());
        thenAllPersonalDetailsQuestionsAreSetInCache();
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse).addCookie(any(Cookie.class));
    }

    private void thenAllPersonalDetailsQuestionsAreSetInCache() {
        verify(mockPersonalDetailsForm).getPersonalDetailsQuestion().getTitleQuestion();
        verify(mockPersonalDetailsForm).getPersonalDetailsQuestion().getFirstNameQuestion();
        verify(mockPersonalDetailsForm).getPersonalDetailsQuestion().getLastNameQuestion();
    }

    @Test
    public void SubmitPersonalDetailsWithEmptyClaimId_UpdatesExistingClaimObj() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getNext(any())).thenReturn("/form/date-of-birth");
        when(mockPersonalDetailsForm.getQuestion()).thenReturn(new PersonalDetailsQuestion());

        String path = sut
            .submitPersonalDetails(COOKIE, mockPersonalDetailsForm, mockBindingResult, mockResponse, mockModel);

        assertThat(path, is("redirect:/form/date-of-birth"));
        verify(mockClaimRepository).findById(eq(COOKIE));
        verify(mockClaimRepository, atLeastOnce()).save(Mockito.any(Claim.class));
        verify(mockResponse, atLeastOnce()).addCookie(any(Cookie.class));
    }

    private StepInstance generateStepInstance() {
        Step step = new Step("form/personal-details", "/form/date-of-birth", null, Section.PERSONAL_DETAILS);
        StepInstance stepInstance = new StepInstance();
        stepInstance.setStep(step);
        stepInstance.setCounter(0);
        stepInstance.setIsAGuard(false);
        stepInstance.setIsGuardedCondition(false);
        stepInstance.setEdit(null);
        return stepInstance;
    }

    private PersonalDetailsForm generatePersonalDetailsForm() {
        MultipleOptionsQuestion optionsQuestion = new MultipleOptionsQuestion();
        optionsQuestion.setUserSelectionValue(TitleEnum.MR);
        Map<Integer, Question> map = new HashMap<>();
        map.put(1, optionsQuestion);

        TitleQuestion titleQuestion = new TitleQuestion();
        titleQuestion.setAnswers(map);
        titleQuestion.setValid(true);
        NameStringTruncatedQuestion firstNameQuestion = new NameStringTruncatedQuestion();
        firstNameQuestion.setValue(FIRST_NAME);
        firstNameQuestion.setIsValid(true);
        NameStringTruncatedQuestion lastNameQuestions = new NameStringTruncatedQuestion();
        lastNameQuestions.setValue(LAST_NAME);
        lastNameQuestions.setIsValid(true);

        PersonalDetailsQuestion question = new PersonalDetailsQuestion(titleQuestion, firstNameQuestion, lastNameQuestions);

        PersonalDetailsForm personalDetailsForm = new PersonalDetailsForm();
        personalDetailsForm.setPersonalDetailsQuestion(question);
        personalDetailsForm.setCounterForm(false);
        return personalDetailsForm;
    }
}
