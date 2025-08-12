package ludo.mentis.aciem.ecm.model;

public enum CredentialType {
    DATABASE(1L, "DATABASE"),
    WINDOWS(2L, "WINDOWS"),
    LINUX(3L, "LINUX"),
    API_KEY(4L, "API_KEY"),
    JWT_TOKEN(5L, "JWT_TOKEN"),
    OTHER(6L, "OTHER");

    private final long id;
    private final String name;

    CredentialType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
