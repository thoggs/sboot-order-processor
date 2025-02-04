package codesumn.sboot.order.processor.application.dtos.param;

import codesumn.sboot.order.processor.application.validators.order.ValidDateRange;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidDateRange
public class FilterCriteriaParamDto {

    private String searchTerm;
    private String role;
    private String sortField;
    private Boolean sortDescending;

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer pageSize = 10;

    private LocalDate startDate;
    private LocalDate endDate;
}
