package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.PeriodConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PeriodValidator
        implements ConstraintValidator<PeriodConstraint, Object>, Validator {
    private PropertyAccessorFactoryWrapper propertyAccessorFactoryWrapper;
    private BeanWrapperHelper beanWrapperHelper;
    private String startDateField;
    private String endDateField;
    private String message;

    PeriodValidator(
            final PropertyAccessorFactoryWrapper propertyAccessorFactoryWrapper,
            final BeanWrapperHelper beanWrapperHelper) {
        this.propertyAccessorFactoryWrapper = propertyAccessorFactoryWrapper;
        this.beanWrapperHelper = beanWrapperHelper;
    }


    @Override
    public void initialize(final PeriodConstraint constraint) {
        Assert.notNull(constraint, "constraintAnnotation");

        startDateField = constraint.startDateField();
        endDateField = constraint.endDateField();
        message = constraint.message();
    }

    @Override
    public boolean isValid(final Object form, final ConstraintValidatorContext context) {
        Assert.notNull(form, "form");
        Assert.notNull(context, "context");
        Assert.state(startDateField != null, "dayField");
        Assert.state(endDateField != null, "monthField");

        BeanWrapper beanWrapper = propertyAccessorFactoryWrapper.forBeanPropertyAccess(form);
        DateQuestion startDateQuestion = beanWrapperHelper.getDateQuestionPropertyValue(startDateField, beanWrapper);
        DateQuestion endDateQuestion = beanWrapperHelper.getDateQuestionPropertyValue(endDateField, beanWrapper);

        LocalDate startDate = LocalDate.of(startDateQuestion.getYear(), startDateQuestion.getMonth(),
                startDateQuestion.getDay());

        LocalDate endDate = LocalDate.of(endDateQuestion.getYear(), endDateQuestion.getMonth(),
                endDateQuestion.getDay());
        if (startDate.isAfter(endDate)) {
            return addInvalidMessage(context, message, startDateField);
        }

        return true;
    }
}
