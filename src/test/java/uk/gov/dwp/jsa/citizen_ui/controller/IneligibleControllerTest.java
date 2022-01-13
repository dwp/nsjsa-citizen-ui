package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IneligibleControllerTest {

    private IneligibleController ineligibleController = new IneligibleController();
    @Mock
    private Model mockModel;

    @Test
    public void getGeneralIneligible_notUkResident_returnsGenericViewWithExpectedModelProperties() {
        String view = ineligibleController.getGeneralIneligible("residence", mockModel);
        verifyIneligibleReasonAndTitleForresidence();
        assertThat(view, is("form/eligibility/ineligible-generic"));
    }

    @Test
    public void getGeneralIneligible_working_returnsGenericViewWithExpectedModelProperties() {
        String view = ineligibleController.getGeneralIneligible("working", mockModel);
        verifyIneligibleReasonAndTitleForWorking();
        assertThat(view, is("form/eligibility/ineligible-generic"));
    }

    @Test
    public void getGeneralIneligible_workingLessThan16Hours_returnsGenericViewWithExpectedModelProperties() {
        String view = ineligibleController.getGeneralIneligible("working-over", mockModel);
        verifyIneligibleReasonAndTitleForWorkingOver();
        assertThat(view, is("form/eligibility/ineligible-generic"));
    }


    @Test
    public void getGeneralIneligible_residence_returnsGenericViewWithExpectedModelProperties() {
        String view = ineligibleController.getGeneralIneligible("residence", mockModel);
        verifyIneligibleReasonAndTitleForresidence();
        assertThat(view, is("form/eligibility/ineligible-generic"));
    }

    private void verifyIneligibleReasonAndTitleForWorking() {
        verify(mockModel).addAttribute("ineligibleReason","working");
        verify(mockModel).addAttribute("title", "ineligible.general.heading1.text");
        verify(mockModel).addAttribute("backUrl", "/form/eligibility/working");
        verify(mockModel).addAttribute("nextUrl", "/form/eligibility/ineligible/apply");
    }

    private void verifyIneligibleReasonAndTitleForWorkingOver() {
        verify(mockModel).addAttribute("ineligibleReason","working-over");
        verify(mockModel).addAttribute("title", "ineligible.general.heading1.text");
        verify(mockModel).addAttribute("backUrl", "/form/eligibility/working-over");
        verify(mockModel).addAttribute("nextUrl", "/form/eligibility/ineligible/apply");
    }

    private void verifyIneligibleReasonAndTitleForresidence() {
        verify(mockModel).addAttribute("ineligibleReason","residence");
        verify(mockModel).addAttribute("title", "ineligible.general.heading1.text");
        verify(mockModel).addAttribute("backUrl", "/form/eligibility/residence");
        verify(mockModel).addAttribute("nextUrl", "/form/eligibility/working");
    }
}
