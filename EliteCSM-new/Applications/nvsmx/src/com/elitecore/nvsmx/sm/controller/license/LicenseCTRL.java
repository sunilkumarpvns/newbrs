package com.elitecore.nvsmx.sm.controller.license;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.license.base.SingleLicenseManager;
import com.elitecore.license.base.commons.LicenseMessages;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.base.exception.InvalidPublickeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import com.elitecore.license.util.AES;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.nvsmx.Version;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.license.LicenseData;
import com.elitecore.nvsmx.sm.model.license.NVLicenseData;
import com.elitecore.nvsmx.sm.model.license.SMLicenseData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;

@ParentPackage(value = "sm")
@Namespace("/sm/license")
public class LicenseCTRL extends RestGenericCTRL<LicenseData> {

    private String contentType = "application/pubkey";
    private transient InputStream fileInputStream;
    private List<LicenseData> licenseDataList = Collectionz.newArrayList();
    private List<NVLicenseData> nvLicenseDataList = Collectionz.newArrayList();
    private Timestamp lastUpdateTime;
    private String lastUpdatedTime;

    @Override
    public ACLModules getModule() {
        return ACLModules.LICENSE;
    }

    @Override
    public LicenseData createModel() {
        return new LicenseData();
    }

    @Override
    public void validate(){
        //No general validation is needed
    }

    @SkipValidation
    public HttpHeaders deregisterLicense(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called deregisterLicense()");
        }

        try{

            LicenseData licenseData = (LicenseData) getModel();

            //Step 1: Check if server instance exists
            ServerInstanceData netServerInstanceData=CRUDOperationUtil.get(ServerInstanceData.class,licenseData.getId());

            if(netServerInstanceData == null){
                prepareLicenseResponse(LicenseMessages.NV_UNKNOWN_SERVER_INSTANCE.getMessageCode(),LicenseMessages.NV_UNKNOWN_SERVER_INSTANCE.getMessage());
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            NVLicenseData storedLicenseData = CRUDOperationUtil.get(NVLicenseData.class,licenseData.getId());

            //Step 2: Check if license data exists
            if(storedLicenseData==null){
                prepareLicenseResponse(LicenseMessages.LICENSE_NEVER_REGISTERED.getMessageCode(),LicenseMessages.LICENSE_NEVER_REGISTERED.getMessage());
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            //Step 3: Deregister license
            CRUDOperationUtil.delete(storedLicenseData);
            DefaultNVSMXContext.getContext().deregisterNetvertexServerLicense(netServerInstanceData);
            prepareLicenseResponse(LicenseMessages.LICENSE_DEREGISTERED.getMessageCode(),LicenseMessages.LICENSE_DEREGISTERED.getMessage());
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(METHOD_SHOW) + "#section2");
            getRequest().setAttribute(NVSMXCommonConstants.TYPE, "instance");
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();

        } catch (Exception e){
            getLogger().error(getLogModule(),"Error while De-Register License from the Instance. Reason: "+e.getMessage());
            getLogger().trace(e);
            addActionError("Failed to De-Register the License");
            return new DefaultHttpHeaders(Results.ERROR.getValue()).disableCaching();
        }
    }

    @SkipValidation
    public HttpHeaders uploadLicense(){

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called uploadLicense()");
        }

        LicenseData licenseData = (LicenseData) getModel();

        if(licenseData.getLicenseFile() ==null){
            addActionError("No file found in the request");
            prepareLicenseResponse(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessageCode(),"No file found in the request");
            return new DefaultHttpHeaders(Results.ERROR.getValue()).disableCaching();
        }

