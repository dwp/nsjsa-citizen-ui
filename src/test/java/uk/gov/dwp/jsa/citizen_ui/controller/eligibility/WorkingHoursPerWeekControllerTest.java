package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.HoursWorkingPerWeekQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.Working16Hours;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.Working16Hours.WORKING_LESS_THAN_16_HOURS;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.Working16Hours.WORKING_MORE_THAN_16_HOURS;

@RunWith(MockitoJUnitRunner.class)
public class WorkingHoursPerWeekControllerTest {

    public static final String IDENTIFIER = "form/eligibility/working-over";
    private WorkingHoursPerWeekController sut;

    @Mock
    private Model mockModel;

    @Mock
    CookieLocaleResolver mockCookieLocaleResolver;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> mockWorkingHoursForm;

    private static final String CLAIM_ID = "123e4567-e89b-12d3-a456-426655440000";

    private Claim claim = new Claim();

    @Mock
    private MultipleOptionsForm mockForm;
    @Mock
    private Step step;

    @Mock
    private Claim mockClaim;

    @Before
    public void setUp() {
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale("en"));
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        sut = new WorkingHoursPerWeekController(mockClaimRepository, mockRoutingService, mockCookieLocaleResolver);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void multipleOptionsSpecificAttributesIsSetCorrectly() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setOptions(asList(Working16Hours.values()));
        verify(mockForm).setDefaultOption(WORKING_LESS_THAN_16_HOURS);
        verify(mockForm).setInline(false);
    }

    @Test
    public void getFormReturnsMultipleOptionsForm() {
        Form form = sut.getForm();

        assertThat(form, is(new MultipleOptionsForm(new HoursWorkingPerWeekQuestion(), WORKING_LESS_THAN_16_HOURS)));
    }

    @Test
    public void backRefIsSetCorrectlyOnForm() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn("/form/eligibility/working");
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setBackRef("/form/eligibility/working");
    }

    @Test
    public void getTranslationKeyReturnsExpectedValue() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setTranslationKey("eligibility.hoursworkingperweek.form.");
    }

    @Test
    public void getWorkingHoursPerWeekReturnsExpectedView() {
        HoursWorkingPerWeekQuestion hoursWorkingPerWeekQuestion = new HoursWorkingPerWeekQuestion();
        hoursWorkingPerWeekQuestion.setUserSelectionValue(WORKING_LESS_THAN_16_HOURS);
        when(mockClaim.getHoursWorkingPerWeekQuestion()).thenReturn(hoursWorkingPerWeekQuestion);
        String view = sut.getHoursWorkingPerWeek(mockModel, CLAIM_ID, mockRequest);

        assertThat(view, is("form/common/multiple-options"));
    }

    @Test
    public void postWorkingHoursPerWeekReturnsExpectedView() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        setResidenceAndHoursWorkingQuestion(true, WORKING_LESS_THAN_16_HOURS);
        when(mockRoutingService.getNext(any(StepInstance.class))).thenReturn("/form/eligibility/working-over/ineligible");

        sut.postHoursWorkingPerWeek(mockWorkingHoursForm, mockBindingResult, CLAIM_ID, mockHttpServletResponse, mockRequest, mockModel);
        verify(mockClaimRepository).save(mockClaim);
        assertEquals(sut.getStep().getNextStepIdentifier(), "/form/eligibility/eligible");
    }

    @Test
    public void verifyNextStepBasedOnEligibleWorkingAndResident() {
        setResidenceAndHoursWorkingQuestion(true, WORKING_LESS_THAN_16_HOURS);
        String expectedPath = sut.nextStepBasedOn(mockWorkingHoursForm, mockClaim);
        assertEquals("/form/eligibility/eligible", expectedPath);
    }

    @Test
    public void verifyNextStepBasedOnResidenceFalseAndWorkingOver() {
        setResidenceAndHoursWorkingQuestion(false, WORKING_LESS_THAN_16_HOURS);
        String expectedPath = sut.nextStepBasedOn(mockWorkingHoursForm, mockClaim);
        assertEquals("/form/eligibility/residence/working-over/ineligible", expectedPath);
    }

    @Test
    public void verifyNextStepBasedOnResidenceTrueAndWorkingOver() {
        setResidenceAndHoursWorkingQuestion(true, WORKING_MORE_THAN_16_HOURS);
        String expectedPath = sut.nextStepBasedOn(mockWorkingHoursForm, mockClaim);
        assertEquals("/form/eligibility/working-over/ineligible", expectedPath);
    }

    @Test
    public void verifyNextStepBasedOnResidenceTrueAndWorkingOverTrue() {
        setResidenceAndHoursWorkingQuestion(false, WORKING_MORE_THAN_16_HOURS);
        String expectedPath = sut.nextStepBasedOn(mockWorkingHoursForm, mockClaim);
        assertEquals("/form/eligibility/working-over-residence/working-over/ineligible", expectedPath);
    }

    public void setResidenceAndHoursWorkingQuestion(final boolean isUkResident, final Working16Hours working16Hours) {
        HoursWorkingPerWeekQuestion hoursWorkingPerWeekQuestion = new HoursWorkingPerWeekQuestion();
        hoursWorkingPerWeekQuestion.setUserSelectionValue(working16Hours);
        ResidenceQuestion residenceQuestion = new ResidenceQuestion();
        residenceQuestion.setUkResidence(isUkResident);
        when(mockWorkingHoursForm.getMultipleOptionsQuestion()).thenReturn(hoursWorkingPerWeekQuestion);
        when(mockWorkingHoursForm.getQuestion()).thenReturn(hoursWorkingPerWeekQuestion);
        when(mockClaim.getResidenceQuestion()).thenReturn(residenceQuestion);
    }

    @Test
    public void createNewFormUpdatesValueFromClaim() {
        givenHoursWorkingIsLessThan16();

        MultipleOptionsForm newForm = sut.createNewForm(claim);

        assertThat(newForm.getMultipleOptionsQuestion().getUserSelectionValue(), is(WORKING_LESS_THAN_16_HOURS));
    }

    @Test
    public void updateClaimUpdatesClaimObjWithFormValues() {
        MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> form = givenFormValueIsLessThan16Hours();

        sut.updateClaim(form, claim, null, Optional.empty());

        assertThat(claim.getHoursWorkingPerWeekQuestion().getUserSelectionValue(), is(WORKING_LESS_THAN_16_HOURS));
    }

    @Test(expected = ClassCastException.class)
    public void updateClaimDoesNotUpdateClaimObjWithFormValues() {
        sut.updateClaim(mockForm, claim, null, Optional.empty());
    }

    private MultipleOptionsForm<HoursWorkingPerWeekQuestion, Working16Hours> givenFormValueIsLessThan16Hours() {
        HoursWorkingPerWeekQuestion question = new HoursWorkingPerWeekQuestion();
        question.setUserSelectionValue(WORKING_LESS_THAN_16_HOURS);
        return new MultipleOptionsForm<>(question, WORKING_LESS_THAN_16_HOURS);
    }

    private void givenHoursWorkingIsLessThan16() {
        claim = new Claim();
        HoursWorkingPerWeekQuestion question = new HoursWorkingPerWeekQuestion();
        question.setUserSelectionValue(WORKING_LESS_THAN_16_HOURS);
        claim.setHoursWorkingPerWeekQuestion(question);
    }
}
