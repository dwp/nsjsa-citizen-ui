package uk.gov.dwp.jsa.citizen_ui.controller.pensions;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MaxPensionControllerTest {


    private MaxPensionController sut;

    @Mock
    private Model model;

    @Before
    public void setUp() throws Exception {
        sut = new MaxPensionController();
    }

    @Test
    public void getMaxCurrentPensionsWarning() {

        String viewName = sut.getMaxCurrentPensionsWarning("/form/backlink", model);

        verify(model).addAttribute("backUrl", "/form/backlink");
        verify(model).addAttribute("nextUrl", "/" + EducationConfirmationController.IDENTIFIER);
        verify(model).addAttribute("translationKey", MaxPensionController.CURRENT_PENSIONS_WARNING_TRANSLATION_KEY);

        Assert.assertThat(viewName, CoreMatchers.is(MaxPensionController.MAX_PENSIONS_VIEW));
    }
/*
    @Test
    public void getMaxDeferredPensionsWarning() {

        String viewName = sut.getMaxDeferredPensionsWarning("/form/backlink", model);

        verify(model).addAttribute("backUrl", "/form/backlink");
        verify(model).addAttribute("nextUrl", "/" + EducationConfirmationController.IDENTIFIER);
        verify(model).addAttribute("translationKey", MaxPensionController.DEFERRED_PENSIONS_WARNING_TRANSLATION_KEY);

        Assert.assertThat(viewName, CoreMatchers.is(MaxPensionController.MAX_PENSIONS_VIEW));
    }
 */
}
