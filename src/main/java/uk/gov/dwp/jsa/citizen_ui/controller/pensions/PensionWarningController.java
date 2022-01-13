package uk.gov.dwp.jsa.citizen_ui.controller.pensions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/form/pensions/warning")
public class PensionWarningController  {

    public static final String REMOVE_VIEW = "form/warning/remove";
    public static final String QUESTION = "common.pension.warning.question";

    @GetMapping("/{type}/{count}/remove")
    public final String remove(
            @PathVariable("type") final String type,
            @PathVariable("count") final Integer count,
            final Model model) {
        addAttributes(type, count, model);
        return REMOVE_VIEW;
    }

    private void addAttributes(@PathVariable("type") final String type,
                               @PathVariable("count") final Integer count,
                               final Model model) {
        model.addAttribute("type", type);
        model.addAttribute("count", count);
        model.addAttribute("question", QUESTION);
        model.addAttribute("hrefForRemove", "/form/pensions/" + type + "/" + count + "/remove");
        model.addAttribute("backRef", "/form/summary");
    }
}
