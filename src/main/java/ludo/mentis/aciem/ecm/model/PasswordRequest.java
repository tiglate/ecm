package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordRequest(
        @NotBlank
        String appCode,
        @NotNull
        Environment environment,
        @NotNull
        CredentialType credentialType,
        @NotBlank
        String username
) {
}
