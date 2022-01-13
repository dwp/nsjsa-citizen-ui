package uk.gov.dwp.jsa.citizen_ui.model.form.education;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EducationDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Captures claimant's education details.
 */
public class EducationDetails {

    /**
     * Q18 Captures if claimant has been a student in the last 4 years.
     */
    private BooleanQuestion educationConfirmationQuestion;
    /**
     * Q21 Captures the education course name.
     */
    private StringQuestion educationCourseNameQuestion;
    /**
     * Q23 Captures the number of hours in education.
     */
    private EducationCourseHoursQuestion educationCourseHoursQuestion;

    private DateRangeQuestion courseDateRangeQuestion;
    /**
     * Q22 Where did your education take place.
     */
    private StringQuestion educationPlaceQuestion;

    public EducationDetails(
            final BooleanQuestion educationConfirmationQuestion,
            final StringQuestion educationCourseNameQuestion,
            final DateRangeQuestion courseDateRangeQuestion,
            final StringQuestion educationPlaceQuestion) {
        this.educationConfirmationQuestion = educationConfirmationQuestion;
        this.educationCourseNameQuestion = educationCourseNameQuestion;
        this.courseDateRangeQuestion = courseDateRangeQuestion;
        this.educationPlaceQuestion = educationPlaceQuestion;
    }

    public EducationDetails() {
    }

    public BooleanQuestion getEducationConfirmationQuestion() {
        if (educationConfirmationQuestion == null) {
            this.educationConfirmationQuestion =
                    new BooleanQuestion();
        }
        return educationConfirmationQuestion;
    }

    public void setEducationConfirmationQuestion(
            final BooleanQuestion educationConfirmationQuestion) {
        this.educationConfirmationQuestion = educationConfirmationQuestion;
    }

    public void setCourseDateRangeQuestion(final DateRangeQuestion courseDateRangeQuestion) {
        this.courseDateRangeQuestion = courseDateRangeQuestion;
    }
    public DateRangeQuestion getCourseDateRangeQuestion() {
        if (courseDateRangeQuestion == null) {
            this.courseDateRangeQuestion = new EducationDurationQuestion();
        }
        return courseDateRangeQuestion;
    }

    public StringQuestion getEducationCourseNameQuestion() {
        if (educationCourseNameQuestion == null) {
            educationCourseNameQuestion = new StringQuestion();
        }
        return educationCourseNameQuestion;
    }

    public void setEducationCourseNameQuestion(final StringQuestion educationCourseNameQuestion) {
        this.educationCourseNameQuestion = educationCourseNameQuestion;
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

    public StringQuestion getEducationPlaceQuestion() {
        if (educationPlaceQuestion == null) {
            educationPlaceQuestion = new StringQuestion();
        }
        return educationPlaceQuestion;
    }

    public void setEducationPlaceQuestion(final StringQuestion educationPlaceQuestion) {
        this.educationPlaceQuestion = educationPlaceQuestion;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

}
