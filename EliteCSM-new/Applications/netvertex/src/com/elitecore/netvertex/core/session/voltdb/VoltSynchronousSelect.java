package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltSynchronousSelect {

    private static final String MODULE = "VOLT-SELECT-OPERATION";
    private VoltDBClient voltDBClient;

    public VoltSynchronousSelect(VoltDBClient voltDBClient) {
        this.voltDBClient = voltDBClient;
    }

    public List<SessionData> list(VoltCriteriaData voltCriteriaData) {

        try {

            long queryExecutionTime = System.currentTimeMillis();
            ClientResponse clientResponse;

            if(isNullOrBlank(voltCriteriaData.getSimpleValue()) == false){
                clientResponse = voltDBClient.callProcedure(voltCriteriaData.getStoredProcedureName()
                        , voltCriteriaData.getSimpleKey() , voltCriteriaData.getSimpleValue());
            } else{

                List<String> sessionIds = voltCriteriaData.getSessionIdValues();
                List<String> subscriberIds = voltCriteriaData.getSubscriberIdValues();
                List<String> sessionIPv4s = voltCriteriaData.getSessionIpV4Values();
                List<String> sessionIPv6s = voltCriteriaData.getSessionIpV6Values();

                clientResponse = voltDBClient.callProcedure(voltCriteriaData.getStoredProcedureName()
                        , isNullOrEmpty(sessionIds) ? null : sessionIds.toArray(new String[sessionIds.size()])
                        , isNullOrEmpty(subscriberIds) ? null : subscriberIds.toArray(new String[subscriberIds.size()])
                        , isNullOrEmpty(sessionIPv4s) ? null : sessionIPv4s.toArray(new String[sessionIPv4s.size()])
                        , isNullOrEmpty(sessionIPv6s) ? null : sessionIPv6s.toArray(new String[sessionIPv6s.size()])
                );
            }

            queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
            if(queryExecutionTime > 100) {
                if(getLogger().isLogLevel(LogLevel.WARN)) {
                    getLogger().warn(MODULE, "Query execution time getting high for list session operation, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }

            if (Objects.isNull(clientResponse) || clientResponse.getStatus() != ClientResponse.SUCCESS) {
                return null;
            }

            VoltTable[] results = clientResponse.getResults();

            if (results.length == 0) {
                return null;
            }

            VoltTable vt = results[0];

            List<SessionData> sessionDatas = new ArrayList<>();

            SchemaMapping schemaMapping = voltCriteriaData.getSchemaMapping();

            while (vt.advanceRow()) {
                SessionDataImpl session = new SessionDataImpl(schemaMapping.getSchemaName(),
                        "",
                        vt.getTimestampAsSqlTimestamp(schemaMapping.getCreationTime().getColumnName()),
                        vt.getTimestampAsSqlTimestamp(schemaMapping.getLastUpdateTime().getColumnName()));

                for (FieldMapping fieldMapping : schemaMapping.getFieldMappings()) {

                    VoltType columnType = vt.getColumnType(vt.getColumnIndex(fieldMapping.getColumnName()));
                    if (columnType == VoltType.STRING) {
                        session.addValue(fieldMapping.getPropertyName(), vt.getString(fieldMapping.getColumnName()));
                    } else if (columnType == VoltType.INTEGER) {
                        long longValue = vt.getLong(fieldMapping.getColumnName());
                        session.addValue(fieldMapping.getPropertyName(), vt.wasNull() ? null : Long.toString(longValue));
                    } else if (columnType == VoltType.FLOAT) {
                        double doubleValue = vt.getDouble(fieldMapping.getColumnName());
                        session.addValue(fieldMapping.getPropertyName(), vt.wasNull() ? null : Double.toString(doubleValue));
                    } else if (columnType == VoltType.TIMESTAMP) {
                        Timestamp timestampValue = vt.getTimestampAsSqlTimestamp(fieldMapping.getColumnName());
                        session.addValue(fieldMapping.getPropertyName(), timestampValue.toString());
                    }
                }
                session.setSessionLoadTime(queryExecutionTime);
                sessionDatas.add(session);
            }
            return sessionDatas;

        } catch (Exception e) {
            getLogger().error(MODULE, "Error in selecting session, Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

        }
        return null;
    }
}