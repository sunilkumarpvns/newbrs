package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.SchemaMapping;

import java.util.List;

public class VoltCriteriaData {
    private String storedProcedureName;
    private SchemaMapping schemaMapping;
    private String simpleKey;
    private String simpleValue;

    private List<String> sessionIdValues;
    private List<String> subscriberIdValues;
    private List<String> sessionIpV4Values;
    private List<String> sessionIpV6Values;


    public VoltCriteriaData(String storedProcedureName, SchemaMapping schemaMapping,
                            String simpleKey, String simpleValue){
        this.storedProcedureName = storedProcedureName;
        this.schemaMapping = schemaMapping;
        this.simpleKey = simpleKey;
        this.simpleValue = simpleValue;
    }

    public VoltCriteriaData(String storedProcedureName, SchemaMapping schemaMapping, List<String> sessionIdValues
            , List<String> subscriberIdValues, List<String> sessionIpV4Values, List<String> sessionIpV6Values) {
        this.storedProcedureName = storedProcedureName;
        this.schemaMapping = schemaMapping;
        this.sessionIdValues = sessionIdValues;
        this.subscriberIdValues = subscriberIdValues;
        this.sessionIpV4Values = sessionIpV4Values;
        this.sessionIpV6Values = sessionIpV6Values;
    }

    public String getStoredProcedureName() {
        return storedProcedureName;
    }

    public SchemaMapping getSchemaMapping() {
        return schemaMapping;
    }

    public String getSimpleKey() {
        return simpleKey;
    }

    public String getSimpleValue() {
        return simpleValue;
    }

    public List<String> getSessionIdValues() {
        return sessionIdValues;
    }

    public List<String> getSubscriberIdValues() {
        return subscriberIdValues;
    }

    public List<String> getSessionIpV4Values() {
        return sessionIpV4Values;
    }

    public List<String> getSessionIpV6Values() {
        return sessionIpV6Values;
    }

}
