package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.gov.dwp.jsa.citizen_ui.controller.eligibility.EligibleController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ActivelyLookingWorkForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ActivelyLookingWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AgeForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.AreYouWorkingQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.eligibility.ResidenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityControllerTest {

    private EligibleController sut;

    @Before
    public void setUp() {
        sut = new EligibleController();
    }

    @Test
    public void getEligibleForm_returnsCorrectView() {
        String path = sut.getEligibleForm();

        assertThat(path, is("form/eligibility/eligible"));
    }
}
