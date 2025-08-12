package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.ecm.domain.CredentialType;
import ludo.mentis.aciem.ecm.domain.Environment;

import java.time.LocalDateTime;

public class CredentialDTO {

    private Long id;

    @NotNull
    private Environment environment;

    @NotNull
    private CredentialType credentialType;

    @NotNull
    private Long applicationId;

    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(max = 500)
    private String password;

    private Integer version;

    @NotNull
    private Boolean enabled;

    @Size(max = 500)
    private String url;

    private String notes;

    // Auditing/display fields
    private String createdBy;
    private LocalDateTime createdAt;

    public CredentialDTO() {
    }

    // Projection constructor used by CredentialRepository
    public CredentialDTO(Long id, String username, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

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

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(CredentialType credentialType) {
        this.credentialType = credentialType;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
