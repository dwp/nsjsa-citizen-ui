package uk.gov.dwp.jsa.citizen_ui.model.form;


import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.VoluntaryPaidController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;

import java.util.List;

public class SummaryForm extends AbstractForm {

    private List<ViewQuestion> questions;

    private boolean morePensionsThanMaxAllowed;

    private boolean canAddPension;

    public SummaryForm(final List<ViewQuestion> questions, final boolean hasMoreThanMaxAllowed,
                       final boolean canAddPension) {
        this.questions = questions;
        this.morePensionsThanMaxAllowed = hasMoreThanMaxAllowed;
        this.canAddPension = canAddPension;
    }

    public ViewQuestion get(final String identifier) {
        return questions.stream().filter(q -> identifier.equals(q.getIdentifier())).findFirst()
                .orElse(ViewQuestion.emptyQuestion());
    }

    public ViewQuestion get(final String identifier, final Integer counter) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(counter, "counter");
        return questions.stream().filter(q -> identifier.equals(q.getIdentifier()) && counter.equals(q.getCounter()))
                .findFirst()
                .orElse(ViewQuestion.emptyQuestion());
    }

    @Override
    public Question getQuestion() {
        return null;
    }

    @Override
    public void setQuestion(final Question question) {
        //Not applicable
    }


    private boolean isPaid(final int counter) {
        ViewQuestion isPaidViewQuestion = get(WorkPaidOrVoluntaryController.IDENTIFIER, counter);
        TypeOfWorkQuestion typeOfWorkQuestion = (TypeOfWorkQuestion) isPaidViewQuestion.getQuestion();

        return typeOfWorkQuestion.getUserSelectionValue() == TypeOfWork.PAID;
    }

    public boolean isVoluntary(final int counter) {
        ViewQuestion isPaidViewQuestion = get(WorkPaidOrVoluntaryController.IDENTIFIER, counter);
        TypeOfWorkQuestion typeOfWorkQuestion = (TypeOfWorkQuestion) isPaidViewQuestion.getQuestion();

        return typeOfWorkQuestion.getUserSelectionValue() == TypeOfWork.VOLUNTARY;
    }

    private boolean isPaidVoluntary(final int counter) {
        ViewQuestion isPaidVoluntaryViewQuestion = get(VoluntaryPaidController.IDENTIFIER, counter);
        GuardQuestion isPaidVoluntaryQuestion = (GuardQuestion) isPaidVoluntaryViewQuestion.getQuestion();

        return (!isPaidVoluntaryViewQuestion.isEmpty() && isPaidVoluntaryQuestion.getChoice());
    }

    public boolean isPaidOrPaidVoluntary(final int counter) {
        return (isPaid(counter) || isPaidVoluntary(counter));
    }

    public boolean isMorePensionsThanMaxAllowed() {
        return morePensionsThanMaxAllowed;
    }

    public boolean isCanAddPension() {
        return canAddPension;
    }
}
