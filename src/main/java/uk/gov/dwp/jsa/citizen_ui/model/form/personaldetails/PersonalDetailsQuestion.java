package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.PersonalDetailsConstraint;

import javax.validation.Valid;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

@PersonalDetailsConstraint
public class PersonalDetailsQuestion implements Question {

    /**
     * Q7 Title Question Response.
     */
    private TitleQuestion titleQuestion;
    /**
     * Q5 First Name Question Response.
     */
    private NameStringTruncatedQuestion firstNameQuestion;
    /**
     * Q4 Last Name Question Response.
     */
    private NameStringTruncatedQuestion lastNameQuestion;

    public PersonalDetailsQuestion() {
    }

    public PersonalDetailsQuestion(
            @Valid final TitleQuestion titleQuestion,
            @Valid final NameStringTruncatedQuestion firstNameQuestion,
            @Valid final NameStringTruncatedQuestion lastNameQuestion
    ) {
        this.titleQuestion = titleQuestion;
        this.firstNameQuestion = firstNameQuestion;
        this.lastNameQuestion = lastNameQuestion;
    }

    public TitleQuestion getTitleQuestion() {
        return titleQuestion;
    }

    public void setTitleQuestion(final TitleQuestion titleQuestion) {
        this.titleQuestion = titleQuestion;
    }

    public NameStringTruncatedQuestion getFirstNameQuestion() {
        return firstNameQuestion;
    }

    public void setFirstNameQuestion(final NameStringTruncatedQuestion firstNameQuestion) {
        this.firstNameQuestion = firstNameQuestion;
    }

    public NameStringTruncatedQuestion getLastNameQuestion() {
        return lastNameQuestion;
    }

    public void setLastNameQuestion(final NameStringTruncatedQuestion lastNameQuestion) {
        this.lastNameQuestion = lastNameQuestion;
    }

    public List<TitleEnum> getTitleValues() {
        return asList(TitleEnum.values());
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

}
