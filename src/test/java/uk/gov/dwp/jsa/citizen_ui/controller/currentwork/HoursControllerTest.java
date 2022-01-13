package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

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
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.HoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;

@RunWith(MockitoJUnitRunner.class)
public class HoursControllerTest {
    public static final String IDENTIFIER = "form/current-work/details/hours";
    @Mock
    private Model mockModel;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Claim mockClaim;

    private Integer count = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HoursForm mockForm;

    @Mock
    HoursQuestion mockQuestion;

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

    @Mock
    private StepInstance stepInstance;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    @Mock
    CounterFormController counterFormController;

    private HoursController sut;

    @Before
    public void setUp() {
        when(mockForm.getCount()).thenReturn(1);
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        sut = new HoursController(mockClaimRepository, routingService);
        mockClaimRepository.save(mockClaim);
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        HoursForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(mockQuestion);
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        HoursForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void updateClaim() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<HoursQuestion> questionCaptor = ArgumentCaptor.forClass(HoursQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(mockQuestion));
    }

    @Test
    public void submitHoursForm_WithValidHours_returnsNextForm() {
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(TypeOfWork.PAID);

        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+ count +"/how-often-paid",
                Section.CURRENT_WORK);
        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);

        when(mockClaim.get(WorkPaidOrVoluntaryController.IDENTIFIER, 1)).
                thenReturn(Optional.of(typeOfWorkOption));

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        int hours = 40;
        when(mockForm.getHoursQuestion().getHours()).thenReturn(hours);
        when(routingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitHours(mockForm, mockBindingResult, claimId, count,
                mockResponse, mockModel);
        assertEquals("redirect:/the/next/page", result);
    }

    @Test
    public void submitHoursForm_WithVolunteer_returnsNextForm() {
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(TypeOfWork.VOLUNTARY);

        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+ count +"/how-often-paid",
                Section.CURRENT_WORK);

        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);

        when(mockClaim.get(WorkPaidOrVoluntaryController.IDENTIFIER, 1)).
                thenReturn(Optional.of(typeOfWorkOption));

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        int hours = 40;
        when(mockForm.getHoursQuestion().getHours()).thenReturn(hours);
        when(routingService.getNext(any())).thenReturn("/the/next/page");
        String result = sut.submitHours(mockForm, mockBindingResult, claimId, count,
                mockResponse, mockModel);
        assertEquals("redirect:/the/next/page", result);

    }

    @Test
    public void test_getting_nextEdit_VoluntaryPath() {
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(TypeOfWork.VOLUNTARY);

        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+ count +"/how-often-paid",
                Section.CURRENT_WORK);

        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);

        when(mockClaim.get(WorkPaidOrVoluntaryController.IDENTIFIER, 1)).
                thenReturn(Optional.of(typeOfWorkOption));

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        int hours = 40;
        when(mockForm.getHoursQuestion().getHours()).thenReturn(hours);
        when(mockForm.getEdit()).thenReturn(EditMode.SECTION);

        String result = sut.submitHours(mockForm, mockBindingResult, claimId, count,
                mockResponse, mockModel);
        assertEquals(result, sut.getNextPath(mockClaim, mockForm, stepInstance));
    }

    @Test
    public void test_getting_nextEdit_PaidPath() {
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(TypeOfWork.PAID);

        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+ count +"/how-often-paid",
                Section.CURRENT_WORK);

        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);

        when(mockClaim.get(WorkPaidOrVoluntaryController.IDENTIFIER, 1)).
                thenReturn(Optional.of(typeOfWorkOption));

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));
        int hours = 40;
        when(mockForm.getHoursQuestion().getHours()).thenReturn(hours);
        when(mockForm.getEdit()).thenReturn(EditMode.SECTION);

        String result = sut.submitHours(mockForm, mockBindingResult, claimId, count,
                mockResponse, mockModel);
        assertEquals(result, sut.getNextPath(mockClaim, mockForm, stepInstance));
    }

    @Test
    public void submitHoursForm_WithErrors_ReturnsHoursForm() {
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(TypeOfWork.PAID);

        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+ count +"/how-often-paid",
                Section.CURRENT_WORK);
        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);

        when(mockBindingResult.hasErrors()).thenReturn(true);

        when(mockClaim.get(WorkPaidOrVoluntaryController.IDENTIFIER, 1)).
                thenReturn(Optional.of(typeOfWorkOption));

        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        String result = sut.submitHours(mockForm, mockBindingResult, claimId,
                count, mockResponse, mockModel);
        assertEquals("form/current-work/hours", result);

    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Integer hours = 5;
        currentWork.setHoursPerWeek(hours);

        final HoursForm form = new HoursForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", hours, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Integer hours = 5;
        currentWork.setHoursPerWeek(hours);

        final HoursForm form = new HoursForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertEquals("Should match", hours, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final HoursForm form = new HoursForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        CurrentWork currentWork = addCurrentWork(claimDB);
        final Integer hours = 5;
        currentWork.setHoursPerWeek(hours);

        final HoursForm form = new HoursForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }
}
