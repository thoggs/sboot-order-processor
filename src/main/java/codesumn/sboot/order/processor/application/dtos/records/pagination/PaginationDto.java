package codesumn.sboot.order.processor.application.dtos.records.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record PaginationDto(
        @JsonProperty("currentPage") int page,
        @JsonProperty("itemsPerPage") int size,
        @JsonProperty("totalItems") long totalElements,
        @JsonProperty("totalPages") int totalPages
) implements Serializable {

    @JsonCreator
    public PaginationDto {
    }
}
