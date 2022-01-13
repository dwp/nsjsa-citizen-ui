package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.CounterFormBackUrlRule;

import java.util.Objects;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SLASH;

public final class ViewQuestion {

    private String identifier;
    private String url;
    private EditMode editMode;
    private Integer counter;
    private Question question;
    private boolean isEmpty;

    private ViewQuestion(final String identifier, final String url,
                         final EditMode editMode, final Integer counter,
                         final Question question, final boolean isEmpty) {
        this.identifier = identifier;
        this.url = url;
        this.editMode = editMode;
        this.counter = counter;
        this.question = question;
        this.isEmpty = isEmpty;
    }

    public static ViewQuestion singleQuestion(final String identifier,
                                              final Claim claim) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(claim, "claim");
        String url = SLASH + identifier;
        Question question = claim.get(identifier).orElse(null);
        return new ViewQuestion(identifier, url, EditMode.SINGLE, null, question, Objects.isNull(question));
    }

    public static ViewQuestion singleQuestion(final String identifier,
                                              final Claim claim,
                                              final EditMode editMode) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(claim, "claim");
        String url = SLASH + identifier;
        Question question = claim.get(identifier).orElse(null);
        return new ViewQuestion(identifier, url, editMode, null, question, Objects.isNull(question));
    }

    public static ViewQuestion bankDetailsQuestion(final String identifier,
                                                   final Claim claim) {
        final ViewQuestion viewQuestion = singleQuestion(identifier, claim);
        final BankAccountQuestion bankAccountQuestion = (BankAccountQuestion) viewQuestion.getQuestion();
        viewQuestion.isEmpty = bankAccountQuestion == null || bankAccountQuestion.isEmpty();
        return viewQuestion;
    }

    public static ViewQuestion guardQuestion(final String identifier,
                                             final Claim claim) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(claim, "claim");
        String url = SLASH + identifier;
        Question question = claim.get(identifier).orElse(null);
        return new ViewQuestion(identifier, url, EditMode.SECTION, null, question, Objects.isNull(question));
    }

    public static ViewQuestion guardLoopQuestion(final String identifier,
                                                 final Claim claim, final Integer counter) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(claim, "claim");
        String url = SLASH + identifier;
        Question question = claim.get(identifier).orElse(null);
        return new ViewQuestion(identifier, url, EditMode.SECTION, counter, question, Objects.isNull(question));
    }

    public static ViewQuestion loopQuestion(final String identifier,
                                            final Question question, final Integer counter,
                                            final EditMode editMode) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(counter, "counter");
        Assert.isTrue(counter > 0, "counter less equals 0");

        String url = CounterFormBackUrlRule.addCounterToUrl(counter, identifier);

        return new ViewQuestion(identifier, url,
                editMode, counter,
                question, Objects.isNull(question));
    }

    public static ViewQuestion emptyQuestion() {
        return new ViewQuestion(null, null,
                null, null,
                null, true);
    }

    public static ViewQuestion emptyQuestion(final String identifier) {
        return new ViewQuestion(identifier, null, EditMode.NONE, null, null, true);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public Integer getCounter() {
        return counter;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
