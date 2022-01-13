package uk.gov.dwp.jsa.citizen_ui.controller.previousemployment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HasPreviousWorkControllerTest {
    private static final String COOKIE = "12345";
    public static final String IDENTIFIER = "form/previous-employment/has-previous-work";
    @Mock private Model mockModel;
    @Mock private BindingResult mockBindingResult;
    @Mock private ClaimRepository mockClaimRepository;
    @Mock private HttpServletResponse mockHttpServletResponse;
    @Mock private HttpServletRequest mockRequest;
    @Mock private CookieLocaleResolver mockCookieLocalResolver;

    @Mock private BooleanQuestion mockHasPreviousWorkQuestion;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private Step step;

    private HasPreviousWorkController hasPreviousWorkController;
    private GuardForm hasPreviousForm;

    @Before
    public void createSut() {
        hasPreviousWorkController = new HasPreviousWorkController(mockClaimRepository, mockRoutingService,
                mockCookieLocalResolver);
        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        hasPreviousForm = new GuardForm();
        hasPreviousForm.setQuestion(mockHasPreviousWorkQuestion);
        when(mockRoutingService.getStep(IDENTIFIER)).thenReturn(Optional.of(step));
        when(step.getIdentifier()).thenReturn("TEST_IDENTIFIER");
        when(mockCookieLocalResolver.resolveLocale(any())).thenReturn(new Locale("en"));
    }

    @Test
    public void verifyCorrectViewPath() {
        String viewPath = hasPreviousWorkController.getView(null, mockModel, mockRequest);
        assertThat(viewPath, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        verify(mockClaimRepository).save(Mockito.any(Claim.class));
    }

    @Test
    public void SubmitFormInWelsh_withErrors_modelContainsCorrectWelshYesText() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockCookieLocalResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/previous-employment/has-previous-work");
        String viewPath = hasPreviousWorkController.setHasPreviousWork(
                COOKIE, hasPreviousForm, mockBindingResult, mockRequest, mockHttpServletResponse, mockModel
        );
        assertEquals("form/common/boolean", viewPath);
        verify(mockModel, times(1)).addAttribute("alternativeWelshTextYES",
                "common.question.yesno.choice.true.alternative.ydw");
    }

    @Test
    public void getFormInWelsh_ReturnsFormWithCorrectWelshYesText() {
        when(mockCookieLocalResolver.resolveLocale(any())).thenReturn(new Locale("cy"));
        when(mockRequest.getServletPath()).thenReturn("/form/pensions/deferred/has-another-pension");

        String viewPath = hasPreviousWorkController.getView(null, mockModel, mockRequest);

        assertThat(viewPath, is("form/common/boolean"));
        verify(mockModel).addAttribute(eq("form"), any(GuardForm.class));
        verify(mockClaimRepository).save(Mockito.any(Claim.class));
    }

    @Test
    public void errors_in_the_binding_should_return_error_view() {
        when(mockBindingResult.hasErrors()).thenReturn(true);
        String viewPath = hasPreviousWorkController.setHasPreviousWork(
            COOKIE, hasPreviousForm, mockBindingResult, mockRequest, mockHttpServletResponse, mockModel
        );
        assertThat(viewPath, is("form/common/boolean"));
    }

    @Test
    public void verify_view_path_when_form_question_is_true() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockHasPreviousWorkQuestion.getChoice()).thenReturn(true);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/previous-employment/employer-details/1/dates");

        String viewPath = hasPreviousWorkController.setHasPreviousWork(
            COOKIE, hasPreviousForm, mockBindingResult, mockRequest, mockHttpServletResponse, mockModel
        );
        assertThat(viewPath, is("redirect:/form/previous-employment/employer-details/1/dates"));
    }

    @Test
    public void verify_view_path_when_form_question_is_false() {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockHasPreviousWorkQuestion.getChoice()).thenReturn(false);
        when(mockRoutingService.getNext(any(StepInstance.class)))
                .thenReturn("/form/summary");

        String viewPath = hasPreviousWorkController.setHasPreviousWork(
            COOKIE, hasPreviousForm, mockBindingResult, mockRequest, mockHttpServletResponse, mockModel
        );
        assertThat(viewPath, is("redirect:/form/summary"));
    }
}
