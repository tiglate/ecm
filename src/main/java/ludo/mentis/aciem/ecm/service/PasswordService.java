package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.model.CipherEnvelope;

public interface PasswordService {

    CipherEnvelope encryptPassword(final String password);

    String decryptPassword(final CipherEnvelope envelope);

    CipherEnvelopeEntity encryptPasswordToEntity(final String password);

    String decryptPasswordFromEntity(final CipherEnvelopeEntity envelope);
}
