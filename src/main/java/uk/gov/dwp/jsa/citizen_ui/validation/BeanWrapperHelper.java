package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

@Component
class BeanWrapperHelper {

    public Integer getIntPropertyValue(
            final String fieldName,
            final BeanWrapper beanWrapper) {

        Assert.notNull(fieldName, "fieldName");
        Assert.notNull(beanWrapper, "beanWrapper");

        Class<?> type = beanWrapper.getPropertyType(fieldName);

        if ((type != null) && Integer.class.isAssignableFrom(type)) {
            return (Integer) beanWrapper.getPropertyValue(fieldName);
        }

        return null;
    }

    public DateQuestion getDateQuestionPropertyValue(
            final String fieldName,
            final BeanWrapper beanWrapper) {

        Assert.notNull(fieldName, "fieldName");
        Assert.notNull(beanWrapper, "beanWrapper");

        Class<?> type = beanWrapper.getPropertyType(fieldName);

        if ((type != null) && DateQuestion.class.isAssignableFrom(type)) {
            return (DateQuestion) beanWrapper.getPropertyValue(fieldName);
        }

        return null;
    }
}
