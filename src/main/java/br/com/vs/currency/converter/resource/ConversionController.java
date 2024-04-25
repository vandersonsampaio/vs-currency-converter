package br.com.vs.currency.converter.resource;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.exception.ErrorResponse;
import br.com.vs.currency.converter.model.exception.SimpleErrorResponse;
import br.com.vs.currency.converter.resource.dto.mapper.ConversionMapper;
import br.com.vs.currency.converter.resource.dto.request.ConversionRequest;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import br.com.vs.currency.converter.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static br.com.vs.currency.converter.utils.Messages.CONVERSION_ID_LENGTH_MESSAGE;
import static br.com.vs.currency.converter.utils.Messages.USER_ID_POSITIVE_MESSAGE;

@Tag(name = "Digital Account", description = "Digital Account management APIs")
@RestController
@RequestMapping("/conversion")
@RequiredArgsConstructor
@Slf4j
public class ConversionController {

    private final ConversionMapper mapper;
    private final TransactionService service;

    @Operation(
            summary = "Retrieve a Conversion Transaction by Id",
            description = "Get a Conversion Transaction object by specifying its Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = ConversionResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404",
                    content = {@Content(schema = @Schema(implementation = SimpleErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConversionResponse> getTransaction(
            @NotBlank @Size(max = 36, min = 36, message = CONVERSION_ID_LENGTH_MESSAGE) @PathVariable String id) {
        log.info("m=getTransaction, id={}", id);

        ConversionResponse response = mapper.from(service.getTransaction(id));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve all User Conversion Transactions by its User ID",
            description = "Find all User Conversion Transaction objects by specifying its User Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = ConversionResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConversionResponse>> findAllByUser(
            @Positive(message = USER_ID_POSITIVE_MESSAGE) @PathVariable Long userId) {
        log.info("m=findAllByUser, userId={}", userId);

        List<ConversionResponse> response = mapper.from(service.findTransactions(userId));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a conversion between different currencies",
            description = "Create a Conversion Transaction object between different currencies.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = {@Content(schema = @Schema(implementation = ConversionResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "424",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConversionResponse> createConversion(@Valid @NotNull @RequestBody ConversionRequest request) {
        log.info("m=createConversion, userId={}", request.getUserId());

        Conversion conversion = mapper.to(request);
        ConversionResponse response = mapper.from(service.converter(conversion));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
