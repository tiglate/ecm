package ludo.mentis.aciem.ecm.config;

import ludo.mentis.aciem.ecm.service.crypto.aes.AesServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    private final CryptoProperties config;

    public CryptoConfig(CryptoProperties config) {
        this.config = config;
    }

    @Bean
    AesServiceConfig aesServiceConfig() {
        var pass = config.getKey().toCharArray();
        return AesServiceConfig
                .builder()
                .passphrase(pass)
                .pbkdf2Iterations(config.getIterations())
                .keyLengthBits(config.getKeySize())
                .build();
    }
}
