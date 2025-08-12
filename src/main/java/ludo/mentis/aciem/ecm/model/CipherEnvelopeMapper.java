package ludo.mentis.aciem.ecm.model;

import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;

public final class CipherEnvelopeMapper {
    private CipherEnvelopeMapper() {
    }

    public static CipherEnvelopeEntity toEntity(CipherEnvelope env) {
        var e = new CipherEnvelopeEntity();
        e.setVersion(env.getVersion());
        e.setKdf(env.getKdf());
        e.setIterations(env.getIterations());
        e.setSalt(env.getSalt());
        e.setIv(env.getIv());
        e.setCiphertext(env.getCiphertext());
        return e;
    }

    public static CipherEnvelope toModel(CipherEnvelopeEntity e) {
        var b = CipherEnvelope.builder()
                .version(e.getVersion())
                .kdf(e.getKdf())
                .iv(e.getIv())
                .ciphertext(e.getCiphertext());
        if (e.getKdf() == Kdf.PBKDF2) {
            b.iterations(e.getIterations())
                    .salt(e.getSalt());
        }
        return b.build();
    }
}
