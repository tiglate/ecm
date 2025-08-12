package ludo.mentis.aciem.ecm.domain;
/*
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_credential")
@EntityListeners(AuditingEntityListener.class)
public class Credential implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_credential", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_credential_next")
    private Long nextCredentialId;

    @Column(name = "id_cipher_envelope", nullable = false)
    private Long cipherEnvelopeId;

    // Persisted as FK id to tb_environment
    @Column(name = "id_environment", nullable = false)
    private Long environmentId;

    @Column(name = "id_application", nullable = false)
    private Long applicationId;

    // Persisted as FK id to tb_credential_type
    @Column(name = "id_credential_type", nullable = false)
    private Long credentialTypeId;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(length = 500)
    private String url;

    @Lob
    @Column
    private String notes;

    //@LastModifiedBy
    @Column(name = "created_by", nullable = false, length = 45)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    // Convenience enum accessors for environment
    @Transient
    public Environment getEnvironment() {
        return Environment.fromId(this.environmentId);
    }

    public void setEnvironment(Environment environment) {
        this.environmentId = (environment != null ? environment.getId() : null);
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

    // Convenience enum accessors for credential type
    @Transient
    public CredentialType getCredentialType() {
        return CredentialType.fromId(this.credentialTypeId);
    }

    public void setCredentialType(CredentialType credentialType) {
        this.credentialTypeId = (credentialType != null ? credentialType.getId() : null);
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

    @SuppressWarnings("unused")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
*/