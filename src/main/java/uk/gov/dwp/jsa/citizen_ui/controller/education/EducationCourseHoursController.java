package uk.gov.dwp.jsa.citizen_ui.controller.education;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
public class EducationCourseHoursController extends BaseFormController<EducationCourseHoursForm> {

    public static final String IDENTIFIER = "form/education/course-hours";
    private static final String TYPE_MISMATCH_ERROR = "typeMismatch";
    private static final String PROPERTY_STRING = "education.coursehours.invalid";

    public EducationCourseHoursController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository, "form/education/course-hours",
                "educationCourseHoursForm",
                routingService,
                IDENTIFIER,
                "/form/education/course-duration",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.EDUCATION);
    }

    @GetMapping("/form/education/course-hours")
    public final String getView(final Model model,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @PostMapping("/form/education/course-hours")
    public String submitCourseHours(@Valid final EducationCourseHoursForm educationCourseHoursForm,
                                    final BindingResult bindingResult,
                                    @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                    final HttpServletResponse response,
                                    final Model model) {
        setModelAttributesIfInvalidCourseHours(bindingResult, model, PROPERTY_STRING);
        return post(claimId, educationCourseHoursForm, bindingResult, response, model);
    }

    @Override
    public EducationCourseHoursForm getForm() {
        EducationCourseHoursForm form = new EducationCourseHoursForm();
        EducationCourseHoursQuestion question = new EducationCourseHoursQuestion();
        form.setEducationCourseHoursQuestion(question);
        return form;
    }

    @Override
    public EducationCourseHoursForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final EducationCourseHoursForm form) {
        resolve(() -> claimDB.getCircumstances().getEducation().getHoursPerWeek()).ifPresent(hours -> {
            form.getEducationCourseHoursQuestion().setCourseHours(BigDecimal.valueOf(hours));
        });
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "False Positive")
    public void setModelAttributesIfInvalidCourseHours(final BindingResult bindingResult, final Model model,
                                                            final String propertyString) {
        if (bindingResult != null) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                List<String> errorCodes = Arrays.asList(Objects.requireNonNull(error.getCodes()));
                if (errorCodes.contains(TYPE_MISMATCH_ERROR)) {
                    model.addAttribute("isTypeMismatchErrorPresent", true);
                    model.addAttribute("invalidCharsLocale", propertyString);
                }
            }
        }
    }
}
