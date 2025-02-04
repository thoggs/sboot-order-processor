package codesumn.sboot.order.processor.shared.parsers;

import codesumn.sboot.order.processor.application.dtos.param.SortingParamDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SortParser {
    private final ObjectMapper objectMapper;

    public SortParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Sort parseSorting(String sorting) throws IOException {
        if (sorting == null || sorting.isEmpty()) {
            return Sort.unsorted();
        }

        List<SortingParamDto> sortingParams = objectMapper.readValue(sorting, new TypeReference<>() {
        });
        List<Sort.Order> orders = sortingParams
                .stream()
                .map(param -> new Sort.Order(param.isDesc()
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC, param.getId()))
                .collect(Collectors.toList());
        return Sort.by(orders);
    }
}

