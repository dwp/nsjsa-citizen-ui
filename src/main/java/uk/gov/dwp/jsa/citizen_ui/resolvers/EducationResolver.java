package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Education;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseDurationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseHoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationPlaceController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class EducationResolver implements Resolver {

    private static final List<String> EDUCATION_IDENTIFIERS =
            Arrays.asList(EducationPlaceController.IDENTIFIER,
                    EducationCourseDurationController.IDENTIFIER,
                    EducationCourseNameController.IDENTIFIER,
                    EducationCourseHoursController.IDENTIFIER);

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {

        boolean hasEducationQuestion = new QuestionValueExtractor().getBooleanValueWithIdentifier(
                claim, EducationConfirmationController.IDENTIFIER, 0
        );
        if (hasEducationQuestion) {
            Education educationAnswer = new Education();
            EDUCATION_IDENTIFIERS
                    .forEach(id -> claim.get(id).ifPresent(q -> fillAnswerToEducation(id, q, educationAnswer)));
            circumstances.setEducation(educationAnswer);
        }
    }

    private void fillAnswerToEducation(final String id,
                                       final Question question,
                                       final Education education) {
        switch (id) {
            case EducationCourseNameController.IDENTIFIER:
                if (question instanceof StringQuestion) {
                    education.setCourseName(((StringQuestion) question).getValue());
                }
                break;
            case EducationPlaceController.IDENTIFIER:
                if (question instanceof StringQuestion) {
                    education.setInstitutionName(((StringQuestion) question).getValue());
                }
                break;
            case EducationCourseDurationController.IDENTIFIER:
                if (question instanceof DateRangeQuestion) {
                    education.setStartDate(dateQuestionToLocalDate(((DateRangeQuestion) question).getStartDate()));
                    education.setEndDate(dateQuestionToLocalDate(((DateRangeQuestion) question).getEndDate()));
                }
                break;
            case EducationCourseHoursController.IDENTIFIER:
                if (question instanceof EducationCourseHoursQuestion) {
                    education.setHoursPerWeek(((EducationCourseHoursQuestion) question).getCourseHours().doubleValue());
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported controller with idntifier %s", id));
        }
    }

    private LocalDate dateQuestionToLocalDate(final DateQuestion dateQuestion) {
        return LocalDate.of(
                dateQuestion.getYear(),
                dateQuestion.getMonth(),
                dateQuestion.getDay());
    }
}
