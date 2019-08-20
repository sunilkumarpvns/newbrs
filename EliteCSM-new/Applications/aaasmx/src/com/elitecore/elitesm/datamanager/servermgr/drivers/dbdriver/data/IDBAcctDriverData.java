package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;

import java.util.HashSet;

public interface IDBAcctDriverData {


	public Long getOpenDbAcctId() ;
	public Long getDatabaseId() ;
	public String getDatasourceType() ;
	public Long getDatasourceScantime() ;
	public String getCdrTablename() ;
	public String getInterimTablename();
	public String getStoreStopRec() ;
	public String getStoreInterimRec() ;
	public String getRemoveInterimOnStop() ;
	public String getStoreTunnelStartRec() ;
	public String getStoreTunnelStopRec() ;
	public String getRemoveTunnelStopRec();
	public String getStoreTunnelLinkStartRec() ;
	public String getStoreTunnelLinkStopRec() ;
	public String getRemoveTunnelLinkStopRec() ;
	public String getStoreTunnelRejectRec() ;
	public String getStoreTunnelLinkRejectRec();
	public Long getDbQueryTimeout() ;
	public Long getMaxQueryTimeoutCount() ;
	public String getMultivalDelimeter() ;
	public String getDbDateField() ;
	public String getEnabled() ;
	public String getCdrIdDbField();
	public String getCdrIdSeqName() ;
	public String getInterimCdrIdDbField() ;
	public String getInterimCdrIdSeqName() ;
	public String getCallStartFieldName() ;
	public String getCallEndFieldName() ;
	public String getCreateDateFieldName();
	public String getLastModifiedDateFieldName() ;
	public HashSet getDbAcctFeildMapSet() ;
	
	
}
