package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CounterFormControllerTest {

    private String mockIdentifier = "identifier";
    private String mockNextStepIdentifier = "nextStepIdentifier";
    private String mockViewName = "viewName";
    private String modelName = "testForm";
    private String mockClaimId = "123e4567-e89b-12d3-a456-426655440000";
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Claim mockClaim;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private Step step;

    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    TestForm mockCounterForm;
    @Mock
    Question mockQuestion;
    @Mock
    private LoopEndBooleanQuestion mockLoopEndBooleanQuestion;

    private TestCounterFormController sut;

    @Before
    public void setUp() {
        sut = new TestCounterFormController(mockClaimRepository, mockViewName, modelName, mockRoutingService,
                mockIdentifier, mockNextStepIdentifier, null);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(mockIdentifier)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("identifier");
    }


    @Test
    public void whenGetMapping_shouldAddModelAndReturnView() {
        int counter = 1;
        String result = sut.getMapping(mockModel, mockClaimId, counter, mockRequest);

        verify(mockModel).addAttribute(eq(modelName), any(TestForm.class));
        assertEquals(mockViewName, result);
    }

    @Test
    public void whenPostMapping_shouldAddCountAttributeAndReturnView() {
        String expected = "redirect:" + mockNextStepIdentifier;
        Integer counter = 1;

        TestForm testForm = new TestForm();
        testForm.setCount(counter);

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRoutingService.getNext(any())).thenReturn(mockNextStepIdentifier);

        String result = sut.postMapping(mockClaimId, testForm, mockBindingResult, mockResponse, mockModel);

        verify(mockModel).addAttribute(modelName, testForm);
        assertEquals(expected, result);
        assertTrue(testForm.isCounterForm());
    }

    @Test
    public void postIfCounterGreaterThan4_returnError() {
        Integer counter = 5;

        TestForm testForm = new TestForm();
        testForm.setCount(counter);

        when(mockBindingResult.hasErrors()).thenReturn(true);

        String result = sut.postMapping(mockClaimId, testForm, mockBindingResult, mockResponse, mockModel);

        verify(mockModel).addAttribute(modelName, testForm);
        assertEquals(mockViewName, result);
    }

    @Test
    public void createNewForm_returnsNewForm() {
        int counter = 1;
        TestForm form = sut.createNewForm(mockClaim, counter);
        assertNotNull(form);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createNewFormBaseController() {
        sut.createNewForm(mockClaim);
    }

    @Test
    public void givenQuestionHasChanged_postShouldSaveClaimWithCorrectIdentifierAndQuestion() {
        final int count = 1;

        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockCounterForm.getQuestion()).thenReturn(mockQuestion);
        when(mockCounterForm.getCount()).thenReturn(count);
        when(mockCounterForm.isCounterForm()).thenReturn(true);

        sut.post(mockClaimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).save(argumentCaptor.capture(), eq(mockQuestion), any(Optional.class));
        assertThat(argumentCaptor.getValue().getStep().getIdentifier(), is(mockIdentifier));
        assertThat(argumentCaptor.getValue().getCounter(), is(count));
    }

    @Test
    public void givenLoopEndBooleanQuestion_postShouldSaveClaimWithZeroCounter() {
        final int count = 1;
        final int zeroCount = 0;

        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockCounterForm.getQuestion()).thenReturn(mockLoopEndBooleanQuestion);
        when(mockCounterForm.getCount()).thenReturn(count);

        sut.post(mockClaimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(1)).save(argumentCaptor.capture(), eq(mockLoopEndBooleanQuestion), any(Optional.class));
        assertThat(argumentCaptor.getValue().getStep().getIdentifier(), is(mockIdentifier));
        assertThat(argumentCaptor.getValue().getCounter(), is(zeroCount));
    }

    @Test
    public void givenQuestionHasNotChanged_postShouldNotSaveClaim() {
        final int count = 1;

        when(mockClaimRepository.findById(mockClaimId)).thenReturn(Optional.of(mockClaim));
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockCounterForm.getCount()).thenReturn(count);
        when(mockClaim.get(Mockito.any(StepInstance.class))).thenReturn(Optional.of(mockQuestion));

        sut.post(mockClaimId, mockCounterForm, mockBindingResult, mockResponse, mockModel);

        ArgumentCaptor<StepInstance> argumentCaptor = ArgumentCaptor.forClass(StepInstance.class);
        verify(mockClaim, times(0)).save(argumentCaptor.capture(), eq(mockQuestion), any(Optional.class));
    }


    @Test
    public void getTypeOfWork_shouldBeVoluntaryWithCount() {
        int value = 1;
        // Max value of 4 because you can have a max of 4 jobs.
        while(value >= 4) {
            Claim claim = generateClaimWithTypeOfWorkAndCount(value, TypeOfWork.VOLUNTARY);
            TypeOfWork voluntary = sut.getTypeOfWork(claim, value).get();
            assertThat(voluntary, is(TypeOfWork.VOLUNTARY));
            value ++;
        }
    }

    @Test
    public void getTypeOfWork_shouldBePaidWithCount() {
        int value = 1;
        // Max value of 4 because you can have a max of 4 jobs.
        while(value >= 4) {
            Claim claim = generateClaimWithTypeOfWorkAndCount(value, TypeOfWork.PAID);
            TypeOfWork voluntary = sut.getTypeOfWork(claim, value).get();
            assertThat(voluntary, is(TypeOfWork.VOLUNTARY));
            value ++;
        }
    }

    @Test
    public void getTypeOfWork_shouldBeMixedWithCount() {
        Claim volunteerClaimOneCount = generateClaimWithTypeOfWorkAndCount(1, TypeOfWork.PAID);
        Claim volunteerClaimTwoCount = generateClaimWithTypeOfWorkAndCount(2, TypeOfWork.VOLUNTARY);
        Claim volunteerClaimThreeCount = generateClaimWithTypeOfWorkAndCount(3, TypeOfWork.PAID);
        Claim volunteerClaimFourCount = generateClaimWithTypeOfWorkAndCount(4, TypeOfWork.VOLUNTARY);

        TypeOfWork typeOfWorkOne = sut.getTypeOfWork(volunteerClaimOneCount, 1).get();
        TypeOfWork typeOfWorkTwo = sut.getTypeOfWork(volunteerClaimTwoCount, 2).get();
        TypeOfWork typeOfWorkThree = sut.getTypeOfWork(volunteerClaimThreeCount, 3).get();
        TypeOfWork typeOfWorkFour = sut.getTypeOfWork(volunteerClaimFourCount, 4).get();

        assertThat(typeOfWorkOne, is(TypeOfWork.PAID));
        assertThat(typeOfWorkTwo, is(TypeOfWork.VOLUNTARY));
        assertThat(typeOfWorkThree, is(TypeOfWork.PAID));
        assertThat(typeOfWorkFour, is(TypeOfWork.VOLUNTARY));
    }

    @Test
    public void removeFrom_shouldRemoveWhenRemoveIsTrue() {
        boolean remove = true;
        String claimId = "claim_id";
        List<String> identifiersToRemove = Arrays.asList("identifier");
        int count = 1;
        String guardIdentifier = "guard_id";
        String countIdentifier = "count_id";
        int maxTo = 4;
        when(mockClaimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        sut.removeFrom(remove, claimId, identifiersToRemove, count, guardIdentifier, countIdentifier, maxTo);
        verify(mockClaimRepository, times(2)).save(mockClaim);
    }

    @Test
    public void removeFrom_shouldNotRemoveWhenRemoveIsFalse() {
        boolean remove = false;
        String claimId = "claim_id";
        List<String> identifiersToRemove = Arrays.asList("identifier");
        int count = 1;
        String guardIdentifier = "guard_id";
        String countIdentifier = "count_id";
        int maxTo = 4;

        sut.removeFrom(remove, claimId, identifiersToRemove, count, guardIdentifier, countIdentifier, maxTo);
        verify(mockClaimRepository, times(0)).save(mockClaim);
    }

    private static Claim generateClaimWithTypeOfWorkAndCount(Integer count, TypeOfWork typeOfWork) {
        Claim claim = new Claim();
        TypeOfWorkQuestion typeOfWorkOption = new TypeOfWorkQuestion();
        typeOfWorkOption.setUserSelectionValue(typeOfWork);
        Step step = new Step(WorkPaidOrVoluntaryController.IDENTIFIER,
                "/form/current-work/details/"+ count +"/choose-payment",
                "/form/current-work/details/"+count+"/how-often-paid",
                Section.CURRENT_WORK);
        StepInstance stepInstance = new StepInstance(step, count,
                false, false, false);
        claim.save(stepInstance, typeOfWorkOption, Optional.empty());
        return claim;
    }

    class TestCounterFormController extends CounterFormController<TestForm> {

        public TestCounterFormController(final ClaimRepository claimRepository,
                                         final String viewName, final String modelName,
                                         final RoutingService routingService,
                                         final String identifier, final String nextStepIdentifier,
                                         final String alternateStepIdentifier) {
            super(claimRepository, viewName, modelName, routingService, identifier, nextStepIdentifier,
                    alternateStepIdentifier, Section.NONE);
        }

        public String getMapping(final Model model, final String claimId, final Integer counter,
                                 final HttpServletRequest request) {
            return get(model, claimId, request, counter);
        }

        public String postMapping(final String claimId,
                                  final TestForm form, final BindingResult bindingResult,
                                  final HttpServletResponse response, final Model model) {
            return post(claimId, form, bindingResult, response, model);
        }

        @Override
        public TestForm createNewForm(final Claim claim, final int counter) {
            return new TestForm();
        }

        @Override
        public void loadForm(final ClaimDB claimDB, final TestForm form) {

        }
    }

    class TestForm extends AbstractCounterForm {
        @Override
        public void setQuestion(Question question) {
            CounterFormControllerTest.this.mockQuestion = question;
        }

        @Override
        public Question getQuestion() {
            return CounterFormControllerTest.this.mockQuestion;
        }
    }
}
