package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork.PAID;

public class MultipleOptionsQuestionTest {

    private MultipleOptionsQuestion<TypeOfWork> multipleOptionsQuestion = new MultipleOptionsQuestion<>();


    @Test
    public void userSelelectionValueIsSetCorrectly(){
        multipleOptionsQuestion.setUserSelectionValue(PAID);

        assertThat(multipleOptionsQuestion.getUserSelectionValue(), is(PAID));
    }

}
