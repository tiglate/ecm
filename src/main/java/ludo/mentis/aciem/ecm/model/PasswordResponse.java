package ludo.mentis.aciem.ecm.model;

import java.util.Map;

public record PasswordResponse(
        String password,
        String exception,
        Map<String, String> validationErrors) {
}
