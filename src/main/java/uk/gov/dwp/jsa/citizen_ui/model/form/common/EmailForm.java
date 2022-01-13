package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class EmailForm extends AbstractForm<EmailStringQuestion> {

    @Valid
    private EmailStringQuestion emailStringQuestion;

    public EmailForm(final EmailStringQuestion question) {
        this.emailStringQuestion = question;
    }

    public EmailForm() {
    }


    @Override
    public EmailStringQuestion getQuestion() {
        return getEmailStringQuestion();
    }

    public EmailStringQuestion getEmailStringQuestion() {
        return emailStringQuestion;
    }

    @Override
    public void setQuestion(final EmailStringQuestion emailStringQuestion) {
        setEmailStringQuestion(emailStringQuestion);
    }

    public void setEmailStringQuestion(final EmailStringQuestion emailStringQuestion) {
        this.emailStringQuestion = emailStringQuestion;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    @Override
    public boolean hasNoGuard() {
        return true;
    }
}
