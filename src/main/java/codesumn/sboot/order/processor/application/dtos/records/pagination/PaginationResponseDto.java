package codesumn.sboot.order.processor.application.dtos.records.pagination;

import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;
import codesumn.sboot.order.processor.application.dtos.records.metadata.MetadataPaginationRecordDto;

import java.io.Serializable;
import java.util.List;

public record PaginationResponseDto<T>(
        T data,
        Boolean success,
        MetadataPaginationRecordDto metadata
) implements Serializable {

    public static <T> PaginationResponseDto<T> create(T data, MetadataPaginationRecordDto metadata) {
        if (metadata == null || metadata.messages().isEmpty()) {
            assert metadata != null;
            metadata = new MetadataPaginationRecordDto(
                    metadata.pagination(),
                    List.of(new ErrorMessageDto("INFO", "Operation completed successfully.", null))
            );
        }
        return new PaginationResponseDto<>(data, true, metadata);
    }

    @Override
    public String toString() {
        return "PaginationResponseDto{" +
                "data=" + data +
                ", success=" + success +
                ", metadata=" + metadata +
                '}';
    }
}