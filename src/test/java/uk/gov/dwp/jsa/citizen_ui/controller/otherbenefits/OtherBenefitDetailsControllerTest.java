package uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.OtherBenefit;
import uk.gov.dwp.jsa.citizen_ui.controller.editors.OtherBenefitDetailsQuestionEditor;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.OtherBenefits;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DefaultStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitTestHelper.addOtherBenefit;

@RunWith(MockitoJUnitRunner.class)
public class OtherBenefitDetailsControllerTest {

    private static final String CLAIM_ID = "2";
    public static final String IDENTIFIER = "form/other-benefits/details";

    private OtherBenefitDetailsController otherBenefitDetailsController;
    @Mock
    private ClaimRepository mockClaimRepository;
    @Mock
    private Model mockModel;
    @Mock
    private Claim mockClaim;
    @Mock
    private OtherBenefits mockOtherBenefits;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private WebDataBinder binder;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private Step step;
    @Mock
    private StepInstance stepInstance;
    private static final String OTHER_BENEFIT_DETAILS = "Extra Working Benefits";

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        otherBenefitDetailsController = new OtherBenefitDetailsController(mockClaimRepository, mockRoutingService);
    }

    @Test
    public void getOtherBenefitDetailsReturnsExpectedView() {
        String view = otherBenefitDetailsController.getOtherBenefitDetails(mockModel, CLAIM_ID, mockRequest);

        assertThat(view, is("form/common/text-area"));
    }

    @Test
    public void createNewFormUpdatesOtherDetailsFromClaimObj() {
        String otherBenefitDetails = "Extra Working Benefits";
        DefaultStringQuestion benefitDetailsQuestion = new DefaultStringQuestion();
        benefitDetailsQuestion.setValue(otherBenefitDetails);

        when(mockClaim.get(any(StepInstance.class))).thenReturn(Optional.of(benefitDetailsQuestion));

        StringForm returnForm = otherBenefitDetailsController.createNewForm(mockClaim);

        assertThat(returnForm.getStringQuestion().getValue(), is(otherBenefitDetails));
    }

    @Test
    public void updateClaimSetsValueFromFormSuccessfully() {
        StringForm form = new StringForm();
        DefaultStringQuestion stringQuestion = new DefaultStringQuestion();
        stringQuestion.setValue(OTHER_BENEFIT_DETAILS);
        form.setQuestion(stringQuestion);

        otherBenefitDetailsController.updateClaim(form, mockClaim, stepInstance, Optional.empty());

        ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
        verify(mockClaim, times(1)).save(any(StepInstance.class), questionCaptor.capture(), any(Optional.class));
        assertThat(questionCaptor.getValue(), is(stringQuestion));
    }

    @Test
    public void getFormReturnsStringForm() {
        Form form = otherBenefitDetailsController.getForm();
        assertEquals(form.getClass(), StringForm.class);
    }

    @Test
    public void dataBinderShouldBindDefaultEditor() {
        otherBenefitDetailsController.dataBinding(binder);
        verify(binder).registerCustomEditor(eq(StringQuestion.class), any(OtherBenefitDetailsQuestionEditor.class));
    }


    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        OtherBenefit otherBenefit = addOtherBenefit(claimDB);
        final String name = "name";
        otherBenefit.setDescription(name);

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());

        otherBenefitDetailsController.loadForm(claimDB, form);

        assertEquals("Should match", name, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());

        otherBenefitDetailsController.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }
}
