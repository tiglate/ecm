package ludo.mentis.aciem.ecm.domain;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Long, CredentialType> BY_ID = new HashMap<>();
    private static final Map<String, CredentialType> BY_NAME = new HashMap<>();

    static {
        for (CredentialType t : values()) {
            BY_ID.put(t.id, t);
            BY_NAME.put(t.name, t);
        }
    }

    public static CredentialType fromId(Long id) {
        if (id == null) return null;
        return BY_ID.get(id);
    }

    public static CredentialType fromName(String name) {
        if (name == null) return null;
        return BY_NAME.get(name);
    }
}
