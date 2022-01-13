package uk.gov.dwp.jsa.citizen_ui.controller.pensions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;

@Controller
@RequestMapping("/form/pensions")
public class MaxPensionController {

    public static final String MAX_PENSIONS_VIEW = "form/pensions/max-pensions";
    public static final String CURRENT_PENSIONS_WARNING_TRANSLATION_KEY = "pensions.current.warning.max.";

    @GetMapping("max-current-pensions")
    public final String getMaxCurrentPensionsWarning(
            @RequestParam("backUrl") final String backUrl,
            final Model model
    ) {
        setModelAttributes(model, backUrl, CURRENT_PENSIONS_WARNING_TRANSLATION_KEY);
        return MAX_PENSIONS_VIEW;
    }

    private void setModelAttributes(final Model model, final String backUrl, final String translationKey) {
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("nextUrl", "/" + EducationConfirmationController.IDENTIFIER);
        model.addAttribute("translationKey", translationKey);
    }


}
