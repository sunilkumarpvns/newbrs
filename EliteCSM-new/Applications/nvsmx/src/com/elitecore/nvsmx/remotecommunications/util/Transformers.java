package com.elitecore.nvsmx.remotecommunications.util;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * Created by aditya on 4/21/17.
 */

public class Transformers {

    private static final Gson gson = GsonFactory.defaultInstance();

    private static final String MODULE = "TRANSFORMERS";

    private static final String NULL_STRING = "null";
    public static final PolicyCacheDetailTransformer POLICY_CACHE_DETAIL_TRANSFORMER = new PolicyCacheDetailTransformer();
    public static final StringToStringTransformer STRING_TO_STRING_TRANSFORMER = new StringToStringTransformer();
    public static final SessionInformationTransformer SESSION_INFORMATION_TRANSFORMER = new SessionInformationTransformer();
    public static final SessionInformationsTransformer SESSION_INFORMATIONS_TRANSFORMER = new SessionInformationsTransformer();

    public static PolicyCacheDetailTransformer stringToPolicyCacheDetail() {
        return POLICY_CACHE_DETAIL_TRANSFORMER;
    }

    public static StringToStringTransformer stringToString() {
        return STRING_TO_STRING_TRANSFORMER;
    }

    public static SessionInformationTransformer stringTosessionInformation() {
        return SESSION_INFORMATION_TRANSFORMER;
    }

    public static SessionInformationsTransformer stringToSessionInformations() {
        return SESSION_INFORMATIONS_TRANSFORMER;
    }


    private static final class PolicyCacheDetailTransformer implements Function<String, PolicyCacheDetail> {
        @Override
        public PolicyCacheDetail apply(String policyCacheDetailString) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Received String at PolicyCacheDetailTransformer is: " + policyCacheDetailString);
            }
            if (Strings.isNullOrEmpty(policyCacheDetailString) == true) {
                return null;
            }

            try {
                String decodedString = URLDecoder.decode(policyCacheDetailString, CommonConstants.UTF_8);
                return gson.fromJson(decodedString, com.elitecore.corenetvertex.data.PolicyCacheDetail.class);
            } catch (UnsupportedEncodingException e) {
                LogManager.getLogger().error(MODULE,"Unable to decode response: " + policyCacheDetailString + " .Reason: "+e.getMessage());
                LogManager.getLogger().trace(MODULE,e);
            }

            return null;
        }


    }


    private static final class StringToStringTransformer implements Function<String, String> {

        @Override
        public String apply(String input) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Received string at StringToString transformer is: " + input);
            }

            ///FIXME CHECK DECODING IS REQUIRED OR NOT
            if (Strings.isNullOrEmpty(input) == true || NULL_STRING.equalsIgnoreCase(input)) {
                return null;
            }
            return input;
        }
    }


    private static final class SessionInformationTransformer implements Function<String, SessionInformation> {


        @Override
        public SessionInformation apply(String sessionInformationString) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Received String at SessionInformation Transformer is: " + sessionInformationString);
            }
            if (Strings.isNullOrEmpty(sessionInformationString) == true) {
                return null;
            }

            ///FIXME CHECK DECODING IS REQUIRED OR NOT
            SessionInformation sessionInformation = gson.fromJson(sessionInformationString, SessionInformation.class);
            return sessionInformation;
        }
    }

    private static final class SessionInformationsTransformer implements Function<String, Collection<SessionInformation>> {


        @Override
        public Collection<SessionInformation> apply(String sessionInformationString) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Received String at SessionInformations Transformer is: " + sessionInformationString);
            }
            if (Strings.isNullOrEmpty(sessionInformationString) == true) {
                return null;
            }

            ///FIXME CHECK DECODING IS REQUIRED OR NOT
            return gson.fromJson(sessionInformationString, new TypeToken<Collection<SessionInformation>>() {
            }.getType());
        }
    }


}
