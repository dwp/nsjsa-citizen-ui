package uk.gov.dwp.jsa.citizen_ui.model.form.education;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EducationCourseHoursConstraint;
import javax.validation.constraints.Max;
import java.math.BigDecimal;

import static uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursForm.MAX_COURSE_HOURS;

/**
 * Q23 Education course hours question.
 */
public class EducationCourseHoursQuestion implements Question {

    @Max(value = MAX_COURSE_HOURS, message = "education.coursehours.length.invalid")
    @EducationCourseHoursConstraint
    private BigDecimal courseHours;

    public EducationCourseHoursQuestion() {
    }

    public EducationCourseHoursQuestion(final BigDecimal courseHours) {
        this.courseHours = courseHours;
    }

    public BigDecimal getCourseHours() {
        return courseHours;
    }

    public void setCourseHours(final BigDecimal courseHours) {
        this.courseHours = courseHours;
    }

    public String getValue() {
        return courseHours.toString();
    }
}
