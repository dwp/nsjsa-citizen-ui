package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class ClaimStartDateFormTests {

    private ClaimStartDateForm sut;
    
    @Mock
    private ClaimStartDateQuestion mockClaimStartDateQuestionResponse;
    
    @Before
    public void before() throws IOException {
    }
    
    @Test
    public void constructorInitializesDateToNull() {

        sut = createSut();
        ClaimStartDateQuestion actual = sut.getClaimStartDateQuestion();
        
        assertThat(actual, is(nullValue()));
    }
    
    @Test
    public void getClaimDate_returnsClaimDate() {
        
        sut = createSut();
        sut.setClaimStartDateQuestion(mockClaimStartDateQuestionResponse);

        ClaimStartDateQuestion actual = sut.getClaimStartDateQuestion();
        
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(equalTo(mockClaimStartDateQuestionResponse)));
    }
    
    @Test(expected = NullPointerException.class)
    public void setClaimDate_wontAcceptNull() {
        sut.setClaimStartDateQuestion(null);
    }

    private ClaimStartDateForm createSut() {
        return new ClaimStartDateForm();
    }
}
