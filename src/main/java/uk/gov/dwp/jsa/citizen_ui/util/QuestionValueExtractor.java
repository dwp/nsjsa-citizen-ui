package uk.gov.dwp.jsa.citizen_ui.util;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.Address;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
public class QuestionValueExtractor {

    public String getStringQuestionValueWithIdentifier(final Claim claim, final String identifier, final int counter) {
        Optional<Question> question = getQuestion(claim, identifier, counter);
        if (question.isPresent() && question.get() instanceof StringQuestion) {
            return ((StringQuestion) question.get()).getValue();
        } else {
            return EMPTY;
        }
    }

    public boolean getBooleanValueWithIdentifier(final Claim claim, final String identifier,
                                                 final int counter) {
        Optional<Question> question = getQuestion(claim, identifier, counter);
        if (question.isPresent() && question.get() instanceof BooleanQuestion) {
            return ((BooleanQuestion) question.get()).getChoice();
        } else {
            return false;
        }
    }

    public Address getEmployerAddressQuestionValueWithIdentifier(final Claim claim, final String identifier,
                                                                 final int counter) {
        Address address = new Address();
        Optional<Question> question = getQuestion(claim, identifier, counter);
        if (question.isPresent() && question.get() instanceof EmployersAddressQuestion) {
            EmployersAddressQuestion addressQuestion = (EmployersAddressQuestion) question.get();
            address.setFirstLine(addressQuestion.getAddressLine1());
            address.setSecondLine(addressQuestion.getAddressLine2());
            address.setTown(addressQuestion.getTownOrCity());
            address.setPostCode(addressQuestion.getPostCode());
        }
        return address;
    }

    public Address extractProviderAddress(final Claim claim, final String identifier,
                                          final int counter) {
        Address address = new Address();
        Optional<Question> question = getQuestion(claim, identifier, counter);
        if (question.isPresent() && question.get() instanceof ProvidersAddressQuestion) {
            ProvidersAddressQuestion addressQuestion = (ProvidersAddressQuestion) question.get();
            address.setFirstLine(addressQuestion.getAddressLine1());
            address.setSecondLine(addressQuestion.getAddressLine2());
            address.setTown(addressQuestion.getTownOrCity());
            address.setPostCode(addressQuestion.getPostCode());
        }
        return address;
    }

    public boolean getLoopEndQuestionValue(final Claim claim, final String identifier) {
        Optional<Question> moreThanMaxQuestionOptional = claim.get(identifier);
        if (moreThanMaxQuestionOptional.isPresent()
                && moreThanMaxQuestionOptional.get() instanceof LoopEndBooleanQuestion) {
            LoopEndBooleanQuestion moreThanMaxQuestion = (LoopEndBooleanQuestion) moreThanMaxQuestionOptional.get();
            return moreThanMaxQuestion.getHasMoreThanLimit();
        }
        return false;
    }

    private Optional<Question> getQuestion(final Claim claim, final String identifier, final int counter) {
        Optional<Question> question;
        if (counter > 0) {
            question = claim.get(identifier, counter);
        } else {
            question = claim.get(identifier);
        }
        return question;
    }
}
