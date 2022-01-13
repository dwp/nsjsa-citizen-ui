package uk.gov.dwp.jsa.citizen_ui.controller.education;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Education;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.controller.education.EducationTestHelper.addEducation;

@RunWith(MockitoJUnitRunner.class)
public class EducationPlaceControllerTest {
    public static final String IDENTIFIER = "form/education/place";
    @Mock
    private Model mockModel;

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claim mockClaim;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StringForm mockForm;

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private RoutingService routingService;
    @Mock
    private Step step;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private EducationPlaceController sut;

    private String place = "Open University";

    @Before
    public void setUp() {
        sut = new EducationPlaceController(mockClaimRepository, routingService);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(routingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
    }

    @Test
    public void getEducationPlaceForm() {
        String viewName = sut.getView(mockModel, claimId, mockRequest);
        assertEquals("form/common/text", viewName);
    }

    @Test
    public void SubmitEducationPlaceForm_WithPlace_ReturnsTheCorrectUrl() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockForm.getStringQuestion().getValue()).thenReturn(place);
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(routingService.getNext(any())).thenReturn("/form/education/how-many-hours");

        String result = sut.submitForm(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("redirect:/form/education/how-many-hours", result);

        verify(mockClaim).save(any(StepInstance.class), any(Question.class), any(Optional.class));
        verify(mockClaimRepository, atLeastOnce()).save(mockClaim);
    }

    @Test
    public void SubmitEducationPlaceForm_WithErrors_ReturnsEducationPlaceForm() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String result = sut.submitForm(mockForm, mockBindingResult, claimId, mockResponse, mockModel);

        assertEquals("form/common/text", result);
    }

    @Test
    public void testLoadDataShouldAssignDataToForm() {
        ClaimDB claimDB = new ClaimDB();
        Education education = addEducation(claimDB);
        final String name = "name";
        education.setInstitutionName(name);

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());

        sut.loadForm(claimDB, form);

        assertEquals("Should match", name, form.getQuestion().getValue());
    }

    @Test
    public void testLoadDataShouldNotAssignDataToFormWhenMissing() {

        ClaimDB claimDB = new ClaimDB();

        final StringForm form = new StringForm();
        form.setQuestion(new StringQuestion());

        sut.loadForm(claimDB, form);

        assertNull("Should be null", form.getQuestion().getValue());
    }
}
