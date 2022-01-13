package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseDurationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseHoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationPlaceController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class EducationSummaryMappingService implements MappingService {


    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        questions.add(ViewQuestion.guardQuestion(EducationConfirmationController.IDENTIFIER, claim));
        questions.add(singleQuestion(EducationCourseNameController.IDENTIFIER, claim));
        questions.add(singleQuestion(EducationPlaceController.IDENTIFIER, claim));
        questions.add(singleQuestion(EducationCourseHoursController.IDENTIFIER, claim));
        questions.add(singleQuestion(EducationCourseDurationController.IDENTIFIER, claim));

    }

}
