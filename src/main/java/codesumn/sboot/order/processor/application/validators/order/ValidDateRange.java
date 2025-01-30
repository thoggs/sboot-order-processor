package codesumn.sboot.order.processor.application.validators.order;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "startDate and endDate must be both present or both absent.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
