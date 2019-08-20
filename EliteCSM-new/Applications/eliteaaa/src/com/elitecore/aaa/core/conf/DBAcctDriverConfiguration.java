package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.conf.impl.DbFiledMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface DBAcctDriverConfiguration extends DriverConfiguration{
	
	public String getDsType();

	public String getCdrTableName();

	public String getInterimTableName();

	public boolean getStoreStopRec();

	public boolean getStoreInterimRec();

	public boolean getRemoveInterimOnStop();

	public int getDbQueryTimeout();

	public int getMaxQueryTimeoutCount();

	public String getMultivalDelimeter();

	public String getDbDateField();

	public boolean getEnebled();

	public String getCdrIdDbField();

	public String getCdrIdSeqName();

	public String getInterimCdrIdDbField();

	public String getInterimCdrIdSeqName();

	public String getCallStartFieldName();

	public String getCallEndFieldName();

	public String getCreateDateFieldName();

	public String getLastModifiedDateFieldName() ;

	public List<DbFiledMapping> getDbFiledMappingList();
	
	public String getDsName();
	
	public String getIntrimCDRAcctSessionId();
	
	// Queries..
	public String getCDRInsertQuery();
	public String getCDRInterimInsertQuery();
	public String getCDRInterimUpdateQuery();
	public String getCDRInterimDeleteQuery();

	public boolean getStoreAllCDR();

	public boolean isStoreStartRecordEnabled();

	public boolean isStoreUpdateRecordEnabled();
}
