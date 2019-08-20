package com.elitecore.corenetvertex.pkg.rnc;

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dhyani.raval
 */
public enum VolumeUnitType {

    TOTAL("Total"){
        public long getVolumeInBytes(AllowedUsage allowedUsage){
            return allowedUsage.getTotalInBytes();
        }
        public long getVolume(AllowedUsage allowedUsage){
            return allowedUsage.getTotal();
        }
    },
    UPLOAD("Upload"){
        public long getVolumeInBytes(AllowedUsage allowedUsage){
            return allowedUsage.getUploadInBytes();
        }
        public long getVolume(AllowedUsage allowedUsage){
            return allowedUsage.getUpload();
        }
    },
    DOWNLOAD("Download"){
        public long getVolumeInBytes(AllowedUsage allowedUsage){
            return allowedUsage.getDownloadInBytes();
        }
        public long getVolume(AllowedUsage allowedUsage){
            return allowedUsage.getDownload();
        }
    },
    ;

    private String value;
    private static final Map<String,VolumeUnitType> map;
    static {
        map = new HashMap();
        for (VolumeUnitType volumeUnitType : values()) {
            map.put(volumeUnitType.name(), volumeUnitType);
        }
    }

    VolumeUnitType(String value) {
        this.value = value;
    }

    public static VolumeUnitType fromVolumeUnitType(String value) {
        return map.get(value);
    }
    public abstract long getVolumeInBytes(AllowedUsage allowedUsage);
    public abstract long getVolume(AllowedUsage allowedUsage);

    public String getValue() {
        return value;
    }
}
