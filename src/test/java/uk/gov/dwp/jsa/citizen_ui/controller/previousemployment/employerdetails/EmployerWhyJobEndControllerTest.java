package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PreviousWork;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.PreviousEmployment;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployerWhyJobEndControllerTest {

    public static final String IDENTIFIER = "form/previous-employment/employer-details/why-end";
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock
    private WhyJobEndForm mockWhyJobEndForm;
    @Mock
    private WhyJobEndQuestion mockWhyJobEndQuestion;
    @Mock
    private Model mockModel;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private StepInstance stepInstance;

    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private Step step;
    @Mock
    private ClaimDB mockClaimDB;
    @Mock
    private Circumstances mockCircumstances;
    @Mock
    private PreviousWork mockPreviousWork;

    private EmployerWhyJobEndController sut;

    @Before
    public void setUp() {
        when(mockWhyJobEndForm.getCount()).thenReturn(1);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new EmployerWhyJobEndController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        WhyJobEndForm actual = sut.createNewForm(mockClaim,1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockWhyJobEndQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        WhyJobEndForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockWhyJobEndForm.getQuestion()).thenReturn(mockWhyJobEndQuestion);

        ArgumentCaptor<WhyJobEndQuestion> questionCaptor = ArgumentCaptor.forClass(WhyJobEndQuestion.class);
        sut.updateClaim(mockWhyJobEndForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockWhyJobEndQuestion));
    }

    @Test
    public void getWhyJobEndReturnsEmployerWhyJobEndForm() {
        String result = sut.getWhyJobEnd(2, mockModel, "claimId", mockRequest);

        assertThat(result, is("form/previous-employment/employer-details/why-end"));
    }

    @Test
    public void submitWhyJobEndRedirectsToEmployerNameUrl() {
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/previous-employment/employer-details/1/name");
        when(mockWhyJobEndForm.getQuestion()).thenReturn(mockWhyJobEndQuestion);
        String result = sut.submitWhyJobEnd("claimId", mockWhyJobEndForm, mockBindingResult, mockResponse, mockModel);

        assertThat(result, is("redirect:/form/previous-employment/employer-details/1/name"));
    }

    @Test
    public void getNextReturnsExpectedUrl() {
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/previous-employment/employer-details/1/name");

        StepInstance stepInstance = new StepInstance(false, false, new Step("ID2", null, null, Section.NONE));

        String nextPath = sut.getNextPath(mockClaim, mockWhyJobEndForm, stepInstance);

        assertThat(nextPath, is("redirect:/form/previous-employment/employer-details/1/name"));
    }

    @Test
    public void loadForm() {
        List<PreviousWork> previousWorks = Arrays.asList(mockPreviousWork);
        when(mockWhyJobEndForm.getQuestion()).thenReturn(mockWhyJobEndQuestion);
        when(mockPreviousWork.getReasonEnded()).thenReturn("OTHER");
        when(mockPreviousWork.getOtherReasonDetails()).thenReturn("DETAILS");
        when(mockCircumstances.getPreviousWork()).thenReturn(previousWorks);
        when(mockClaimDB.getCircumstances()).thenReturn(mockCircumstances);

        sut.loadForm(mockClaimDB, mockWhyJobEndForm);
        verify(mockWhyJobEndQuestion).setDetailedReason("DETAILS");
    }
}
