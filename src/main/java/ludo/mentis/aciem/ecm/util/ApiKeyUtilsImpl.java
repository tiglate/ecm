package ludo.mentis.aciem.ecm.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyUtilsImpl implements ApiKeyUtils {
    
    @Override
    public String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[48];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
