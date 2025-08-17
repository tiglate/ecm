package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.ecm.validation.ApiKeyAppEnvironmentUnique;
import ludo.mentis.aciem.ecm.validation.ApiKeyClientIdUnique;

import java.time.LocalDateTime;

@ApiKeyAppEnvironmentUnique
public class ApiKeyDTO {

    private Long id;

    @NotNull
    private Environment environment;

    @NotNull
    private Long applicationId;

    @Size(max = 45)
    @NotBlank
    @ApiKeyClientIdUnique
    private String clientId;

    @Size(max = 45)
    private String server;

    @NotBlank
    @Size(max = 500)
    private String secret;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
