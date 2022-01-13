package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;

import static uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController.IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.singleQuestion;

@Service
public class HasOutsideWorkMappingService implements MappingService {
    @Override
    public void map(final Claim claim, final List<ViewQuestion> questions) {
        ViewQuestion question = singleQuestion(IDENTIFIER, claim);
        questions.add(question);
    }
}
