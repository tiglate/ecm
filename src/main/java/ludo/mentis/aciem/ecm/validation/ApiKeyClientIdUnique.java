package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.*;


/**
 * Validate that the name value isn't taken yet.
 */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ApiKeyClientIdUnique.ApiKeyClientIdUniqueValidator.class)
public @interface ApiKeyClientIdUnique {

    String message() default "An API Key with the same Client Id already exists.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ApiKeyClientIdUniqueValidator implements ConstraintValidator<ApiKeyClientIdUnique, String> {

        private final ApiKeyService apiKeyService;
        private final HttpServletRequest request;

        public ApiKeyClientIdUniqueValidator(final ApiKeyService apiKeyService, final HttpServletRequest request) {
            this.apiKeyService = apiKeyService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            final String currentId = (pathVariables != null ? pathVariables.get("id") : null);
            if (currentId != null) {
                try {
                    final var existing = apiKeyService.get(Long.parseLong(currentId));
                    if (existing != null && existing.getClientId() != null && value.equalsIgnoreCase(existing.getClientId())) {
                        // value hasn't changed
                        return true;
                    }
                } catch (Exception ignored) {
                    // if parse fails or service throws, fall back to existence check
                }
            }
            return !apiKeyService.clientIdExists(value);
        }
    }
}