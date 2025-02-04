package codesumn.sboot.order.processor.application.dtos.records.pagination;

import java.io.Serializable;

public record PaginationDto(
        int currentPage,
        int itemsPerPage,
        long totalItems,
        int totalPages
) implements Serializable {
}
