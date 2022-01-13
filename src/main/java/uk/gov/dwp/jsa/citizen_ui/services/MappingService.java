package uk.gov.dwp.jsa.citizen_ui.services;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.List;
import java.util.stream.IntStream;

import static uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion.loopQuestion;

public interface MappingService {
    void map(Claim claim, List<ViewQuestion> questions);

    default void addLoopQuestion(final Claim claim,
                                 final List<ViewQuestion> questions,
                                 final String identifier, final int limit, final EditMode editMode) {

        IntStream.range(1, limit + 1)
                .forEach(idx -> questions.add(
                        loopQuestion(identifier, claim.get(identifier, idx).orElse(null), idx, editMode)
                ));
    }
}
