package ludo.mentis.aciem.ecm.model;

import java.time.LocalDateTime;

public record CredentialSearchDTO(
        Long id,
        Long applicationId,
        String application,
        Long environmentId,
        String environment,
        Long credentialTypeId,
        String credentialType,
        String username,
        Integer version,
        Boolean enabled,
        LocalDateTime createdAt,
        String createdBy) { }
