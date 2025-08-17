package ludo.mentis.aciem.ecm.model;

import java.time.LocalDateTime;

public record ApiKeySearchDTO(
        Long id,
        Long applicationId,
        String application,
        Long environmentId,
        String environment,
        String server,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String updatedBy) { }
