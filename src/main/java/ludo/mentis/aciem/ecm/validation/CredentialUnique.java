package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.service.CredentialService;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Validate that there is no other Credential with the same combination of
 * (applicationId, environmentId, credentialTypeId, username, version, enabled).
 * This is a class-level constraint because it depends on multiple fields.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CredentialUnique.CredentialUniqueValidator.class)
public @interface CredentialUnique {

    String message() default "A Credential with the same Application, Environment, Type, Username, Version and Enabled already exists.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CredentialUniqueValidator implements ConstraintValidator<CredentialUnique, CredentialDTO> {

        private final CredentialRepository credentialRepository;
        private final CredentialService credentialService;
        private final HttpServletRequest request;

        public CredentialUniqueValidator(final CredentialRepository credentialRepository,
                                         final CredentialService credentialService,
                                         final HttpServletRequest request) {
            this.credentialRepository = credentialRepository;
            this.credentialService = credentialService;
            this.request = request;
        }

        @Override
        public boolean isValid(final CredentialDTO dto, final ConstraintValidatorContext context) {
            if (dto == null) {
                return true;
            }
            if (dto.getApplicationId() == null || dto.getEnvironment() == null || dto.getCredentialType() == null) {
                // Let field-level @NotNull handle these cases
                return true;
            }
            // Determine if this is an update (edit) by inspecting path variables
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            final String idStr = (pathVariables != null ? pathVariables.get("id") : null);

            Long appId = dto.getApplicationId();
            Long envId = dto.getEnvironment().getId();
            Long typeId = dto.getCredentialType().getId();
            String username;
            Integer version;
            Boolean enabled;

            if (idStr != null) {
                // Update flow: service will create a new record with a version = old.version + 1, same username, enabled = true
                try {
                    final var existing = credentialService.get(Long.parseLong(idStr));
                    if (existing == null) {
                        return true; // will be handled elsewhere
                    }
                    username = existing.getUsername();
                    version = (existing.getVersion() == null ? 1 : existing.getVersion() + 1);
                    enabled = Boolean.TRUE; // update always enables
                } catch (Exception e) {
                    return true; // if cannot resolve current, skip validation to avoid false negatives
                }
            } else {
                // Create flow: service sets default version=1 if null and enabled=true if null
                username = dto.getUsername();
                version = (dto.getVersion() == null ? 1 : dto.getVersion());
                enabled = (dto.getEnabled() == null ? Boolean.TRUE : dto.getEnabled());
                if (username == null || username.isBlank()) {
                    // Let field-level validators handle username presence on create
                    return true;
                }
            }

            boolean exists = credentialRepository
                    .existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                            appId, envId, typeId, username, version, enabled
                    );
            if (!exists) {
                return true;
            }

            // For completeness, if the update and the composite somehow match the original (which shouldn't happen due to version increment), allow.
            if (idStr != null) {
                try {
                    final var existing = credentialService.get(Long.parseLong(idStr));
                    if (existing != null
                            && existing.getApplicationId() != null
                            && existing.getEnvironment() != null
                            && existing.getCredentialType() != null
                            && existing.getApplicationId().equals(appId)
                            && existing.getEnvironment().getId() == envId
                            && existing.getCredentialType().getId() == typeId
                            && existing.getUsername() != null
                            && existing.getUsername().equals(username)
                            && existing.getEnabled() != null
                            && existing.getEnabled().equals(enabled)
                            && existing.getVersion() != null
                            && existing.getVersion().equals(version)) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // fall through
                }
            }

            return false;
        }
    }
}
