package com.elitecore.netvertex.license;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.license.base.License;
import com.elitecore.license.base.LicenseManager;
import com.elitecore.license.base.LicenseObserver;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.LicenseDecryptor;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EvaluationLicenseManager implements LicenseManager {

    private long evaluationLicenseTPS;
    private long systemExpiryTime;

    public EvaluationLicenseManager(long evaluationLicenseTPS, int evaluationDays) {
        this.evaluationLicenseTPS = evaluationLicenseTPS;
        this.systemExpiryTime = System.currentTimeMillis()+TimeUnit.DAYS.toMillis(evaluationDays);

    }

    public void add(String licenseKey, String version) throws InvalidLicenseKeyException {
        //Sonar ignore
    }

    public void add(String licenseKey, String version, LicenseDecryptor decryptor) throws InvalidLicenseKeyException {
        //Sonar ignore
    }

    public void add(File licenseFile, String version) throws InvalidLicenseKeyException {
        //Sonar ignore
    }

    public boolean validateLicense(String key, String value) {
        if (LicenseNameConstants.SYSTEM_EXPIRY.equals(key)) {
            if (systemExpiryTime <= System.currentTimeMillis()) {
                return false;
            }
        }

        if (LicenseNameConstants.SYSTEM_TPS.equals(key)) {

            long tpsInLong;
            try{
                tpsInLong = Long.parseLong(value);
            }catch(Exception e){
                LogManager.ignoreTrace(e);
                return false;
            }

            if (tpsInLong > evaluationLicenseTPS) {
                return false;
            }
        }

        return true;
    }

    public String getLicenseExpiryDate(){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(systemExpiryTime));
    }

    public String getLicenseValue(String key){
        if (LicenseNameConstants.SYSTEM_TPS.equals(key)) {
            return Long.toString(evaluationLicenseTPS);
        }

        return null;
    }

    public String getLicenseKey(){
        return null;
    }

    public void uploadLicense(String license, String version){
        //Sonar ignore
    }

    public long getRemainedDaysToExpire() throws Exception{
        return 0;
    }
    public void registerObserver(LicenseObserver observer){
        //SONAR ignore
    }

    public void deregisterLicense(){
        //SONAR ignore
    }

    public License.LicenseGenerator upgrade() {
        return null;
    }
}
