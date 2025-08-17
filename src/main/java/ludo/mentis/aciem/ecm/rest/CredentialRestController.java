package ludo.mentis.aciem.ecm.rest;

import jakarta.validation.Valid;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.PasswordRequest;
import ludo.mentis.aciem.ecm.model.PasswordResponse;
import ludo.mentis.aciem.ecm.service.CredentialRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/v1/credential")
@Tag(name = "Credentials", description = "Endpoints for retrieving credentials/passwords")
public class CredentialRestController {

    private final CredentialRestService credentialRestService;
    private final Logger logger = LoggerFactory.getLogger(CredentialRestController.class);

    public CredentialRestController(CredentialRestService credentialRestService) {
        this.credentialRestService = credentialRestService;
    }

    @GetMapping
    @Operation(
            summary = "Get credential password",
            description = "Retrieves a password for a given application code, environment, credential type, and username. " +
                    "Provide the parameters as query string values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Credential not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResponse.class))
            )
    })
    public ResponseEntity<PasswordResponse> getPassword(final @Valid @ParameterObject PasswordRequest passwordRequest, HttpServletRequest request) {
        logGetPassword(passwordRequest, request);

        final var credential = credentialRestService.getPassword(passwordRequest);
        if (credential.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final var response = new PasswordResponse(credential.get(), null, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void logGetPassword(PasswordRequest passwordRequest, HttpServletRequest request) {
        logger.info("Password request from IP: {}, Host: {}, Parameters: appCode={}, environment={}, credentialType={}, username={}",
                request.getRemoteAddr(), request.getRemoteHost(),
                passwordRequest.appCode(), passwordRequest.environment(),
                passwordRequest.credentialType(), passwordRequest.username());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PasswordResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        final var response = new PasswordResponse(null, null, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<PasswordResponse> handleNotFoundException(NotFoundException ex) {
        final var response = new PasswordResponse(null, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PasswordResponse> handleException(Exception ex) {
        logger.error("Internal error on CredentialRestController", ex);
        final var response = new PasswordResponse(null, "Internal server error. Check server logs for details.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
