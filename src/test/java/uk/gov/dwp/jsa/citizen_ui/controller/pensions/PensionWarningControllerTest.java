package uk.gov.dwp.jsa.citizen_ui.controller.pensions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PensionWarningControllerTest {

    private PensionWarningController sut;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        sut = new PensionWarningController();
    }

    @Test
    public void getPensionsWarningRemoveView() {
        String viewName = sut.remove("deferred", 1, model);

        verify(model).addAttribute("type", "deferred");
        verify(model).addAttribute("count", 1);
        verify(model).addAttribute("question", PensionWarningController.QUESTION);
        verify(model).addAttribute("hrefForRemove", "/form/pensions/deferred/1/remove");
        verify(model).addAttribute("backRef", "/form/summary");
        Assert.assertThat(viewName, is(PensionWarningController.REMOVE_VIEW));
    }
}
