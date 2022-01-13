package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class UnableToWorkDueToIllnessForm extends AbstractForm<UnableToWorkDueToIllnessQuestion> {

    @Valid
    private UnableToWorkDueToIllnessQuestion unableToWorkDueToIllnessQuestion;

    public UnableToWorkDueToIllnessForm(final UnableToWorkDueToIllnessQuestion unableToWorkDueToIllnessQuestion) {
        this.unableToWorkDueToIllnessQuestion = unableToWorkDueToIllnessQuestion;
    }

    public UnableToWorkDueToIllnessForm() { }

    @Override
    public UnableToWorkDueToIllnessQuestion getQuestion() {
        return getUnableToWorkDueToIllnessQuestion();
    }

    public UnableToWorkDueToIllnessQuestion getUnableToWorkDueToIllnessQuestion() {
        return unableToWorkDueToIllnessQuestion;
    }

    @Override
    public void setQuestion(final UnableToWorkDueToIllnessQuestion booleanAndDateFieldQuestions) {
        setUnableToWorkDueToIllnessQuestion(booleanAndDateFieldQuestions);
    }

    public void setUnableToWorkDueToIllnessQuestion(final UnableToWorkDueToIllnessQuestion unableToWorkDueToIllnessQuestion) {
        this.unableToWorkDueToIllnessQuestion = unableToWorkDueToIllnessQuestion;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    @Override
    public boolean hasNoGuard() {
        return true;
    }
}
