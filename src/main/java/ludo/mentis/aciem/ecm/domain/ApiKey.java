package ludo.mentis.aciem.ecm.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.ecm.model.Environment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_api_key")
@EntityListeners(AuditingEntityListener.class)
public class ApiKey {

    @Id
    @Column(name = "id_api_key", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cipher_envelope", nullable = false)
    private CipherEnvelopeEntity cipherEnvelopeEntity;

    // Persist the numeric environment ID; expose the enum via transient getter/setter
    @Column(name = "id_environment", nullable = false)
    private Long environmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_application", nullable = false)
    private BusinessApp application;

    @Column(nullable = false, length = 45, unique = true)
    private String clientId;

    @Column(nullable = false, length = 45)
    private String server;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 45)
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BusinessApp getApplication() {
        return application;
    }

    public void setApplication(BusinessApp application) {
        this.application = application;
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

    public void setServer(String Server) {
        this.server = Server;
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