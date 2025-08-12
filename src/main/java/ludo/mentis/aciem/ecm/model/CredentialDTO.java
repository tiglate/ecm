package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CredentialDTO {

    private Long id;

    private Long nextCredentialId;

    @NotNull
    private Long cipherEnvelopeId;

    @NotNull
    private Long environmentId;

    @NotNull
    private Long applicationId;

    @NotNull
    private Long credentialTypeId;

    @NotBlank
    @Size(max = 255)
    private String username;

    @NotNull
    private Integer version;

    @NotNull
    private Boolean enabled;

    @Size(max = 500)
    private String url;

    private String notes;

    @NotBlank
    @Size(max = 45)
    private String createdBy;

    private LocalDateTime createdAt;

    public CredentialDTO() { }

    public CredentialDTO(Long id,
                         Long nextCredentialId,
                         Long cipherEnvelopeId,
                         Long environmentId,
                         Long applicationId,
                         Long credentialTypeId,
                         String username,
                         Integer version,
                         Boolean enabled,
                         String url,
                         String notes,
                         String createdBy,
                         LocalDateTime createdAt) {
        this.id = id;
        this.nextCredentialId = nextCredentialId;
        this.cipherEnvelopeId = cipherEnvelopeId;
        this.environmentId = environmentId;
        this.applicationId = applicationId;
        this.credentialTypeId = credentialTypeId;
        this.username = username;
        this.version = version;
        this.enabled = enabled;
        this.url = url;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNextCredentialId() {
        return nextCredentialId;
    }

    public void setNextCredentialId(Long nextCredentialId) {
        this.nextCredentialId = nextCredentialId;
    }

    public Long getCipherEnvelopeId() {
        return cipherEnvelopeId;
    }

    public void setCipherEnvelopeId(Long cipherEnvelopeId) {
        this.cipherEnvelopeId = cipherEnvelopeId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCredentialTypeId() {
        return credentialTypeId;
    }

    public void setCredentialTypeId(Long credentialTypeId) {
        this.credentialTypeId = credentialTypeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
