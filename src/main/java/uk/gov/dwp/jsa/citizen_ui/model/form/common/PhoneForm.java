package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.primitives.Booleans.asList;

public class PhoneForm extends AbstractForm<PhoneQuestion> {

    public PhoneForm(final PhoneQuestion question) {
        this.phoneQuestion = question;
    }

    public PhoneForm() {

    }

    @Valid
    private PhoneQuestion phoneQuestion;

    @Override
    public PhoneQuestion getQuestion() {
        return getPhoneQuestion();
    }

    public PhoneQuestion getPhoneQuestion() {
        return phoneQuestion;
    }

    @Override
    public void setQuestion(final PhoneQuestion phoneQuestion) {
        setPhoneQuestion(phoneQuestion);
    }

    public void setPhoneQuestion(final PhoneQuestion phoneQuestion) {
        this.phoneQuestion = phoneQuestion;
    }

    public List<Boolean> radioOptions() {
        return asList(true, false);
    }

    @Override
    public boolean hasNoGuard() {
        return true;
    }

}
