package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.PensionWarningController;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmploymentWarningControllerTest {

    private EmploymentWarningController sut;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        sut = new EmploymentWarningController();
    }

    @Test
    public void getPreviousWorkWarningRemoveView() {
        String viewName = sut.remove("previous", 1, model);

        verify(model).addAttribute("count", 1);
        verify(model).addAttribute("question", "common.work.warning.question");
        verify(model).addAttribute("hrefForRemove", "/form/previous-employment/1/remove-work");
        verify(model).addAttribute("backRef", "/form/summary");
        Assert.assertThat(viewName, is(PensionWarningController.REMOVE_VIEW));
    }

    @Test
    public void getCurrentWorkWarningRemoveView() {
        String viewName = sut.remove("current", 1, model);

        verify(model).addAttribute("count", 1);
        verify(model).addAttribute("question", "common.work.warning.question");
        verify(model).addAttribute("hrefForRemove", "/form/current-work/1/remove-work");
        verify(model).addAttribute("backRef", "/form/summary");
        Assert.assertThat(viewName, is(PensionWarningController.REMOVE_VIEW));
    }
}
