package codesumn.sboot.order.processor.shared.exceptions.handlers;

import codesumn.sboot.order.processor.application.dtos.errors.ErrorMessageDto;
import codesumn.sboot.order.processor.application.dtos.records.errors.ErrorResponseDto;
import codesumn.sboot.order.processor.application.dtos.records.metadata.MetadataRecordDto;
import codesumn.sboot.order.processor.shared.exceptions.errors.DuplicateOrderException;
import codesumn.sboot.order.processor.shared.exceptions.errors.EnumValidationException;
import codesumn.sboot.order.processor.shared.exceptions.errors.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleAllExceptions(Exception ex) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred: " + ex.getMessage(),
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(
                Collections.singletonList(errorMessage)
        );
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EnumValidationException.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleEnumValidationException(
            EnumValidationException ex
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "ENUM_VALIDATION_ERROR",
                String.format("Invalid value '%s' for enum %s", ex.getInvalidValue(), ex.getEnumName()),
                "role"
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "ILLEGAL_ARGUMENT",
                "Error: " + ex.getMessage(),
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleResourceNotFoundException() {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "RESOURCE_NOT_FOUND",
                "Resource not found in the database",
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "BAD_REQUEST",
                "Failed to convert parameter: " + ex.getName() + " with value: " + ex.getValue(),
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<ErrorResponseDto<List<Object>>> handleDuplicateOrderException(
            DuplicateOrderException ex
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "DUPLICATE_ORDER",
                ex.getMessage(),
                null
        );

        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        List<ErrorMessageDto> errorMessages = new ArrayList<>();

        errorMessages.addAll(ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorMessageDto(
                        "VALIDATION_ERROR",
                        fieldError.getDefaultMessage(),
                        fieldError.getField()
                ))
                .toList());

        errorMessages.addAll(ex
                .getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(globalError -> new ErrorMessageDto(
                        "VALIDATION_ERROR",
                        globalError.getDefaultMessage(),
                        globalError.getObjectName()
                ))
                .toList());

        MetadataRecordDto metadata = new MetadataRecordDto(errorMessages);
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "BAD_REQUEST",
                "Failed to read request: " + ex.getMessage(),
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ErrorMessageDto errorMessage = new ErrorMessageDto(
                "METHOD_NOT_ALLOWED",
                "Method Not Allowed: " + ex.getMessage(),
                null
        );
        MetadataRecordDto metadata = new MetadataRecordDto(Collections.singletonList(errorMessage));
        ErrorResponseDto<List<Object>> errorResponse = ErrorResponseDto
                .createWithoutData(Collections.singletonList(metadata));
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
