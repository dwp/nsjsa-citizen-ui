package uk.gov.dwp.jsa.citizen_ui.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class BeanWrapperHelperTests {
    
    private BeanWrapperHelper sut;
    
    @Before
    public void before() {
    }
    
    @Test
    public void getIntPropertyValue_returnsCorrectValue() {
        
        Integer expected = 142;
        String fieldName = "field1";
        sut = createSut();
        
        FakeIntEntity target = new FakeIntEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expected);
        
        Integer actual = sut.getIntPropertyValue(fieldName, fakeBeanWrapper);
        
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
    
    @Test
    public void givenMissingField_getIntPropertyValue_returnsNull() {
        
        Integer expected = 14;
        String fieldName = "field1";
        String missedField = "field3";
        sut = createSut();

        // Require this entity as it is not possible to map the forBeanPropertyAccess method
        FakeIntEntity target = new FakeIntEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expected);
        
        Integer actual = sut.getIntPropertyValue(missedField, fakeBeanWrapper);
        
        assertNull(actual);
    }
    
    @Test
    public void givenInvalidFormatField_getIntPropertyValue_returnsNull() {
        
        String expectedString = "text";
        String fieldName = "field1";
        sut = createSut();

        // We require this entity as it is not possible to map the forBeanPropertyAccess method
        FakeStringEntity target = new FakeStringEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expectedString);
        
        Integer actual = sut.getIntPropertyValue(fieldName, fakeBeanWrapper);
        
        assertNull(actual);
    }

    @Test
    public void getDateQuestionPropertyValue_returnsCorrectValue() {

        DateQuestion expected = new DateQuestion();
        expected.setYear(1);
        expected.setMonth(2);
        expected.setDay(3);
        String fieldName = "field2";
        sut = createSut();

        FakeIntEntity target = new FakeIntEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expected);

        DateQuestion actual = sut.getDateQuestionPropertyValue(fieldName, fakeBeanWrapper);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void givenMissingField_getDateQuestionPropertyValue_returnsNull() {

        DateQuestion expected = new DateQuestion();
        expected.setYear(1);
        expected.setMonth(2);
        expected.setDay(3);
        String fieldName = "field2";
        String missedField = "field3";
        sut = createSut();

        // Require this entity as it is not possible to map the forBeanPropertyAccess method
        FakeIntEntity target = new FakeIntEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expected);

        DateQuestion actual = sut.getDateQuestionPropertyValue(missedField, fakeBeanWrapper);

        assertNull(actual);
    }

    @Test
    public void givenInvalidFormatField_getDateQuestionPropertyValue_returnsNull() {

        String expectedString = "text";
        String fieldName = "field1";
        sut = createSut();

        // We require this entity as it is not possible to map the forBeanPropertyAccess method
        FakeStringEntity target = new FakeStringEntity();
        BeanWrapper fakeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        fakeBeanWrapper.setPropertyValue(fieldName, expectedString);

        DateQuestion actual = sut.getDateQuestionPropertyValue(fieldName, fakeBeanWrapper);

        assertNull(actual);
    }
    
    public class FakeStringEntity {
        
        private String field1;

        private String field2;
        
        public String getField1() {
            return field1;
        }
        
        public void setField1(String value) {
            field1 = value; 
        }

        public String getField2() {
            return field2;
        }

        public void setField2(final String field2) {
            this.field2 = field2;
        }
    }
    
    public class FakeIntEntity {
        
        public Integer field1;

        public DateQuestion field2;
        
        public Integer getField1() {
            return field1;
        }
        
        public void setField1(Integer value) {
            field1 = value; 
        }

        public DateQuestion getField2() {
            return field2;
        }

        public void setField2(final DateQuestion field2) {
            this.field2 = field2;
        }
    }
    
    private BeanWrapperHelper createSut() {
        return new BeanWrapperHelper();
    }
}
