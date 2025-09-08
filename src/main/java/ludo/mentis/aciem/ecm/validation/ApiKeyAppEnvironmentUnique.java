package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Validate that there is no other ApiKey with the same (applicationId, environment) pair.
 * This is a class-level constraint because it depends on two fields together.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ApiKeyAppEnvironmentUnique.ApiKeyAppEnvironmentUniqueValidator.class
)
public @interface ApiKeyAppEnvironmentUnique {

    String message() default "There is already an API Key for the selected Application and Environment.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ApiKeyAppEnvironmentUniqueValidator implements ConstraintValidator<ApiKeyAppEnvironmentUnique, ApiKeyDTO> {

        private final ApiKeyService apiKeyService;
        private final ApiKeyRepository apiKeyRepository;
        private final HttpServletRequest request;

        public ApiKeyAppEnvironmentUniqueValidator(final ApiKeyService apiKeyService,
                                                   final ApiKeyRepository apiKeyRepository,
                                                   final HttpServletRequest request) {
            this.apiKeyService = apiKeyService;
            this.apiKeyRepository = apiKeyRepository;
            this.request = request;
        }

        @Override
        public boolean isValid(final ApiKeyDTO dto, final ConstraintValidatorContext context) {
            if (dto == null) {
                return true;
            }
            if (dto.getApplicationId() == null || dto.getEnvironment() == null) {
                // Let @NotNull on fields handle missing values
                return true;
            }

            Long appId = dto.getApplicationId();
            Long envId = dto.getEnvironment().getId();

            // If editing and the pair hasn't changed, it's valid
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables != null) {
                final String currentId = pathVariables.get("id");
                if (currentId != null) {
                    try {
                        final var existing = apiKeyService.get(Long.parseLong(currentId));
                        if (existing != null
                                && existing.getApplicationId() != null
                                && existing.getEnvironment() != null
                                && existing.getApplicationId().equals(appId)
                                && existing.getEnvironment().getId() == envId) {
                            // Pair unchanged
                            return true;
                        }
                    } catch (Exception ignored) {
                        // If not found or parse error, fall back to existence check
                    }
                }
            }

            return !apiKeyRepository.existsByApplicationIdAndEnvironmentId(appId, envId);
        }
    }
}
