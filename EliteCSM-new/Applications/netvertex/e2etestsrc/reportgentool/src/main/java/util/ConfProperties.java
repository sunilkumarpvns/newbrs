package util;

public enum ConfProperties {

    TEST_RESULT_INDEX("test.result.index"),
    TEST_RUN_ID("test.runid"),
    TEST_CASE_ID_INDEX("test.caseid.index"),
    CHECK_VERSION("test.check.version"),
    CHECK_REVISION("test.check.revision"),
    TEST_ELAPSE_TIME_INDEX("test.elapsetime.index"),
    TEST_INOUT_FILE_NAME_INDEX("test.inout.file.name.index"),
    TEST_FAIL_MESSAGE_INDEX("test.failmessage.index");

    private String key;
    private int intVal;

    ConfProperties(String key) {

        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
