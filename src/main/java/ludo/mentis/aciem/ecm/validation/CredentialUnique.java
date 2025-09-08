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

    @SuppressWarnings("ClassCanBeRecord")
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
            if (shouldSkipValidation(dto)) {
                return true;
            }

            final String idStr = resolvePathId();

            final Long appId = dto.getApplicationId();
            final Long envId = dto.getEnvironment().getId();
            final Long typeId = dto.getCredentialType().getId();

            final Composite composite = resolveComposite(dto, idStr);
            if (composite == null) {
                // Could not resolve composite reliably; defer to other validations
                return true;
            }

            final boolean exists = existsByComposite(appId, envId, typeId, composite);
            if (!exists) {
                return true;
            }

            return isSameAsExistingOnUpdate(idStr, appId, envId, typeId, composite);
        }

        private boolean shouldSkipValidation(final CredentialDTO dto) {
            if (dto == null) {
                return true;
            }
            // Let field-level @NotNull handle these cases
            return dto.getApplicationId() == null || dto.getEnvironment() == null || dto.getCredentialType() == null;
        }

        private String resolvePathId() {
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            return (pathVariables != null ? pathVariables.get("id") : null);
        }

        private Composite resolveComposite(final CredentialDTO dto, final String idStr) {
            String username;
            int version;
            Boolean enabled;

            if (idStr != null) {
                try {
                    final var existing = credentialService.get(Long.parseLong(idStr));
                    if (existing == null) {
                        return null; // let other layers handle missing entity
                    }
                    username = existing.getUsername();
                    version = (existing.getVersion() == null ? 1 : existing.getVersion() + 1);
                    enabled = Boolean.TRUE; // update always enables
                } catch (Exception e) {
                    return null; // fail-safe: skip strict validation
                }
            } else {
                username = dto.getUsername();
                version = (dto.getVersion() == null ? 1 : dto.getVersion());
                enabled = (dto.getEnabled() == null ? Boolean.TRUE : dto.getEnabled());
                if (username == null || username.isBlank()) {
                    return null; // Let field-level validators handle username presence on create
                }
            }
            return new Composite(username, version, enabled);
        }

        private boolean existsByComposite(Long appId, Long envId, Long typeId, Composite c) {
            return credentialRepository
                    .existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                            appId, envId, typeId, c.username, c.version, c.enabled
                    );
        }

        private boolean isSameAsExistingOnUpdate(String idStr, Long appId, Long envId, Long typeId, Composite c) {
            if (idStr == null) {
                return false;
            }
            try {
                final var existing = credentialService.get(Long.parseLong(idStr));
                return existing != null
                        && existing.getApplicationId() != null
                        && existing.getEnvironment() != null
                        && existing.getCredentialType() != null
                        && existing.getApplicationId().equals(appId)
                        && existing.getEnvironment().getId() == envId
                        && existing.getCredentialType().getId() == typeId
                        && existing.getUsername() != null
                        && existing.getUsername().equals(c.username)
                        && existing.getEnabled() != null
                        && existing.getEnabled().equals(c.enabled)
                        && existing.getVersion() != null
                        && existing.getVersion().equals(c.version);
            } catch (Exception ignored) {
                return false;
            }
        }

        private static class Composite {
            final String username;
            final Integer version;
            final Boolean enabled;

            Composite(String username, Integer version, Boolean enabled) {
                this.username = username;
                this.version = version;
                this.enabled = enabled;
            }
        }
    }
}
