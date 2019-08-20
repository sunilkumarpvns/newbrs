package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.DataTypeConstant;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverData;
import com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverFieldMappingData;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.DBCDRDriverConfigurationImpl;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.netvertex.core.util.ConfigurationUtil.stringToBoolean;

public class DBCDRDriverConfigurationFactory {

    private static final String MODULE = "DB-CDR-DRIVER-CONF-FACTORY";

    public DBCDRDriverConfigurationImpl create(DriverData driverData) throws LoadConfigurationException {

        DbCdrDriverData dbCdrDriverData = driverData.getDbCdrDriverData();
        int size = dbCdrDriverData.getBatchSize();
        if(size >= CommonConstants.BATCH_SIZE_MIN){
            dbCdrDriverData.setBatchSize(size);
        }else{
            LogManager.getLogger().warn(MODULE,"Using default value '"+dbCdrDriverData.getBatchSize()
                    +"' for 'Batch Size'. Reason: Configured 'Batch Size' "+size
                    +" is less than minimum required value '"+CommonConstants.BATCH_SIZE_MIN+"'");
        }

        String tempValue;
        tempValue = dbCdrDriverData.getTableName();
        if(tempValue == null)
            throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Table name not configured");

        tempValue = dbCdrDriverData.getIdentityField();
        if(tempValue == null)
            throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Identity field name not configured");

        tempValue = dbCdrDriverData.getSequenceName();
        if(tempValue == null)
            throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Sequence name not configured");

        boolean isStoreAllCDRs = stringToBoolean("stor all CDR", dbCdrDriverData.getStoreAllCdr(), true);

        tempValue = dbCdrDriverData.getSessionIdFieldName();
        if(tempValue == null)
            throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: SessionId field name not configured");
        dbCdrDriverData.setSessionIdFieldName(tempValue.trim());
        List<DBFieldMapping> fieldMappings = new ArrayList<>();
        fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getSessionIdFieldName(), PCRFKeyConstants.CS_SESSION_ID.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));

        String reportingType = dbCdrDriverData.getReportingType();

        if(reportingType != null && ("QR".equals(reportingType) || "UM".equals(reportingType))) {
            tempValue = dbCdrDriverData.getUsageKeyFieldName();
            if(tempValue != null && !tempValue.trim().isEmpty()) {
                fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getUsageKeyFieldName(), PCRFKeyConstants.CDR_USAGEKEY.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
            }

            tempValue = dbCdrDriverData.getInputOctetsFieldName();
            if(tempValue != null && !tempValue.trim().isEmpty()) {
                fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getInputOctetsFieldName(), PCRFKeyConstants.CDR_INPUTOCTETS.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
            }

            tempValue = dbCdrDriverData.getOutputOctetsFieldName();
            if(tempValue != null && !tempValue.trim().isEmpty()) {
                fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getOutputOctetsFieldName(), PCRFKeyConstants.CDR_OUTPUTOCTETS.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
            }

            tempValue = dbCdrDriverData.getTotalOctetsFieldName();
            if(tempValue != null && !tempValue.trim().isEmpty()) {
                fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getTotalOctetsFieldName(), PCRFKeyConstants.CDR_TOTALOCTETS.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
            }

            tempValue = dbCdrDriverData.getUsageTimeFieldName();
            if(tempValue != null && !tempValue.trim().isEmpty()) {
                fieldMappings.add(new DBFieldMapping(dbCdrDriverData.getUsageTimeFieldName(), PCRFKeyConstants.CDR_USAGETIME.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
            }
        }

        List<DbCdrDriverFieldMappingData> dbCdrDriverFieldMappingDataList = dbCdrDriverData.getDbCdrDriverFieldMappingDataList();

        List<DBFieldMapping> tempDBFieldMappings= new ArrayList<>(fieldMappings);
        for (DbCdrDriverFieldMappingData dbCdrDriverFieldMappingData : dbCdrDriverFieldMappingDataList) {
            tempDBFieldMappings.add(new DBFieldMapping(dbCdrDriverFieldMappingData.getDbField(), dbCdrDriverFieldMappingData.getPcrfKey(), new Integer(dbCdrDriverFieldMappingData.getDataType()), dbCdrDriverFieldMappingData.getDefaultValue()));
        }
        fieldMappings = tempDBFieldMappings;

        return new DBCDRDriverConfigurationImpl(driverData.getId(), driverData.getDriverType(), dbCdrDriverData.getDatabaseId(), dbCdrDriverData.getMaxQueryTimeoutCount(), dbCdrDriverData.getBatchSize()
                , isStoreAllCDRs, Boolean.parseBoolean(dbCdrDriverData.getBatchUpdate()),
                driverData.getName(), dbCdrDriverData.getTableName(), dbCdrDriverData.getIdentityField(), dbCdrDriverData.getSequenceName(),
                dbCdrDriverData.getSessionIdFieldName(), dbCdrDriverData.getReportingType(), dbCdrDriverData.getInputOctetsFieldName(),
                dbCdrDriverData.getOutputOctetsFieldName(),
                dbCdrDriverData.getTotalOctetsFieldName(), dbCdrDriverData.getUsageTimeFieldName(), dbCdrDriverData.getUsageKeyFieldName(),
                dbCdrDriverData.getTimeStampFieldName(),
                dbCdrDriverData.getCreateDateFieldName(), dbCdrDriverData.getLastModifiedDateFieldName(), fieldMappings);
    }
}
