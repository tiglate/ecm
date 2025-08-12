package ludo.mentis.aciem.ecm.model;

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
}
