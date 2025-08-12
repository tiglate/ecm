package ludo.mentis.aciem.ecm.domain;

import java.util.HashMap;
import java.util.Map;

public enum Environment {
    DEV(1L, "DEV"),
    QA(2L, "QA"),
    UAT(3L, "UAT"),
    PROD(4L, "PROD");

    private final long id;
    private final String name;

    Environment(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final Map<Long, Environment> BY_ID = new HashMap<>();
    private static final Map<String, Environment> BY_NAME = new HashMap<>();

    static {
        for (Environment e : values()) {
            BY_ID.put(e.id, e);
            BY_NAME.put(e.name, e);
        }
    }

    public static Environment fromId(Long id) {
        if (id == null) return null;
        return BY_ID.get(id);
    }

    public static Environment fromName(String name) {
        if (name == null) return null;
        return BY_NAME.get(name);
    }
}
