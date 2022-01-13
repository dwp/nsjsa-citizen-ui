package uk.gov.dwp.jsa.citizen_ui.model.form.education;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;

public class EducationCourseHoursForm extends AbstractForm<EducationCourseHoursQuestion> {

    public static final int MAX_COURSE_HOURS = 60;

    /**
     * Q23 Education course hours question response.
     */
    @Valid
    private EducationCourseHoursQuestion educationCourseHoursQuestion;

    public EducationCourseHoursForm() {
    }

    public EducationCourseHoursForm(final EducationCourseHoursQuestion educationCourseHoursQuestion) {
        this.educationCourseHoursQuestion = educationCourseHoursQuestion;
    }

    public EducationCourseHoursQuestion getEducationCourseHoursQuestion() {
        if (educationCourseHoursQuestion == null) {
            educationCourseHoursQuestion = new EducationCourseHoursQuestion();
        }
        return educationCourseHoursQuestion;
    }

    public void setEducationCourseHoursQuestion(final EducationCourseHoursQuestion educationCourseHoursQuestion) {
        this.educationCourseHoursQuestion = educationCourseHoursQuestion;
    }

    public EducationCourseHoursQuestion getQuestion() {
        return getEducationCourseHoursQuestion();
    }

    public void setQuestion(final EducationCourseHoursQuestion question) {
        setEducationCourseHoursQuestion(question);
    }
}
