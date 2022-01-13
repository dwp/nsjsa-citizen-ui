package uk.gov.dwp.jsa.citizen_ui.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EducationDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

/**
 * Q24 EducationDetails Dates controller.
 */
@Controller
public class EducationCourseDurationController extends BaseFormController<DateRangeForm> {
    public static final String POST_URL = "/form/education/course-duration";
    public static final String IDENTIFIER = "form/education/course-duration";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.EXAMPLE_DATE_FORMAT);
    private static final int START_MONTH = 9;
    private static final int START_DAY = 1;

    public EducationCourseDurationController(final ClaimRepository claimRepository,
                                             final RoutingService routingService) {
        super(claimRepository,
              "form/common/date-range",
              FORM_NAME,
              routingService,
              IDENTIFIER,
              "/form/summary",
              NO_ALTERNATIVE_IDENTIFIER,
                Section.EDUCATION);
    }

    /**
     * Add example dates in the model.
     *
     * @param model the Spring Boot MVC model
     */
    @ModelAttribute
    public void exampleDates(final Model model) {
        LocalDate now = LocalDate.now();
        model.addAttribute("exampleStartDate", LocalDate.of(now.getYear(), START_MONTH, START_DAY)
                .minusYears(1).format(formatter));
        model.addAttribute("exampleEndDate", now.format(formatter));
    }

    @GetMapping(path = "/form/education/course-duration")
    public final String getEducationForm(final Model model,
                                         @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                         final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @Override
    public String getFormPostUrl() {
        return POST_URL;
    }

    @PostMapping(path = POST_URL)
    public final String submitCourseDurationForm(
            @ModelAttribute(FORM_NAME) @Validated(ValidationSequence.class) final DateRangeForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {
        return post(claimId, form, bindingResult, response, model);
    }

    @Override
    public void setFormAttrs(final DateRangeForm form, final String claimId) {
        super.setFormAttrs(form, claimId);
        form.setTranslationKey("education.courseduration.");
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final DateRangeForm form) {
        resolve(() -> claimDB.getCircumstances().getEducation())
                .ifPresent(education -> {
                    if (nonNull(education.getStartDate())) {
                        form.getDateRange().setStartDate(new DateQuestion(education.getStartDate()));
                    }
                    if (nonNull(education.getEndDate())) {
                        form.getDateRange().setEndDate(new DateQuestion(education.getEndDate()));
                    }
                });
    }


    @Override
    public DateRangeForm getForm() {
        DateRangeQuestion dateRangeQuestion = new EducationDurationQuestion();
        DateRangeForm form = new DateRangeForm();
        form.setCount(1);
        form.setDateRange(dateRangeQuestion);
        return form;
    }

    @Override
    public DateRangeForm getTypedForm() {
        return getForm();
    }

}
