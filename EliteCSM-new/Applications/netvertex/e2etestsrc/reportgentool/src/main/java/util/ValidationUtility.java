package util;

public class ValidationUtility {

    private ValidationUtility(){}

    public static boolean isNullOrBlank(String inputString) {
        return inputString == null || inputString.trim().length() == 0;
    }
}
