package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.gateway.diameter.utility.data.ApplicationData;
import com.elitecore.netvertex.gateway.diameter.utility.data.ResultCodeEntryData;
import com.elitecore.netvertex.gateway.diameter.utility.data.ResultCodeMappingData;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultCodeMapping implements Cacheable {

    private static final String MODULE = "RESULT-CODE-MAP";
    private static ResultCodeMapping resultCodeMapping;
    private static final String DISPLAY_NAME = "RESULT-CODE-MAPPING";
    private static final String RESULT_CODE_MAPPING_XML = "result-code-mapping.xml";
    private static final String SYSTEM_FOLDER = "system";
    private static final String DIAMETER = "diameter";
    private NetVertexServerContext context;
    private Map<String, ResultCodeEntry> pcrfKeyWiseResultCodes;
    private boolean isInitialized;


    private ResultCodeMapping() {
        this.isInitialized = false;
        this.pcrfKeyWiseResultCodes = new HashMap<>();
    }

    private Map<String, ResultCodeEntry> getDefaultMapping() {
        Map<String, ResultCodeEntry> tempKeyWiseResultCodeEntries = new HashMap<>();

        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val, createResultCodeEntry(ResultCode.DIAMETER_SUCCESS.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, createResultCodeEntry(ResultCode.DIAMETER_USER_UNKNOWN.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, createResultCodeEntry(ResultCode.DIAMETER_USER_UNKNOWN.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_CREDIT_LIMIT_REACH.val, createResultCodeEntry(ResultCode.DIAMETER_CREDIT_LIMIT_REACHED.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, createResultCodeEntry(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_RESPONSE_DROPPED.val, createResultCodeEntry(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_SESSION_ID.val, createResultCodeEntry(ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_EXPIRED.val, createResultCodeEntry(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_INACTIVE.val, createResultCodeEntry(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        tempKeyWiseResultCodeEntries.put(PCRFKeyValueConstants.RESULT_CODE_INTERNAL_ERROR.val, createResultCodeEntry(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));

        return tempKeyWiseResultCodeEntries;
    }

    @Nonnull
    private ResultCodeEntry createResultCodeEntry(int resultCode) {
        return new ResultCodeEntry(createResultCodeAVP(resultCode));
    }

    static {
        resultCodeMapping = new ResultCodeMapping();
    }

    public static ResultCodeMapping getInstance() {
        return resultCodeMapping;
    }
    
 
    
    


    public void init(NetVertexServerContext serverContext) throws InitializationFailedException {
        if (isInitialized) {
            if (getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Result code mapping is already initialized");
            }
            return;
        }
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing result code mapping");
        }

        this.context = serverContext;

        Map<String, ResultCodeEntry> defaultMapping = getDefaultMapping();

        try {
            defaultMapping.putAll(readConfiguration());
            this.pcrfKeyWiseResultCodes = defaultMapping;
            ConfigLogger.getInstance().info(MODULE, toString());
        } catch (InitializationFailedException e) {
            this.pcrfKeyWiseResultCodes = defaultMapping;
            throw e;
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Result code mapping initialization completed");
        }
    }

    private Map<String, ResultCodeEntry> readConfiguration() throws InitializationFailedException {
        Map<String, ResultCodeEntry> tempPCRFKeyWiseResultCodes = new HashMap<>();

        File confFile = new File(context.getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + DIAMETER + File.separator + RESULT_CODE_MAPPING_XML);

        try {
            ResultCodeMappingData tempResultCodeMappingData = ConfigUtil.deserialize(confFile, ResultCodeMappingData.class);
            List<ResultCodeEntryData> resultCodeEntryDatas = tempResultCodeMappingData.getResultCodeEntryDatas();
            Iterator<ResultCodeEntryData> iterator = resultCodeEntryDatas.iterator();

            while (iterator.hasNext()) {

                ResultCodeEntryData mapping = iterator.next();

                if (Strings.isNullOrBlank(mapping.getPcrfKey())) {
                    iterator.remove();
                    continue;
                }

                if (mapping.getResultCode() == null || mapping.getResultCode() == 0) {
                    iterator.remove();
                    if (getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, "Skipping key: " + mapping.getPcrfKey() + ". Reason: ResultCode not configured");
                    }
                    continue;
                }

                tempPCRFKeyWiseResultCodes.put(mapping.getPcrfKey(), new ResultCodeEntry(createResultCodeAVP(mapping.getResultCode()),
                        createApplicationWiseResultCodeAVP(mapping.getApplicationData())));
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Result code mapping initialization completed");
            }

        } catch (Exception e) {
            throw new InitializationFailedException("Error while reading result code mapping. Reason: " + e.getMessage(), e);
        }

        return tempPCRFKeyWiseResultCodes;
    }

    private Map<Integer, IDiameterAVP> createApplicationWiseResultCodeAVP(List<ApplicationData> applicationDatas) {

        HashMap<Integer, IDiameterAVP> appWiseResultCodes = new HashMap<>();

        if (applicationDatas == null) {
            return appWiseResultCodes;
        }

        Iterator<ApplicationData> iterator = applicationDatas.iterator();

        while (iterator.hasNext()) {
            ApplicationData applicationData = iterator.next();

            if (applicationData.getId() == null) {
                iterator.remove();
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping application specific result code entry for application id: " + applicationData.getId() + ". Reason: ResultCode not configured");
                }
                continue;
            }

            if (applicationData.getDiameterResultCode() == null) {
                iterator.remove();
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping application specific result code entry for application id: " + applicationData.getId() + ". Reason: ResultCode not configured");
                }
                continue;
            }

            if (applicationData.getDiameterResultCode() == 0) {
                iterator.remove();
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping application specific result code entry for application id: " + applicationData.getId() + ". Reason: ResultCode is 0");
                }
                continue;
            }

            appWiseResultCodes.put(applicationData.getId(),
                    createApplicationResultCodeEntry(applicationData));
        }

        return appWiseResultCodes;
    }


    private IDiameterAVP createApplicationResultCodeEntry(ApplicationData applicationData) {

        if (applicationData.getVendorId() == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Using application specific diameter result code for application id: " + applicationData.getId() + ". Reason: Vendor Id is null");
            }
            return createResultCodeAVP(applicationData.getDiameterResultCode());
        }

        if (applicationData.getExperimentalResultCode() == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Using application specific diameter result code for application id: " + applicationData.getId() + ". Reason: Experimental result code is null");
            }
            return createResultCodeAVP(applicationData.getDiameterResultCode());
        }

        if (applicationData.getExperimentalResultCode() == 0) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Using application specific diameter result code for application id: " + applicationData.getId() + ". Reason: Experimental result code is 0");
            }
            return createResultCodeAVP(applicationData.getDiameterResultCode());
        }

        return createExperimentalResultCodeAVP(applicationData.getVendorId(), applicationData.getExperimentalResultCode());
    }

    private IDiameterAVP createExperimentalResultCodeAVP(int vendoreId, int experimentalResultCode) {
        AvpGrouped experimentalResultAVP = (AvpGrouped) getAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT);
        IDiameterAVP vendoreIdAVP = getAttribute(DiameterAVPConstants.VENDOR_ID);
        vendoreIdAVP.setInteger(vendoreId);

        IDiameterAVP experimentalResultCodeAVP = getAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
        experimentalResultCodeAVP.setInteger(experimentalResultCode);

        experimentalResultAVP.addSubAvp(vendoreIdAVP);
        experimentalResultAVP.addSubAvp(experimentalResultCodeAVP);
        return experimentalResultAVP;
    }

    private IDiameterAVP createResultCodeAVP(int resultCode) {
        IDiameterAVP resultCodeAVP = getAttribute(DiameterAVPConstants.RESULT_CODE);
        resultCodeAVP.setInteger(resultCode);
        return resultCodeAVP;
    }

    private IDiameterAVP getAttribute(String experimentalResult) {
        return DiameterDictionary.getInstance().getAttribute(experimentalResult);
    }

    private ILogger getLogger() {
        return LogManager.getLogger();
    }

    public IDiameterAVP getResultCodeAVP(String pcrfKey, int appId) {
        ResultCodeEntry resultCodeEntry = pcrfKeyWiseResultCodes.get(pcrfKey);

        // key not configured
        if (resultCodeEntry == null) {
            int resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY.code;
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Sending " + resultCode +
                        " as default result code. Reason: mapping not found for " + pcrfKey);
            }
            return createResultCodeAVP(resultCode);
        }

        return resultCodeEntry.getResultCodeAVP(appId);
    }

    public String toString() {
        StringWriter stringBuffer = new StringWriter();
        IndentingPrintWriter out = new IndentingPrintWriter(stringBuffer);
        out.println("Result Code Mapping");

        if (Maps.isNullOrEmpty(pcrfKeyWiseResultCodes)) {
            out.println("No cofiguration for result code mappig is provided");
        } else {

            pcrfKeyWiseResultCodes.forEach((pcrfKey, resultCodeEntry) -> {
                out.print(pcrfKey);
                out.println(":");
                out.incrementIndentation();
                resultCodeEntry.toString(out);
                out.decrementIndentation();
            });
        }

        out.println();
        out.close();
        return stringBuffer.toString();
    }

    @Override
    public CacheDetail reloadCache() {
        CacheDetailProvider cacheDetail = new CacheDetailProvider();
        cacheDetail.setName(DISPLAY_NAME);
        cacheDetail.setSource(getSource());

        try {
            cacheDetail.setResultCode(CacheConstants.SUCCESS);
            Map<String, ResultCodeEntry> tempPCRFKeyWiseResultCodeEntry = getDefaultMapping();
            tempPCRFKeyWiseResultCodeEntry.putAll(readConfiguration());
            this.pcrfKeyWiseResultCodes = tempPCRFKeyWiseResultCodeEntry;
            ConfigLogger.getInstance().info(MODULE, toString());
        } catch (InitializationFailedException initEx) {
            LogManager.getLogger().error(MODULE, "Failed to reload cache for result code mapping. Reason: " + initEx.getMessage());
            LogManager.getLogger().trace(MODULE, initEx);
            cacheDetail.setResultCode(CacheConstants.FAIL);
            cacheDetail.setDescription("Fail Reason : " + initEx.getMessage());
        }
        return cacheDetail;
    }

    @Override
    public String getName() {
        return MODULE;
    }

    private String getSource() {
        return "--";
    }

    @VisibleForTesting
    int size() {
        return this.pcrfKeyWiseResultCodes.size();
    }

}
