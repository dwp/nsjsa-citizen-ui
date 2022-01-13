package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class HaveYouTravelledOutsideForm extends AbstractForm<HaveYouTravelledOutsideQuestion> {

    @Valid
    private HaveYouTravelledOutsideQuestion haveYouTravelledOutsideQuestion;

    public HaveYouTravelledOutsideForm(final HaveYouTravelledOutsideQuestion haveYouTravelledOutsideQuestion) {
        this.haveYouTravelledOutsideQuestion = haveYouTravelledOutsideQuestion;
    }

    public HaveYouTravelledOutsideForm() {
    }

    @Override
    public HaveYouTravelledOutsideQuestion getQuestion() {
        return getHaveYouTravelledOutsideQuestion();
    }

    public HaveYouTravelledOutsideQuestion getHaveYouTravelledOutsideQuestion() {
        return haveYouTravelledOutsideQuestion;
    }

    @Override
    public void setQuestion(final HaveYouTravelledOutsideQuestion booleanAndDateFieldQuestions) {
        setHaveYouTravelledOutsideQuestion(booleanAndDateFieldQuestions);
    }

    public void setHaveYouTravelledOutsideQuestion(final HaveYouTravelledOutsideQuestion haveYouTravelledOutsideQuestion) {
        this.haveYouTravelledOutsideQuestion = haveYouTravelledOutsideQuestion;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    @Override
    public boolean hasNoGuard() {
        return true;
    }
}