        try {
            //Step 1: Check for file validity.
            String plainTextLicense =SystemUtil.readStoredLicense(licenseData.getLicenseFile());
            validateLicenseFile(plainTextLicense);

            String encryptedLicense = encrypt(plainTextLicense);

            long time = System.currentTimeMillis();
            SMLicenseData smLicenseData = new SMLicenseData();
            smLicenseData.setId(UUID.randomUUID().toString());
            smLicenseData.setLicense(encryptedLicense.getBytes());
            smLicenseData.setUpdatedTime(new Timestamp(time));

            //Step 2: Delete existing entries.
            List<SMLicenseData> licenseDataList = CRUDOperationUtil
                    .findAll(SMLicenseData.class);
            for(SMLicenseData smLicense: licenseDataList){
                CRUDOperationUtil
                        .delete(smLicense);
            }

            //Step 3: Delete NetVertex server license entries.
            SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
            singleLicenseManager.add(plainTextLicense, Version.getMajorVersion());

            List<NVLicenseData> nvLicenseDataList = CRUDOperationUtil
                    .findAll(NVLicenseData.class);
            if (singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_MAX_INSTANCES, Integer.toString(nvLicenseDataList.size())) == false) {
                Collections.sort(nvLicenseDataList, (lic1, lic2) -> Long.compare(lic1.getLastUpdateTime().getTime(), lic2.getLastUpdateTime().getTime()));
                int allowedLicenseCount = Integer.parseInt(singleLicenseManager.getLicenseValue("SYSTEM_MAX_INSTANCES"));
                for (int index = allowedLicenseCount; index < nvLicenseDataList.size(); index++) {
                    CRUDOperationUtil.delete(nvLicenseDataList.get(index));
                }
            }

