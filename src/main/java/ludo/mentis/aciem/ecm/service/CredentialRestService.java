package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.model.PasswordRequest;

import java.util.Optional;

public interface CredentialRestService {
    Optional<String> getPassword(final PasswordRequest passwordRequest);
}
