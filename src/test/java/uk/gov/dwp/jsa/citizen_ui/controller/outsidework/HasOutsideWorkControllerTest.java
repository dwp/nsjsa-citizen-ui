package uk.gov.dwp.jsa.citizen_ui.controller.outsidework;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class HasOutsideWorkControllerTest {

    private HasOutsideWorkController sut;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GuardForm mockForm;

    @Mock
    private BooleanQuestion mockQuestion;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private String anonymousClaimId = "123e4567-e89b-12d3-a456-426655440000";

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(HasOutsideWorkController.IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        sut = new HasOutsideWorkController(mockClaimRepository, mockRoutingService);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void getView_returnsView() {
        String result = sut.getHaveYouWorkedAbroad(mockModel, anonymousClaimId, mockRequest);
        assertEquals(BOOLEAN_VIEW_NAME, result);
    }

    @Test
    public void submit_returnsNextPathFromRoutingService() {
        String expected = "path";
        when(mockRoutingService.getNext(any())).thenReturn(expected);
        String result = sut.postHaveYouWorkedAbroad(mockForm, mockBindingResult, anonymousClaimId, mockResponse, mockRequest, mockModel);
        assertThat(result, is("redirect:" + expected));
    }

    @Test
    public void givenPreviousAnswer_createNewForm_returnsFormWithPreviousAnswer() {

        Optional<Question> optionalQuestion = Optional.of(mockQuestion);

        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        BooleanForm actual = sut.createNewForm(mockClaim);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void givenNoPreviousAnswer_createNewForm_returnsEmptyForm() {

        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        BooleanForm actual = sut.createNewForm(mockClaim);
        assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {

        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        final Circumstances circumstances = new Circumstances();
        circumstances.setHasNonUKWorkBenefit(true);
        claimDB.setCircumstances(circumstances);

        final BooleanForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenFalse() {
        ClaimDB claimDB = new ClaimDB();
        final Circumstances circumstances = new Circumstances();
        circumstances.setHasNonUKWorkBenefit(false);
        claimDB.setCircumstances(circumstances);

        final BooleanForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final BooleanForm form = sut.getForm();

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getChoice());
    }
}
