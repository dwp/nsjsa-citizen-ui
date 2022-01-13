package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm.MULTIPLE_OPTIONS_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months.APRIL;

@RunWith(MockitoJUnitRunner.class)
public class PensionIncreaseDateControllerTest {

    public static final String BACK_REF = "/form/pensions/current/details/did-pension-increase";
    public static final String IDENTIFIER = "form/pensions/current/details/increase-date";
    private PensionIncreaseDateController sut;

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

    @Mock private Claim mockClaim;

    private static final int count = 1;

    @Mock
    private PensionIncreaseMultipleOptionsForm mockForm;
    @Mock
    private Step step;

    @Mock private PensionIncreaseMonthQuestion mockQuestion;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new PensionIncreaseDateController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
    }

    @Test
    public void multipleOptionsSpecificAttributesIsSetCorrectly() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setDefaultOption(Months.valueOf(LocalDate.now().getMonth().name()));
        verify(mockForm).setInline(false);
    }

    @Test
    public void getFormReturnsMultipleOptionsForm() {
        Form form = sut.getForm();

        assertThat(form, is(new PensionIncreaseMultipleOptionsForm(new PensionIncreaseMonthQuestion(), APRIL)));
    }

    @Test
    public void backRefIsSetToAreYouWorkingWhenCountIsOne() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn(BACK_REF);
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setBackRef(BACK_REF);
    }

    @Test
    public void getTranslationKeyReturnsExpectedValue() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setTranslationKey("pensions.current.month.");
    }

    @Test
    public void getPensionIncreaseDateReturnsExpectedView() {
        String view = sut.get(mockModel, CLAIM_ID, mockRequest, count);

        assertThat(view, is(MULTIPLE_OPTIONS_VIEW_NAME));
    }

    @Test
    public void postPensionIncreaseDateReturnsExpectedView() {
        PensionIncreaseMultipleOptionsForm form = new PensionIncreaseMultipleOptionsForm(
                new PensionIncreaseMonthQuestion(), APRIL);
        form.setCount(1);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/pensions/current/details/1/increase-frequency");
        String view = sut.postIsWorkPaid(form,
                mockBindingResult, CLAIM_ID, mockHttpServletResponse, mockModel);

        assertThat(view, is("redirect:/form/pensions/current/details/1/increase-frequency"));
    }

    @Test
    public void GivenNoPreviousAnswer_CreateNewForm_ReturnsEmptyForm() {
        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.empty());
        MultipleOptionsForm actual = sut.createNewForm(mockClaim,1);
        Assert.assertThat(actual.getQuestion(), notNullValue());
    }

    @Test
    public void GivenPreviousAnswer_CreateNewForm_ReturnsFormWithPreviousAnswer() {
        Optional<Question> optionalQuestion = Optional.of(new PensionIncreaseMonthQuestion());
        when(mockClaim.get(any(StepInstance.class))).thenReturn(optionalQuestion);

        MultipleOptionsForm actual = sut.createNewForm(mockClaim,1);
        assertEquals(actual.getQuestion(), optionalQuestion.get());
    }

}
