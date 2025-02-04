package codesumn.sboot.order.processor.application.validators.order;

import codesumn.sboot.order.processor.application.dtos.param.FilterCriteriaParamDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, FilterCriteriaParamDto> {

    public boolean isValid(FilterCriteriaParamDto value, ConstraintValidatorContext context) {
        boolean startDatePresent = value.getStartDate() != null;
        boolean endDatePresent = value.getEndDate() != null;

        context.disableDefaultConstraintViolation();

        if (startDatePresent && !endDatePresent) {
            context.buildConstraintViolationWithTemplate("endDate must be provided if startDate is present")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        if (!startDatePresent && endDatePresent) {
            context.buildConstraintViolationWithTemplate("startDate must be provided if endDate is present")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
