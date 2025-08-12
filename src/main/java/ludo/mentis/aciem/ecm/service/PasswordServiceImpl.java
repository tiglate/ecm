package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.config.CryptoProperties;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.model.CipherEnvelope;
import ludo.mentis.aciem.ecm.model.CipherEnvelopeMapper;
import ludo.mentis.aciem.ecm.service.crypto.CryptoService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final CryptoService cryptoService;
    private final CryptoProperties config;

    public PasswordServiceImpl(CryptoService cryptoService, CryptoProperties config) {
        this.cryptoService = cryptoService;
        this.config = config;
    }

    @Override
    public CipherEnvelope encryptPassword(String password) {
        byte[] aad = config.getAad().getBytes(StandardCharsets.UTF_8);
        return cryptoService.encryptString(password, aad);
    }

    @Override
    public String decryptPassword(CipherEnvelope envelope) {
        byte[] aad = config.getAad().getBytes(StandardCharsets.UTF_8);
        return cryptoService.decryptToString(envelope, aad);
    }

    @Override
    public CipherEnvelopeEntity encryptPasswordToEntity(String password) {
        return CipherEnvelopeMapper.toEntity(encryptPassword(password));
    }

    @Override
    public String decryptPasswordFromEntity(CipherEnvelopeEntity envelope) {
        return decryptPassword(CipherEnvelopeMapper.toModel(envelope));
    }
}
