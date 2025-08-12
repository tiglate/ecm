package ludo.mentis.aciem.ecm.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_credential")
@EntityListeners(AuditingEntityListener.class)
public class Credential {

    @Id
    @Column(name = "id_credential", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Self-reference to the next version (null if this is the latest)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_credential_next")
    private Credential nextCredential;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cipher_envelope", nullable = false)
    private CipherEnvelopeEntity cipherEnvelopeEntity;

    // Persist the numeric environment ID; expose the enum via transient getter/setter
    @Column(name = "id_environment", nullable = false)
    private Long environmentId;

    // Persist the numeric type ID; expose the enum via transient getter/setter
    @Column(name = "id_credential_type", nullable = false)
    private Long credentialTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_application", nullable = false)
    private BusinessApp application;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(length = 500)
    private String url;

    @Column(columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.CLOB)
    private String notes;

    @LastModifiedBy
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

    public Credential getNextCredential() {
        return nextCredential;
    }

    public void setNextCredential(Credential nextCredential) {
        this.nextCredential = nextCredential;
    }

    public CipherEnvelopeEntity getCipherEnvelope() {
        return cipherEnvelopeEntity;
    }

    public void setCipherEnvelope(CipherEnvelopeEntity cipherEnvelopeEntity) {
        this.cipherEnvelopeEntity = cipherEnvelopeEntity;
    }

    // Transient enum view backed by environmentId
    @Transient
    public Environment getEnvironment() {
        if (environmentId == null) {
            return null;
        }
        for (Environment env : Environment.values()) {
            if (env.getId() == environmentId) {
                return env;
            }
        }
        throw new IllegalStateException("Environment ID " + environmentId + " is not mapped to an Environment enum value");
    }

    public void setEnvironment(Environment environment) {
        this.environmentId = (environment != null ? environment.getId() : null);
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    // Transient enum view backed by credentialTypeId
    @Transient
    public CredentialType getCredentialType() {
        if (credentialTypeId == null) {
            return null;
        }
        for (CredentialType type : CredentialType.values()) {
            if (type.getId() == credentialTypeId) {
                return type;
            }
        }
        throw new IllegalStateException("Credential type ID " + credentialTypeId + " is not mapped to a CredentialType enum value");
    }

    public void setCredentialType(CredentialType credentialType) {
        this.credentialTypeId = (credentialType != null ? credentialType.getId() : null);
    }

    public Long getCredentialTypeId() {
        return credentialTypeId;
    }

    public void setCredentialTypeId(Long credentialTypeId) {
        this.credentialTypeId = credentialTypeId;
    }

    public BusinessApp getApplication() {
        return application;
    }

    public void setApplication(BusinessApp application) {
        this.application = application;
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