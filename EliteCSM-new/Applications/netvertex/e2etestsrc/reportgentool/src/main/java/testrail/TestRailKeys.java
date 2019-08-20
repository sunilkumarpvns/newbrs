package testrail;

public enum TestRailKeys {

    STATUS_ID("status_id"),
    COMMENT("comment"),
    ELAPSED("elapsed"),
    CUSTOM_VERSION("custom_version"),
    CUSTOM_REVESION("custom_revision"),
        ;

    private String id;

    TestRailKeys(String id) {

        this.id = id;
    }

    public String getId() {
        return id;
    }
}
