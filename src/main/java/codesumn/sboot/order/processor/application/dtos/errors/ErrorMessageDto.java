package codesumn.sboot.order.processor.application.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDto {
    private String errorCode;
    private String errorMessage;
    private String field;
}
