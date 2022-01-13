package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseFormControllerTest {
    private static final String mockClaimId = "12345678-1234-4321-12345678";
    private static final String CITIZEN_CLAIM_START_VIEW_VARIABLE = "citizensClaimStartDate";
    private static final Map<Integer, String> ENGLISH_MONTHS = new HashMap<>();
    private static final Map<Integer, String> WELSH_MONTHS = new HashMap<>();
    private static final String CY_LOCALE = "cy";

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

    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private RoutingService routingService;
    @Mock
    Form mockForm;
    @Mock
    AbstractCounterForm mockCounterForm;
    @Mock
    Question mockQuestion;
    @Mock
    MultipleOptionsQuestion mockMultipleOptionsQuestion;
    @Mock
    GuardQuestion mockGuardQuestion;
    @Mock
    BindingResult mockBindingResult;
    @Mock
    HttpServletResponse mockResponse;
    @Mock
    Model mockModel;
    @Mock
    HttpServletRequest mockHttpServletRequest;
    @Mock
    Claim mockClaim;
    @Mock
    DateFormatterUtils mockDateFormatterUtils;
    @Mock
    CookieLocaleResolver mockCookieLocaleResolver;

    private BaseFormController sut;

    @Test
    public void getNextPathConstructsStepCorrectly() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";

        final String expectedCurrentStepIdentifier = identifier;

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        when(routingService.getNext(argumentCaptor.capture())).thenReturn("no next path!");

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);
        StepInstance stepInstance = new StepInstance(false, false, new Step(identifier, null, null, Section.NONE));
        sut.getNextPath(null, mockForm, stepInstance);

        assertThat(argumentCaptor.getValue().getStep().getIdentifier(), is(expectedCurrentStepIdentifier));
    }

    @Test
    public void ensureThatWeFormatTheNextPathCorrectly() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";

        final String expectedNextStepIdentifier = nextStepIdentifier;

        when(routingService.getNext(any())).thenReturn(expectedNextStepIdentifier);

        sut = createSut(identifier, nextStepIdentifier, null);
        String nextStep = sut.getNextPath(null, mockForm, null);

        assertThat(nextStep, is("redirect:" + expectedNextStepIdentifier));
    }

    @Test
    public void shouldReturnFormPostUrl() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);

        String formPostUrl = sut.getFormPostUrl();

        assertEquals("/" + identifier, formPostUrl);
    }

    @Test
    public void shouldReturnNewForm() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);

        Form form = sut.getForm();

        assertEquals(new GuardForm(), form);

    }

    @Test
    public void givenAnswerHasChanged_postShouldSaveClaimWithCorrectIdentifierAndQuestion() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";
        final String claimId = "123e4567-e89b-12d3-a456-426655440000";

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockCounterForm.getQuestion()).thenReturn(mockQuestion);
        when(routingService.getStep(identifier)).
                thenReturn(Optional.of(new Step(identifier, nextStepIdentifier, alternateStepIdentifier, Section.NONE)));

        sut.post(claimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).save(argumentCaptor.capture(), eq(mockQuestion), any(Optional.class));
        assertThat(argumentCaptor.getValue().getStep().getIdentifier(), is(identifier));
        // The count should always be 0 for non-counter forms
        assertThat(argumentCaptor.getValue().getCounter(), is(0));
    }

    @Test
    public void givenAnswerHasNotChanged_postShouldNotSaveClaim() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";
        final String claimId = "123e4567-e89b-12d3-a456-426655440000";

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockCounterForm.getQuestion()).thenReturn(mockGuardQuestion);
        when(routingService.getStep(identifier)).
                thenReturn(Optional.of(new Step(identifier, nextStepIdentifier, alternateStepIdentifier, Section.NONE)));
        when(mockClaim.get(Mockito.any(StepInstance.class))).thenReturn(Optional.of(mockGuardQuestion));

        sut.post(claimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaimRepository, times(0)).save(Mockito.any(Claim.class));
    }

    @Test
    public void givenMultipleOptionsAnswerHasNotChanged_postShouldNotSaveClaim() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";
        final String claimId = "123e4567-e89b-12d3-a456-426655440000";

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier);

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(routingService.getStep(identifier)).
                thenReturn(Optional.of(new Step(identifier, nextStepIdentifier, alternateStepIdentifier, Section.NONE)));
        MultipleOptionsQuestion<TypeOfWork> multipleOptionQuestion = new MultipleOptionsQuestion();
        multipleOptionQuestion.setUserSelectionValue(TypeOfWork.PAID);
        when(mockClaim.get(Mockito.any(StepInstance.class))).thenReturn(Optional.of(multipleOptionQuestion));

        when(mockMultipleOptionsQuestion.getUserSelectionValue()).thenReturn(TypeOfWork.PAID);
        when(mockCounterForm.getQuestion()).thenReturn(mockMultipleOptionsQuestion);

        sut.post(claimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaimRepository, times(0)).save(Mockito.any(Claim.class));
    }

    @Test
    public void givenPresentClaimStartDateQuestion_inEnglish_setsViewModelWithCitizensClaimStartDateInCorrectFormat() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";
        final int day = 6;
        final int month = 7;
        final int year = LocalDate.now().getYear();
        final String monthAlpha = "July";
        String expected = String.format("%s %s %s", day, monthAlpha, year);
        givenClaimWithPresentClaimStartDateQuestion(year, month, day);
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier, mockDateFormatterUtils, mockCookieLocaleResolver);
        sut.addCitizensClaimStartDateAndIsBackDatingToModel(mockHttpServletRequest, mockModel, mockClaimId);

        verify(mockModel, times(1)).addAttribute(CITIZEN_CLAIM_START_VIEW_VARIABLE, expected);
    }

    @Test
    public void givenPresentClaimStartDateQuestion_inWelsh_setsViewModelWithCitizensClaimStartDateInCorrectFormat() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";
        final int day = 6;
        final int month = 7;
        final int year = LocalDate.now().getYear();
        final String monthAlpha = WELSH_MONTHS.get(7);
        String expected = String.format("%s %s %s", day, monthAlpha, year);
        givenClaimWithPresentClaimStartDateQuestion(year, month, day);
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier, mockDateFormatterUtils, mockCookieLocaleResolver);
        sut.addCitizensClaimStartDateAndIsBackDatingToModel(mockHttpServletRequest, mockModel, mockClaimId);

        verify(mockModel, times(1)).addAttribute(CITIZEN_CLAIM_START_VIEW_VARIABLE, expected);
    }

    @Test
    public void givenNonPresentClaimStartDateQuestion_inEnglish_setsViewModelWithTodayDateInCorrectFormat() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";

        LocalDate now = LocalDate.now();
        final String monthAlpha = ENGLISH_MONTHS.get(now.getMonthValue());

        String expected = String.format("%s %s %s", now.getDayOfMonth(), monthAlpha, now.getYear());

        when(mockClaimRepository.findById(any())).thenReturn(Optional.empty());
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(now);

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier, mockDateFormatterUtils, mockCookieLocaleResolver);
        sut.addCitizensClaimStartDateAndIsBackDatingToModel(mockHttpServletRequest, mockModel, mockClaimId);

        verify(mockModel, times(1)).addAttribute(CITIZEN_CLAIM_START_VIEW_VARIABLE, expected);
    }

    @Test
    public void givenNonPresentClaimStartDateQuestion_inWelsh_setsViewModelWithTodayDateInCorrectFormat() {
        final String identifier = "identifier";
        final String nextStepIdentifier = "/form/nextStepIdentifier";
        final String alternateStepIdentifier = "/form/alternateStepIdentifier";

        LocalDate now = LocalDate.now();
        final String monthAlpha = WELSH_MONTHS.get(now.getMonthValue());

        String expected = String.format("%s %s %s", now.getDayOfMonth(), monthAlpha, now.getYear());

        when(mockClaimRepository.findById(any())).thenReturn(Optional.empty());
        when(mockDateFormatterUtils.formatDate(any(), any(), any())).thenReturn(expected);
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(now);

        sut = createSut(identifier, nextStepIdentifier, alternateStepIdentifier, mockDateFormatterUtils, mockCookieLocaleResolver);
        sut.addCitizensClaimStartDateAndIsBackDatingToModel(mockHttpServletRequest, mockModel, mockClaimId);

        verify(mockModel, times(1)).addAttribute(CITIZEN_CLAIM_START_VIEW_VARIABLE, expected);
    }

    private void givenClaimWithPresentClaimStartDateQuestion(int year, int month, int day) {
        when(mockClaimRepository.findById(any())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.getClaimStartDate()).thenReturn(
                Optional.of(new ClaimStartDateQuestion(LocalDate.of(year, month, day))));
    }


    public class TestBaseController extends BaseFormController {


        public TestBaseController(final ClaimRepository claimRepository,
                                  final String viewName,
                                  final String modelName,
                                  final RoutingService routingService,
                                  final String identifier,
                                  final String nextStepIdentifier,
                                  final String alternateStepIdentifier) {
            super(claimRepository, viewName, modelName, routingService, identifier, nextStepIdentifier,
                    alternateStepIdentifier,
                    Section.NONE);
        }

        public TestBaseController(final ClaimRepository claimRepository,
                                  final String viewName,
                                  final String modelName,
                                  final RoutingService routingService,
                                  final String identifier,
                                  final String nextStepIdentifier,
                                  final String alternateStepIdentifier,
                                  final DateFormatterUtils dateFormatterUtils,
                                  final CookieLocaleResolver cookieLocaleResolver) {
            super(claimRepository, viewName, modelName, routingService, identifier, nextStepIdentifier,
                    alternateStepIdentifier,
                    Section.NONE, dateFormatterUtils, cookieLocaleResolver);
        }

        @Override
        public Form getForm() {
            return new GuardForm();
        }

        @Override
        public Form createNewForm(final Claim claim) {
            return null;
        }

        @Override
        public void loadForm(final ClaimDB claimDB, final Form form) {

        }

    }

    private BaseFormController createSut(final String identifier,
                                         final String nextStepIdentifier,
                                         final String alternateStepIdentifier) {
        return new TestBaseController(mockClaimRepository, null, null, routingService, identifier, nextStepIdentifier,
                alternateStepIdentifier);
    }

    private BaseFormController createSut(final String identifier,
                                         final String nextStepIdentifier,
                                         final String alternateStepIdentifier,
                                         final DateFormatterUtils dateFormatterUtils,
                                         final CookieLocaleResolver cookieLocaleResolver) {
        return new TestBaseController(mockClaimRepository, null, null, routingService, identifier, nextStepIdentifier,
                alternateStepIdentifier, dateFormatterUtils, mockCookieLocaleResolver);
    }
}