            //Step 4: Store new license in DB.
            CRUDOperationUtil.save(smLicenseData);
            prepareLicenseResponse(LicenseMessages.SUCCESS.getMessageCode(),LicenseMessages.SUCCESS.getMessage());
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).disableCaching();
        } catch (InvalidLicenseKeyException e){
            getLogger().error(getLogModule(),"InvalidLicenseKeyException occurred. Reason: "+e.getMessage());
            getLogger().trace(e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            prepareLicenseResponse(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessageCode(),e.getMessage());
            return new DefaultHttpHeaders(Results.ERROR.getValue()).disableCaching();
        } catch (Exception e){
            getLogger().error(getLogModule(),"Exception occurred. Reason: "+e.getMessage());
            getLogger().trace(e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            prepareLicenseResponse(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessageCode(),e.getMessage());
            return new DefaultHttpHeaders(Results.ERROR.getValue()).disableCaching();
        }
    }

    private void validateLicenseFile(String licenseKey) throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();

        if(Strings.isNullOrBlank(licenseKey)){
            throw new InvalidLicenseKeyException(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessage());
        }

        singleLicenseManager.add(licenseKey, Version.getMajorVersion());

        try{
            if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_NODE,new DefaultDecryptor().decryptPublicKey(DefaultNVSMXContext.getContext().getPublicKey()))==false){
                if(getLogger().isWarnLogLevel()){
                    getLogger().warn(getLogModule(),"Rejecting license. Reason: Invalid license file uploaded");
                }
                throw new InvalidLicenseKeyException(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessage());
            }
        } catch (InvalidPublickeyException e){
            getLogger().error(getLogModule(),"InvalidPublickeyException occurred while generating public key. " +
                    "Reason: "+e.getMessage());
            getLogger().trace(e);
            addActionError("Couldn't generate public key. please contact support.");
            throw new InvalidLicenseKeyException("License file validation failed because of failure" +
                    " in generating public key. Reason: "+e.getMessage());
        }

        // It will be -1 when SYSTEM_MAX_INSTANCES is unlimited
        if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_MAX_INSTANCES, Integer.toString(-1))==false){
            if(getLogger().isWarnLogLevel()){
                getLogger().warn(getLogModule(),"Rejecting license. Reason: Invalid license file uploaded");
            }
            throw new InvalidLicenseKeyException(LicenseMessages.INVALID_LICENSE_FILE_UPLOADED.getMessage());
        }

        if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY, Long.toString(System.currentTimeMillis()))==false){
            if(getLogger().isWarnLogLevel()){
                getLogger().warn(getLogModule(),"Rejecting license. Reason: Invalid license file uploaded");
            }
            throw new InvalidLicenseKeyException(LicenseMessages.LICENSE_FILE_EXPIRED.getMessage());
        }

    }

    private String encrypt(String plainString) throws NoSuchAlgorithmException {
        try {
            return AES.encrypt(plainString);
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e.getMessage(), e);
        }
    }

    private void prepareLicenseResponse(int resultCode, String message){
        LicenseData model = (LicenseData)getModel();

        if(model!=null){
            model.setId(null);
            model.setLicenseFile(null);
            model.setStatus(null);
            model.setMessageCode(resultCode);
            model.setMessage(message);
        }
    }

    public InputStream getFileInputStream()
    {
        return fileInputStream;
    }
    public String getFileName()
    {
        return DefaultNVSMXContext.getContext().getLocalHostName()+".pubkey";
    }
    public String getContentType()
    {
        return contentType;
    }

    @SkipValidation
    public HttpHeaders downloadPublicKey(){
        try{
            fileInputStream = new ByteArrayInputStream(DefaultNVSMXContext.getContext().getPublicKey().getBytes(StandardCharsets.UTF_8.name()));
            return new DefaultHttpHeaders(Results.DOWNLOAD.getValue());
        } catch (InvalidPublickeyException e){
            getLogger().error(getLogModule(),"Can not download public key. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage());
            return new DefaultHttpHeaders(LicenseMessages.PUBLIC_KEY_GENERATION_ERROR.getMessage()).disableCaching().withStatus(LicenseMessages.PUBLIC_KEY_GENERATION_ERROR.getMessageCode());

        } catch (UnsupportedEncodingException e){
            getLogger().error(getLogModule(),"Can not download public key. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage());
            return new DefaultHttpHeaders(LicenseMessages.INVALID_ENCODING.getMessage()).disableCaching().withStatus(LicenseMessages.INVALID_ENCODING.getMessageCode());

        }
    }

    @Nullable
    private String getParsedLicense(SMLicenseData smLicenseData) throws Exception{
            String encryptedLicense = new String(smLicenseData.getLicense());
            smLicenseData.getUpdatedTime();
            if(encryptedLicense!=null){
                return AES.decrypt(encryptedLicense);
            }
        return null;
    }

    @Override
    public HttpHeaders show() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(),"Method called show()");
        }
        try {

            List<SMLicenseData> smLicenseDataList = CRUDOperationUtil.findAll(SMLicenseData.class);
            String smLicenseFromDB = null;

            if(Collectionz.isNullOrEmpty(smLicenseDataList)==false){
                SMLicenseData smLicenseData =smLicenseDataList.get(0);
                smLicenseFromDB = getParsedLicense(smLicenseData);
                lastUpdateTime =smLicenseData.getUpdatedTime();
            }

            if(Strings.isNullOrBlank(smLicenseFromDB) == false){
                Map<String, com.elitecore.license.base.LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(smLicenseFromDB);
                if(licenseDataMap!=null){
                    List<LicenseData> licenseDataListTemp = Collectionz.newArrayList();
                    for (Map.Entry<String, com.elitecore.license.base.LicenseData> entry : licenseDataMap.entrySet()) {
                        com.elitecore.license.base.LicenseData licenseData = entry.getValue();
                        LicenseData licenseDataTemp = new LicenseData();
                        licenseDataTemp.setKey(licenseData.getName());
                        licenseDataTemp.setValue(licenseData.getValue());
                        licenseDataTemp.setVersion(licenseData.getVersion());
                        licenseDataListTemp.add(licenseDataTemp);
                    }

                    setLicenseDataList(licenseDataListTemp);
                    setNvLicenseDataList(CRUDOperationUtil.findAll(NVLicenseData.class));
                    for(NVLicenseData nvLicenseData : getNvLicenseDataList()) {
                        nvLicenseData.setInstanceName(AES.decrypt(nvLicenseData.getInstanceName()));
                    }
                }
            }
        } catch (Exception e) {
            addActionError("Failed to view " + getModule().getDisplayLabel());
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + ". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }

        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }

    public List<LicenseData> getLicenseDataList() {
        return licenseDataList;
    }

    public String getLicenseDataListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJsonTree(getLicenseDataList(),new TypeToken<List<LicenseData>>() {}.getType()).getAsJsonArray().toString();
    }

    public String getInstancesListAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJsonTree(getNvLicenseDataList(),new TypeToken<List<LicenseData>>() {}.getType()).getAsJsonArray().toString();
    }

    public void setLicenseDataList(List<LicenseData> licenseDataList) {
        this.licenseDataList = licenseDataList;
    }

    public List<NVLicenseData> getNvLicenseDataList() {
        return nvLicenseDataList;
    }

    public void setNvLicenseDataList(List<NVLicenseData> nvLicenseDataList) {
        this.nvLicenseDataList = nvLicenseDataList;
    }

    public String getLastUpdatedTime() {
        return new SimpleDateFormat("dd MMM yyyy kk:mm:ss").format(lastUpdateTime);
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
