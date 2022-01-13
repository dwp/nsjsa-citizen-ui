package uk.gov.dwp.jsa.citizen_ui.model.form;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ClaimStartDateQuestionTests {

    private ClaimStartDateQuestion sut;
    
    @Before
    public void before() {  
    }
    
    @Test
    public void constructor_initialisesDayToNotSet() {
        
        sut = createSut();
        
        Integer actual = sut.getDay();
        
        assertThat(actual, is(nullValue()));
    }
    
    @Test
    public void getDay_returnsDay() {
        
        int dummyDay = 15;
        sut = createSut();
        sut.setDay(dummyDay);
        Integer actual = sut.getDay();
        
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(dummyDay));
    }
    
    @Test
    public void constructor_initialisesMonthToNotSet() {
        
        sut = createSut();
        
        Integer actual = sut.getMonth();
        
        assertThat(actual, is(nullValue()));
    }
    
    @Test
    public void getMonth_returnsMonth() {
        
        int dummyMonth = 9;
        sut = createSut();
        sut.setMonth(dummyMonth);
        Integer actual = sut.getMonth();
        
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(dummyMonth));
    }
    
    @Test
    public void constructor_initialisesYearToNotSet() {
        
        sut = createSut();
        
        Integer actual = sut.getYear();
        
        assertThat(actual, is(nullValue()));
    }
    
    @Test
    public void getYear_returnsYear() {
        
        int dummyYear = 2018;
        sut = createSut();
        sut.setYear(dummyYear);
        Integer actual = sut.getYear();
        
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(dummyYear));
    }

    private ClaimStartDateQuestion createSut() {
        return new ClaimStartDateQuestion();
    }
}
