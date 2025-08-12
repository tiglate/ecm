package ludo.mentis.aciem.ecm.domain;

import jakarta.persistence.*;
import ludo.mentis.aciem.ecm.model.Kdf;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cipher_envelope")
@EntityListeners(AuditingEntityListener.class)
public class CipherEnvelopeEntity {

    @Id
    @Column(name = "id_cipher_envelope", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8, columnDefinition = "CHAR(8)")
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Kdf kdf;

    @Column
    private Integer iterations;

    @Column(length = 64)
    private byte[] salt;

    @Column(nullable = false, length = 32)
    private byte[] iv;

    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] ciphertext;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Kdf getKdf() {
        return kdf;
    }

    public void setKdf(Kdf kdf) {
        this.kdf = kdf;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
