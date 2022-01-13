package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

@Component
class PropertyAccessorFactoryWrapper {

    public BeanWrapper forBeanPropertyAccess(final Object value) {
        return PropertyAccessorFactory.forBeanPropertyAccess(value);
    }
}
