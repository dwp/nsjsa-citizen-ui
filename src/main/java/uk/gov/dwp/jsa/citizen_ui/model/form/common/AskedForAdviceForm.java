package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.AskedForAdviceQuestion;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class AskedForAdviceForm extends AbstractForm<AskedForAdviceQuestion> {
    @Valid
    private AskedForAdviceQuestion askedForAdviceQuestion;

    public AskedForAdviceForm(final AskedForAdviceQuestion question) {
        this.askedForAdviceQuestion = question;
    }
    public AskedForAdviceForm() {

    }

    @Override
    public AskedForAdviceQuestion getQuestion() {
        return getAskedForAdviceQuestion();
    }

    public AskedForAdviceQuestion getAskedForAdviceQuestion() {
        return askedForAdviceQuestion;
    }

    @Override
    public void setQuestion(final AskedForAdviceQuestion askedForAdviceQuestion) {
        setAskedForAdviceQuestion(askedForAdviceQuestion);
    }

    public void setAskedForAdviceQuestion(final AskedForAdviceQuestion askedForAdviceQuestion) {
        this.askedForAdviceQuestion = askedForAdviceQuestion;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    @Override
    public boolean hasNoGuard() {
        return true;
    }
}
