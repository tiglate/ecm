package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class AuthenticationRequest {

    @NotNull
    @Size(max = 30)
    private String username;

    @NotNull
    @Size(max = 72)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String value) {
        this.username = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String value) {
        this.password = value;
    }
}
