package uk.gov.dwp.jsa.citizen_ui.controller.eligibility;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionsAnswer;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionsAnswer.NO;
import static uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ContributionsAnswer.YES;

@RunWith(MockitoJUnitRunner.class)
public class ContributionsControllerTest {

    public static final String IDENTIFIER = "form/eligibility/contributions";
    private ContributionsController sut;

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

    private Claim claim = new Claim();

    @Mock
    private MultipleOptionsForm mockForm;
    @Mock
    private Step step;

    @Before
    public void setUp() {
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        sut = new ContributionsController(mockClaimRepository, mockRoutingService);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void multipleOptionsSpecificAttributesIsSetCorrectly() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setOptions(asList(ContributionsAnswer.values()));
        verify(mockForm).setDefaultOption(NO);
        verify(mockForm).setInline(false);
    }

    @Test
    public void getFormReturnsMultipleOptionsForm() {
        Form form = sut.getForm();

        assertThat(form, is(new MultipleOptionsForm(new ContributionQuestion(), NO)));
    }

    @Test
    public void backRefIsSetCorrectlyOnForm() {
        when(mockRoutingService.getBackRef(CLAIM_ID)).thenReturn("/form/eligibility/seeking-work");
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setBackRef("/form/eligibility/seeking-work");
    }

    @Test
    public void getTranslationKeyReturnsExpectedValue() {
        sut.setFormAttrs(mockForm, CLAIM_ID);

        verify(mockForm).setTranslationKey("eligibility.contributions.form.");
    }

    @Test
    public void getContributionsReturnsExpectedView() {
        String view = sut.getContributions(mockModel, CLAIM_ID, mockRequest);

        assertThat(view, is("form/common/multiple-options"));
    }

    @Test
    public void postContributionsReturnsExpectedView() {
        MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form = new MultipleOptionsForm<>(
                new ContributionQuestion(), YES);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/eligibility/eligible");
        String view = sut.postContributions(form,
                                            mockBindingResult, CLAIM_ID, mockHttpServletResponse, mockModel);

        assertThat(view, is("redirect:/form/eligibility/eligible"));
    }

    @Test
    public void createNewFormUpdatesValueFromClaim() {
        givenContributionsIsYes();

        MultipleOptionsForm newForm = sut.createNewForm(claim);

        assertThat(newForm.getMultipleOptionsQuestion().getUserSelectionValue(), is(YES));
    }

    @Test
    public void updateClaimUpdatesClaimObjWithFormValues() {
        MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> form = givenFormValueIsPaid();

        sut.updateClaim(form, claim, null, Optional.empty());

        assertThat(claim.getContributionsQuestion().getUserSelectionValue(), is(YES));
    }

    @Test(expected = ClassCastException.class)
    public void updateClaimDoesNotUpdateClaimObjWithFormValues() {
        sut.updateClaim(mockForm, claim, null, Optional.empty());
    }

    private MultipleOptionsForm<ContributionQuestion, ContributionsAnswer> givenFormValueIsPaid() {
        ContributionQuestion question = new ContributionQuestion();
        question.setUserSelectionValue(YES);
        return new MultipleOptionsForm<>(question, YES);
    }

    private void givenContributionsIsYes() {
        claim = new Claim();
        ContributionQuestion question = new ContributionQuestion();
        question.setUserSelectionValue(YES);
        claim.setContributionsQuestion(question);
    }
}
