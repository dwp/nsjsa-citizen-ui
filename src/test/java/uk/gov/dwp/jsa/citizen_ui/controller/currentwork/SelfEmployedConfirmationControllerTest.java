package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.SelfEmployedConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm.BOOLEAN_VIEW_NAME;

@RunWith(MockitoJUnitRunner.class)
public class SelfEmployedConfirmationControllerTest {
    private static final String EXPECTED_PATH = "PATH";
    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";
    private static final Integer COUNT = 1;
    private static final BooleanQuestion QUESTION = new BooleanQuestion();
    private static final String EXPECTED_BACK_REF = String.format("/form/current-work/details/%s/hours", COUNT);
    private static final String TRANSLATION_KEY = "currentwork.selfemployedconfirmation.";
    public static final String IDENTIFIER = "form/current-work/details/self-employed-confirmation";


    @Mock
    private Model mockModel;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BooleanForm mockForm;
    @Mock
    BooleanQuestion mockQuestion;
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private SelfEmployedConfirmationController sut;
    private String returnedNextPath;
    private String returnedViewName;


    @Before
    public void setUp() {
        sut = new SelfEmployedConfirmationController(mockClaimRepository, mockRoutingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        ReflectionTestUtils.setField(sut, "cookieLocaleResolver", mockCookieLocaleResolver);
    }

    @Test
    public void getsView() {
        givenAController();
        whenIGetTheView();
        thenTheViewNameIsReturned();
    }

    @Test
    public void submitFormreturnsNextPath() {
        givenAController();
        whenISubmitForm();
        thenTheNextPathIsReturned();
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        BooleanForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        BooleanForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<BooleanQuestion> questionCaptor = ArgumentCaptor.forClass(BooleanQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockQuestion));
    }


    @Test
    public void setsAttributes() {
        givenAController();
        whenISetAttributes();
        thenTheAttributesAreSet();
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setSelfEmployedOrDirector(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenFalse() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setSelfEmployedOrDirector(false);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertFalse("Should be false", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork another = addCurrentWork(claimDB);
        another.setSelfEmployedOrDirector(false);
        CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setSelfEmployedOrDirector(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertTrue("Should be true", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getChoice());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork another = addCurrentWork(claimDB);
        another.setSelfEmployedOrDirector(true);

        final BooleanForm form = new BooleanForm(new BooleanQuestion());
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getChoice());
    }

    public void givenAController() {
        when(mockForm.getCount()).thenReturn(COUNT);
        when(mockRoutingService.getNext(any())).thenReturn(EXPECTED_PATH);
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn(EXPECTED_BACK_REF);
        when(mockClaim.getCurrentWork().getCurrentWorkDetails(COUNT).getSelfEmployedConfirmation()).thenReturn(QUESTION);
    }

    private void whenIGetTheView() {
        returnedViewName = sut.getView(COUNT, mockModel, CLAIM_ID, mockRequest);
    }

    private void whenISubmitForm() {
        returnedNextPath = sut.submitForm(CLAIM_ID, mockForm, mockBindingResult, mockResponse, mockRequest, mockModel);
    }

    private void whenISetAttributes() {
        sut.setFormAttrs(mockForm, CLAIM_ID);
    }

    private void thenTheAttributesAreSet() {
        verify(mockForm).setBackRef(EXPECTED_BACK_REF);
        verify(mockForm).setTranslationKey(TRANSLATION_KEY);
    }

    private void thenTheNextPathIsReturned() {
        assertEquals("redirect:" + EXPECTED_PATH, returnedNextPath);
    }

    private void thenTheViewNameIsReturned() {
        assertEquals(BOOLEAN_VIEW_NAME, returnedViewName);
    }


}
