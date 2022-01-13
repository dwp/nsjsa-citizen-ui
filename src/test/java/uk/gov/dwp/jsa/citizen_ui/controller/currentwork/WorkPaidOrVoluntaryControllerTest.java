package uk.gov.dwp.jsa.citizen_ui.controller.currentwork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.CurrentWork;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.CurrentWorkDetails;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.currentwork.CurrentWorkTestHelper.addCurrentWork;
import static uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork.PAID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork.VOLUNTARY;

@RunWith(MockitoJUnitRunner.class)
public class WorkPaidOrVoluntaryControllerTest {

    public static final String BACK_REF = "/form/current-work/1/has-another-job";
    public static final String IDENTIFIER = "form/current-work/details/is-work-paid";
    private WorkPaidOrVoluntaryController sut;

    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private HttpServletResponse mockHttpServletResponse;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private RoutingService mockRoutingService;

    private static final String CLAIM_ID = "1234566";

    @Mock
    private Claim mockClaim;

    private static final int count = 1;

    @Mock
    private MultipleOptionsForm mockForm;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;

    @Mock
    private MultipleOptionsQuestion mockQuestion;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new WorkPaidOrVoluntaryController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void multipleOptionsSpecificAttributesIsSetCorrectly() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setOptions(asList(TypeOfWork.values()));
        verify(mockForm).setDefaultOption(PAID);
        verify(mockForm).setInline(true);
    }

    @Test
    public void getFormReturnsMultipleOptionsForm() {
        Form form = sut.getForm();

        assertThat(form, is(new MultipleOptionsForm(new TypeOfWorkQuestion(), PAID)));
    }

    @Test
    public void backRefIsSetToAreYouWorkingWhenCountIsOne() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn(BACK_REF);
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setBackRef(BACK_REF);
    }

    @Test
    public void backRefIsSetToHasAnotherCurrentJobWhenCountIsTwo() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn(BACK_REF);
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setBackRef(BACK_REF);
    }

    @Test
    public void getTranslationKeyReturnsExpectedValue() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setTranslationKey("current.work.is.paid.");
    }

    @Test
    public void getAreYouWorkingReturnsExpectedView() {
        String view = sut.getIsWorkPaid(count, mockModel, CLAIM_ID, mockRequest);

        assertThat(view, is("form/common/multiple-options"));
    }

    @Test
    public void postAreYouWorkingReturnsExpectedView() {
        MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = new MultipleOptionsForm<>(
                new TypeOfWorkQuestion(), PAID);
        form.setCount(1);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/current-work/details/1/choose-payment");
        String view = sut.postIsWorkPaid(form,
                mockBindingResult, CLAIM_ID, mockHttpServletResponse, mockModel);

        assertThat(view, is("redirect:/form/current-work/details/1/choose-payment"));
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        MultipleOptionsForm actual = sut.createNewForm(mockClaim, 1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(new TypeOfWorkQuestion());
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        MultipleOptionsForm actual = sut.createNewForm(mockClaim, 1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

    @Test
    public void updateClaim_setsCorrectQuestion() {
        when(mockForm.getQuestion()).thenReturn(mockQuestion);

        ArgumentCaptor<MultipleOptionsQuestion> questionCaptor =
                ArgumentCaptor.forClass(MultipleOptionsQuestion.class);
        sut.updateClaim(mockForm, mockClaim, stepInstance, Optional.empty());

        verify(mockClaim).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        MatcherAssert.assertThat(questionCaptor.getValue(), Is.is(mockQuestion));
    }


    @Test
    public void testLoadDataShouldAssignDataToFormPaid() {
        ClaimDB claimDB = new ClaimDB();
        uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setPaid(true);
        currentWork.setVoluntary(false);

        final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should be true", TypeOfWork.PAID, form.getQuestion().getUserSelectionValue());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormVoluntary() {
        ClaimDB claimDB = new ClaimDB();
        uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setPaid(false);
        currentWork.setVoluntary(true);

        final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertEquals("Should be true", TypeOfWork.VOLUNTARY, form.getQuestion().getUserSelectionValue());
    }

    @Test
    public void testLoadDataShouldAssignDataToFormWhenCounter2() {
        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setPaid(true);
        currentWork.setVoluntary(false);

        final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = sut.getForm();
        form.setCount(2);

        sut.loadForm(claimDB, form);

        assertEquals("Should be true", TypeOfWork.PAID, form.getQuestion().getUserSelectionValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getUserSelectionValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenCounterNotValid() {

        ClaimDB claimDB = new ClaimDB();
        addCurrentWork(claimDB);
        uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork currentWork = addCurrentWork(claimDB);
        currentWork.setPaid(true);
        currentWork.setVoluntary(false);

        final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form = sut.getForm();
        form.setCount(1);

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getUserSelectionValue());
    }

    private MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> givenFormValueIsPaid() {
        TypeOfWorkQuestion question = new TypeOfWorkQuestion();
        question.setUserSelectionValue(PAID);
        return new MultipleOptionsForm<>(question, PAID);
    }

    private void givenTypeOfWorkIsVoluntary() {
        mockClaim = new Claim();
        CurrentWorkDetails currentWorkDetails = new CurrentWorkDetails();
        TypeOfWorkQuestion question = new TypeOfWorkQuestion();
        question.setUserSelectionValue(VOLUNTARY);
        currentWorkDetails.getVoluntaryDetails().setWorkPaidOrVoluntary(question);
        CurrentWork currentWork = new CurrentWork();
        currentWork.setCurrentWorkDetailsList(asList(currentWorkDetails));
        mockClaim.setCurrentWork(currentWork);
    }

}
