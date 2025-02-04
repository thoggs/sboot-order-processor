package codesumn.sboot.order.processor.application.dtos.records.response;


import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;
import codesumn.sboot.order.processor.application.dtos.records.metadata.MetadataRecordDto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record ResponseDto<T>(
        T data,
        Boolean success,
        List<MetadataRecordDto> metadata
) implements Serializable {

    public static <T> ResponseDto<T> create(T data, List<MetadataRecordDto> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            metadata = Collections.singletonList(
                    new MetadataRecordDto(
                            List.of(new ErrorMessageDto("INFO", "Operation completed successfully.", null))
                    )
            );
        }
        return new ResponseDto<>(data, true, metadata);
    }

    public static <T> ResponseDto<T> create(T data) {
        return create(data, null);
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "data=" + data +
                ", success=" + success +
                ", metadata=" + metadata +
                '}';
    }
}