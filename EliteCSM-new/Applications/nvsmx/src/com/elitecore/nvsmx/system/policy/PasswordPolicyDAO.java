package com.elitecore.nvsmx.system.policy;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * This will be used to manage password policy and validate the password with password policy
 * Created by dhyani.raval on 28/12/16.
 */
public class PasswordPolicyDAO {

    private static final String MODULE = PasswordPolicyDAO.class.getSimpleName();
    private static final String PASSWORD_RANGE = "5-50";    //NOSONAR
    private static final Integer ALPHABET_RANGE = 1;
    private static final Integer DIGITS_RANGE = 1;
    private static final Integer SPECIAL_CHAR_RANGE = 1;

    private static final String PROHIBITED_CHARS = "$@";
    private static final Integer PASSWORD_VALIDITY = 180;
    private static final boolean CHANGE_PWD_ON_FIRST_LOGIN = true;
    private static final Integer TOTAL_HISTORICAL_PASSWORDS = 10;

    private PasswordPolicyDAO(){}

    public static PasswordPolicyConfigData getPasswordSelectionPolicy() throws Exception{
        PasswordPolicyConfigData passwordPolicyConfigData = null;
        boolean policyFlag = false;

        Session session =null;
        try {
            session = HibernateSessionFactory.getSession();
            Criteria criteria = session.createCriteria(PasswordPolicyConfigData.class);
            List<PasswordPolicyConfigData> passwordPolicyList = (List<PasswordPolicyConfigData>) criteria.list();
            if (Collectionz.isNullOrEmpty(passwordPolicyList) == false) {
                passwordPolicyConfigData = passwordPolicyList.get(0);
            }
            policyFlag = isValidPasswordPolicy(passwordPolicyConfigData);
        } catch (Exception e) {
            getLogger().error(MODULE, "Exception while fetching Password Policy. Reason:" + e.getMessage());
            getLogger().trace(e);
            policyFlag = false;
        } finally{
            if( policyFlag == false ){
                getLogger().warn(MODULE, "Taking Default Password Policy. Reason: Configured Password Policy is Invalid");
                passwordPolicyConfigData =  new PasswordPolicyConfigData();
                setDefaultPasswordPolicy(passwordPolicyConfigData);
            }
        }
        return passwordPolicyConfigData;
    }

