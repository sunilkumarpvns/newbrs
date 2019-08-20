package com.elitecore.nvsmx.integration.controlller.license;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.license.base.License;
import com.elitecore.license.base.SingleLicenseManager;
import com.elitecore.license.base.commons.LicenseMessages;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.base.exception.InvalidPublickeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import com.elitecore.license.crypt.DefaultEncryptor;
import com.elitecore.license.formatter.LicFormatter;
import com.elitecore.license.util.AES;
import com.elitecore.nvsmx.Version;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.license.LicenseData;
import com.elitecore.nvsmx.sm.model.license.NVLicenseData;
import com.elitecore.nvsmx.sm.model.license.SMLicenseData;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Projections;

import javax.annotation.Nullable;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;

@ParentPackage(value = "integration")
@Namespace("/integration/license")

public class LicenseCTRL extends RestGenericCTRL<LicenseData> {
    private static final String LICENSE_DATE_FORMAT = "dd/MM/yyyy";

    @Override
    public ACLModules getModule() {
        return ACLModules.LICENSE;
    }

    @Override
    public LicenseData createModel() {
        return new LicenseData();
    }

    @SkipValidation
    public HttpHeaders getLicense(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called getLicense()");
        }
        LicenseData licenseData = (LicenseData) getModel();

        try {
            //Step 0: validate parameters
            validateLicenseRequest();

            //Step 1: Check if server instance exists
            ServerInstanceData serverInstanceData=CRUDOperationUtil.get(ServerInstanceData.class,licenseData.getId());

            if(serverInstanceData == null){
                prepareLicenseResponse(LicenseMessages.NV_UNKNOWN_SERVER_INSTANCE.getMessageCode(),
                        LicenseMessages.NV_UNKNOWN_SERVER_INSTANCE.getMessage(),null);
                return new DefaultHttpHeaders(Results.ERROR.getValue());

            }

            if(Strings.isNullOrBlank(serverInstanceData.getName()) == false &&
                    serverInstanceData.getName().equals(licenseData.getInstanceName())==false){
                prepareLicenseResponse(LicenseMessages.NV_ID_NAME_MISMATCH_WITH_SM.getMessageCode(),
                        LicenseMessages.NV_ID_NAME_MISMATCH_WITH_SM.getMessage(),null);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            //Step 2: Validate with stored license data if any
            SingleLicenseManager singleLicenseManager = new SingleLicenseManager();

            //Read license data from DB. This license is uploaded by admin on SM.
            String licenseAsText = getLicenseFromDB();

            if(Strings.isNullOrBlank(licenseAsText)){
                prepareLicenseResponse(LicenseMessages.LICENSE_DOES_NOT_EXIST.getMessageCode(),
                        LicenseMessages.LICENSE_DOES_NOT_EXIST.getMessage(),null);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            singleLicenseManager.add(licenseAsText, Version.getMajorVersion());

            if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_NODE,new DefaultDecryptor().decryptPublicKey(DefaultNVSMXContext.getContext().getPublicKey()))==false){
                if(getLogger().isWarnLogLevel()){
                    getLogger().warn(getLogModule(),"Rejecting license. Reason: Invalid license file uploaded");
                }
                prepareLicenseResponse(LicenseMessages.INVALID_LICENSE_ON_SERVER_MANAGER.getMessageCode(),
                        LicenseMessages.INVALID_LICENSE_ON_SERVER_MANAGER.getMessage(),null);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            NVLicenseData storedLicenseData = CRUDOperationUtil.get(NVLicenseData.class,licenseData.getId());

            int activeLicensesCount  = getActiveLicenseCount();
            int allowedLicenseCount = Integer.parseInt(singleLicenseManager.getLicenseValue("SYSTEM_MAX_INSTANCES"));
            if(storedLicenseData != null){
                if (allowedLicenseCount < activeLicensesCount &&
                        allowedLicenseCount != Integer.parseInt(License.UNLIMITED_ACCESS)) {

                    CRUDOperationUtil.delete(storedLicenseData);
                    DefaultNVSMXContext.getContext().deregisterNetvertexServerLicense(serverInstanceData);
                    prepareLicenseResponse(LicenseMessages.LICENSE_ISSUED_BEYOND_LIMIT.getMessageCode(),
                            LicenseMessages.LICENSE_ISSUED_BEYOND_LIMIT.getMessage(),null);
                    return new DefaultHttpHeaders(Results.SUCCESS.getValue());
                } else {
                    storedLicenseData.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));

                    String licenseText = getLicenseKey(singleLicenseManager,licenseData);
                    CRUDOperationUtil.update(storedLicenseData);
                    prepareLicenseResponse(LicenseMessages.SUCCESS.getMessageCode(),
                            LicenseMessages.SUCCESS.getMessage(),
                            licenseText);
                    return new DefaultHttpHeaders(Results.SUCCESS.getValue());
                }
            }
            // If license de-registered and request arrives for new license then allocate new license.

