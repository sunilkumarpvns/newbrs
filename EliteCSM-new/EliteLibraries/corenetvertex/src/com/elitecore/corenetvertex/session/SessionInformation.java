package com.elitecore.corenetvertex.session;

import com.elitecore.commons.base.Collectionz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * This class is used to fetch sub session detail from session data
 * Created by dhyani on 19/4/17.
 */
public class SessionInformation {

    private String schemaName;
    private String sessionId;
    private Date creationTime;
    private Date lastUpdateTime;
    private Map<String,String> sessionInfo;
    private List<SessionRuleData> sessionRuleDatas;

    public SessionInformation(String schemaName, String sessionId, Date creationTime, Date lastUpdateTime) {
        this.schemaName = schemaName;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        sessionInfo = new LinkedHashMap<String, String>();
        sessionRuleDatas = Collectionz.newArrayList();
        this.sessionId = sessionId;
    }

    public String getValue(String key) {
        return sessionInfo.get(key);
    }

    public void addValue(String key, String value) {
        sessionInfo.put(key, value);
    }


    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Map<String, String> getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(Map<String, String> sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public List<SessionRuleData> getSessionRuleDatas() {
        return sessionRuleDatas;
    }

    public void setSessionRuleDatas(List<SessionRuleData> sessionRuleDatas) {
        this.sessionRuleDatas = sessionRuleDatas;
    }

    @Override
    public String toString() {
        StringWriter stringBuffer = new StringWriter();
        PrintWriter out = new PrintWriter( stringBuffer);
        out.println();
        out.println("Session ID : " + sessionId);
        for(Map.Entry<String, String> entry:sessionInfo.entrySet()){
            out.println(entry.getKey() + " : " + entry.getValue());
        }
        out.println();
        out.flush();
        out.close();
        return stringBuffer.toString();
    }

    public Set<String> getKeySet() {
        return sessionInfo.keySet();
    }

    public int compareTo(SessionInformation other) {
        return other.getLastUpdateTime().compareTo(this.getLastUpdateTime());
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        boolean flag = false ;
        if(obj == this )
            return true;
        if(! (obj instanceof SessionInformation))
            return false;
        SessionInformation sessionObj = (SessionInformation) obj;

        flag = this.getCreationTime().equals(sessionObj.getCreationTime()) &&
                this.getLastUpdateTime().equals(sessionObj.getLastUpdateTime()) &&
                this.getSchemaName().equals(sessionObj.getSchemaName()) &&
                this.getSessionId().equals(sessionObj.getSessionId());
        if( flag ) {
            this.sessionInfo.equals(sessionObj.sessionInfo);
        }
        return flag ;
    }


}