    public static boolean isValidPasswordPolicy(PasswordPolicyConfigData passwordPolicyConfigData) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called isValidPasswordPolicy()");
        }
        if (passwordPolicyConfigData==null) {
            getLogger().error(MODULE, "Empty password Policy");
            return false;
        }

        if (passwordPolicyConfigData.getTotalHistoricalPasswords()==null || passwordPolicyConfigData.getTotalHistoricalPasswords() > 10){
            getLogger().error(MODULE, "Historical Passwords exceeds max value");
            return false;
        }

        boolean checkValid=true;

        checkValid = checkPasswordRange(passwordPolicyConfigData);

        if(checkValid == true) {
            if (passwordPolicyConfigData.isAlphabetCheckReq() == true || passwordPolicyConfigData.isDigitCheckReq() == true
                    || passwordPolicyConfigData.isSpecialCharCheckReq() == true) {
                checkValid = checkRequiredCharsLength(passwordPolicyConfigData);
            } else {
                checkValid = true;
            }
        }

        return checkValid;
    }

    private static boolean checkRequiredCharsLength(PasswordPolicyConfigData passwordPolicyConfigData) {
        int totalAlphabets = 0;

        if (passwordPolicyConfigData.getAlphabetRange() != null) {
            totalAlphabets = passwordPolicyConfigData.getAlphabetRange();
            if(passwordPolicyConfigData.getAlphabetRange()<0 || passwordPolicyConfigData.getAlphabetRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                getLogger().error(MODULE, "Invalid Required Alphabets");
                return false;
            }
        }

        int totalDigits = 0;
        if (passwordPolicyConfigData.getDigitsRange() != null) {
            totalDigits = passwordPolicyConfigData.getDigitsRange();
            if(passwordPolicyConfigData.getDigitsRange()<0 || passwordPolicyConfigData.getDigitsRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                getLogger().error(MODULE, "Invalid Required Digits");
                return false;
            }
        }

        int totalSpecialChars = 0;
        if (passwordPolicyConfigData.getSpecialCharRange() != null) {

            totalSpecialChars = passwordPolicyConfigData.getSpecialCharRange();
            if(passwordPolicyConfigData.getSpecialCharRange()<0 || passwordPolicyConfigData.getSpecialCharRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                getLogger().error(MODULE, "Invalid Required Special Characters");
                return false;
            }
        }

        int totalAll = totalAlphabets + totalDigits + totalSpecialChars;
        if (totalAll > passwordPolicyConfigData.getMinPasswordLength()) {
            getLogger().error(MODULE, "Total of Require Alphabets, Digits and Special characters should not be greater than min password length");
            return false;
        }
        return true;
    }

    private static boolean checkPasswordRange(PasswordPolicyConfigData passwordPolicyConfigData) {

        if (Strings.isNullOrBlank(passwordPolicyConfigData.getPasswordRange())) {
            getLogger().error(MODULE, "Empty Password Range");
            return false;
        }

        if (passwordPolicyConfigData.getPasswordRange() != null && passwordPolicyConfigData.isLengthCheckReq() == true) {
            if (passwordPolicyConfigData.getMinPasswordLength() < 5 ){
                getLogger().error(MODULE, "Minimum password length value must be at least Five(5)");
                return false;
            }

            if (passwordPolicyConfigData.getMaxPasswordLength() > 50) {
                getLogger().error(MODULE, "Maximum password length value must be less than or equal to Fifty(50)");
                return false;
            }

            if (passwordPolicyConfigData.getMinPasswordLength() > passwordPolicyConfigData.getMaxPasswordLength()) {
                getLogger().error(MODULE, "Invalid password Range");
                return false;
            }
        } else {
            getLogger().error(MODULE, "Only Positive Integer are allowed in Min-Max Password Range");
            return false;
        }
        return true;
    }

    private static void setDefaultPasswordPolicy(PasswordPolicyConfigData passwordPolicyConfigData) {
        passwordPolicyConfigData.setPasswordRange(PASSWORD_RANGE);
        passwordPolicyConfigData.setAlphabetRange(ALPHABET_RANGE);
        passwordPolicyConfigData.setDigitsRange(DIGITS_RANGE);
        passwordPolicyConfigData.setSpecialCharRange(SPECIAL_CHAR_RANGE);
        passwordPolicyConfigData.setProhibitedChars(PROHIBITED_CHARS);
        passwordPolicyConfigData.setPasswordValidity(PASSWORD_VALIDITY);
        passwordPolicyConfigData.setTotalHistoricalPasswords(TOTAL_HISTORICAL_PASSWORDS);
        passwordPolicyConfigData.setChangePwdOnFirstLogin(CHANGE_PWD_ON_FIRST_LOGIN);
        passwordPolicyConfigData.setDefaultPasswordPolicy(true);
    }

    public static boolean validatePasswordWithPasswordPolicy(String password , PasswordPolicyConfigData passwordPolicyConfigData) throws Exception {


        Integer minPasswordLength = passwordPolicyConfigData.getMinPasswordLength();
        Integer maxPasswordLength = passwordPolicyConfigData.getMaxPasswordLength();
        String minMaxPasswordLength = "{"+minPasswordLength+","+maxPasswordLength+"}";


        Integer alphabetRange = passwordPolicyConfigData.getAlphabetRange();
        if (alphabetRange == null) {
            alphabetRange = 0;
        }
        String allowedAlphabets = "{"+alphabetRange+"}";


        Integer digitsRange = passwordPolicyConfigData.getDigitsRange();
        if (digitsRange == null ) {
            digitsRange = 0;
        }
        String allowedDigits = "{"+digitsRange+"}";

        Integer specialCharRange = passwordPolicyConfigData.getSpecialCharRange();

        if (specialCharRange == null) {
            specialCharRange = 0;
        }
        String allowedSpecialCharacter = "{"+specialCharRange+"}";

        String specialCharacterList = "!#$%&'()*+,-./:;\\[\\]<=>?@^_`{|}~";


        String[] prohibitedList = passwordPolicyConfigData.getProhibitedChars().split("");

        for (String specialCharacter : prohibitedList) {
            if(password.indexOf(specialCharacter) != -1) {
                throw new Exception("Special characters "+ Arrays.toString(prohibitedList)+" prohibited");
            }
        }

        /**
         * Explanation of Regex with example
         * ^                                            Start of string
         * (?=.*\\d{1})                                 at-least one digit required
         * (?=.*[a-zA-Z]{3})                            at-least 3 alphabets are required either UPPER OR LOWER
         * (?=.*[!#$%&'()*+,-./:;\[\]<=>?@^_`{|}~]{1})  at-least one special character required among the defined Special Characters.
         * (?=\S+$)                                     no white space allowed in entire password
         * .{5,15}                                      minimum 5 and maximum 15 character is required
         *  $                                           end of the string
         */
        String passwordRegEx = "^(?=.*\\d"+allowedDigits+")(?=.*[a-zA-Z]"+allowedAlphabets+")(?=.*["+specialCharacterList+"]"+allowedSpecialCharacter+")(?=\\S+$)."+minMaxPasswordLength+"$";
        return Pattern.matches(passwordRegEx, password);
    }
}