            //Step 3: Check license limit

            if (allowedLicenseCount <= activeLicensesCount &&
                    allowedLicenseCount != Integer.parseInt(License.UNLIMITED_ACCESS)) {
                prepareLicenseResponse(LicenseMessages.LICENSE_LIMIT_REACHED.getMessageCode(),
                        LicenseMessages.LICENSE_LIMIT_REACHED.getMessage(),null);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            //Step 4: Register new license in database
            licenseData.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));

            NVLicenseData licenseDetails =getNVLicenseBean(licenseData);
            encrypt(licenseDetails);

            CRUDOperationUtil.save(licenseDetails);

            //Step 5: Send license for response
            prepareLicenseResponse(LicenseMessages.SUCCESS.getMessageCode(),
                    LicenseMessages.SUCCESS.getMessage(),
                    getLicenseKey(singleLicenseManager,licenseData));
            return new DefaultHttpHeaders(Results.SUCCESS.getValue());

        } catch (OperationFailedException e){
            getLogger().error(getLogModule(),"Request failed. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            prepareLicenseResponse(LicenseMessages.MISSING_REQUIRED_PARAMETERS.getMessageCode(),
                    e.getMessage(),null);
            return new DefaultHttpHeaders(LicenseMessages.MISSING_REQUIRED_PARAMETERS.getMessage()).disableCaching().withStatus(LicenseMessages.MISSING_REQUIRED_PARAMETERS.getMessageCode());
        } catch (InvalidLicenseKeyException e){
            getLogger().error(getLogModule(),"InvalidLicenseKeyException while serving license request. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            return new DefaultHttpHeaders(LicenseMessages.INVALID_LICENSE_KEY.getMessage()).disableCaching().withStatus(LicenseMessages.INVALID_LICENSE_KEY.getMessageCode());
        } catch (NoSuchAlgorithmException e) {
            getLogger().error(getLogModule(),"NoSuchAlgorithmException while serving license request. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            return new DefaultHttpHeaders(LicenseMessages.ENCRYPTION_ERROR.getMessage()).disableCaching().withStatus(LicenseMessages.ENCRYPTION_ERROR.getMessageCode());
        } catch (InvalidPublickeyException e) {
            getLogger().error(getLogModule(),"InvalidPublickeyException while serving license request. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            return new DefaultHttpHeaders(LicenseMessages.PUBLIC_KEY_GENERATION_ERROR.getMessage()).disableCaching().withStatus(LicenseMessages.PUBLIC_KEY_GENERATION_ERROR.getMessageCode());
        }

    }


    private NVLicenseData getNVLicenseBean(LicenseData licenseData){
        NVLicenseData nvLicenseData = new NVLicenseData();
        nvLicenseData.setInstanceName(licenseData.getInstanceName());
        nvLicenseData.setId(licenseData.getId());
        nvLicenseData.setIp(licenseData.getIp());
        nvLicenseData.setLastUpdateTime(licenseData.getLastUpdateTime());

        return nvLicenseData;
    }

    private void validateLicenseRequest() throws OperationFailedException{

        LicenseData licenseData = (LicenseData)getModel();

        StringBuilder message= new StringBuilder("One or more required parameters are missing: ");
        boolean isValid = true;

        if(isNullOrBlank(licenseData.getId())){
            message.append("id,");
            isValid = false;
        }

        if(isNullOrBlank(licenseData.getInstanceName())){
            message.append("instanceName,");
            isValid = false;
        }

        if(isNullOrBlank(licenseData.getIp())){
            message.append("ip,");
            isValid = false;
        }

        if(isNullOrBlank(licenseData.getKey())){
            message.append("key,");
            isValid = false;
        }

        message.subSequence(0, message.length());

        if(isValid==false){
            throw new OperationFailedException(message.toString());
        }

    }

    @Nullable
    private String getLicenseFromDB() throws NoSuchAlgorithmException{
        List<SMLicenseData> licenseDataList = CRUDOperationUtil.findAll(SMLicenseData.class);
        if(Collectionz.isNullOrEmpty(licenseDataList)==false){
            String encryptedLicense = new String(licenseDataList.get(0).getLicense());
            if(encryptedLicense!=null){
                return decrypt(encryptedLicense);
            }
        }
        return null;
    }

    private String getLicenseKey(SingleLicenseManager singleLicenseManager, LicenseData nvLicenseData) throws InvalidLicenseKeyException {

        long systemExpiryTime =System.currentTimeMillis()+72*3600*1000; // Set license validity for next 72 hours.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LICENSE_DATE_FORMAT);

        String licenseExpiry = singleLicenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY);

        if(Strings.isNullOrBlank(licenseExpiry)){
            Date date;
            try {
                date = simpleDateFormat.parse(licenseExpiry);
            } catch (ParseException e){
                throw new InvalidLicenseKeyException(LicenseMessages.INVALID_LICENSE_KEY.getMessage());
            }

            if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY, Long.toString(System.currentTimeMillis()))==false){
                throw new InvalidLicenseKeyException(LicenseMessages.LICENSE_EXPIRED.getMessage());
            }

            if(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY, Long.toString(systemExpiryTime))==false){
                systemExpiryTime = date.getTime();
            }
        }

        return singleLicenseManager.
                upgrade().
                modifyValue(LicenseNameConstants.SYSTEM_NODE, nvLicenseData.getKey()).
                modifyValue(LicenseNameConstants.SYSTEM_EXPIRY, simpleDateFormat.format(new Date(systemExpiryTime))). //Valid only for 72 hours
                remove(LicenseNameConstants.SYSTEM_MAX_INSTANCES).
                generate().
                format(new LicFormatter(new DefaultEncryptor()));
    }

    private int getActiveLicenseCount() {
        Object result = HibernateSessionFactory.getSession().createCriteria(NVLicenseData.class)
                .setProjection(Projections.rowCount()).uniqueResult();

        if(result!=null){
            return ((Long) result).intValue();
        } else {
            return 0;
        }
    }

    private void encrypt(NVLicenseData nvLicenseData) throws NoSuchAlgorithmException{
        try {
            nvLicenseData.setIp(AES.encrypt(nvLicenseData.getIp()));
            nvLicenseData.setInstanceName(AES.encrypt(nvLicenseData.getInstanceName()));
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e.getMessage(), e);
        }
    }

    private String decrypt(String encrypted) throws NoSuchAlgorithmException {
        try {
            return AES.decrypt(encrypted);
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e.getMessage(), e);
        }
    }

    private void prepareLicenseResponse(int resultCode, String message, String licenseKey){
        LicenseData model = (LicenseData)getModel();

        if(model!=null){
            model.setIp(null);
            model.setId(null);
            model.setKey(null);
            model.setInstanceName(null);
            model.setVersion(null);
            model.setStatus(null);
            model.setLastUpdateTime(null);
            model.setMessageCode(resultCode);
            model.setMessage(message);
            model.setLicenseKey(licenseKey);
        }
    }

}
