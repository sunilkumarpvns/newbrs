package com.elitecore.diameterapi.mibs.base.extended.localcfgs;

import com.elitecore.diameterapi.mibs.base.autogen.DbpLocalApplEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpLocalApplRowStatus;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpLocalApplStorageType;
import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerAppAdvTable;


public class DbpLocalApplEntryImpl extends DbpLocalApplEntry {
	
	private static final long serialVersionUID = 1L;
	private static EnumDbpLocalApplRowStatus enumDbpLocalApplRowStatus;
	private static EnumDbpLocalApplStorageType enumDbpLocalApplStorageType;
	transient private DiameterBasePeerAppAdvTable diameterBasePeerAppAdvTable;
	
	static {
		enumDbpLocalApplRowStatus = new EnumDbpLocalApplRowStatus(RowStatus.ACTIVE.code); // Active
		enumDbpLocalApplStorageType = new EnumDbpLocalApplStorageType(StorageTypes.PARMANENT.code); // Permanent
	}
	
	
	public DbpLocalApplEntryImpl(DiameterBasePeerAppAdvTable diameterBasePeerAppAdvTable) {
		this.diameterBasePeerAppAdvTable = diameterBasePeerAppAdvTable;
	}

	@Override
	public EnumDbpLocalApplRowStatus getDbpLocalApplRowStatus() {
		return enumDbpLocalApplRowStatus;
	}

	@Override
	public void setDbpLocalApplRowStatus(EnumDbpLocalApplRowStatus x) {

	}

	@Override
	public EnumDbpLocalApplStorageType getDbpLocalApplStorageType(){
		return enumDbpLocalApplStorageType;
	}

	@Override
	public void setDbpLocalApplStorageType(EnumDbpLocalApplStorageType x) {

	}

	@Override
	public void checkDbpLocalApplStorageType(EnumDbpLocalApplStorageType x) {

	}

	@Override
	public Long getDbpLocalApplIndex() {
		return diameterBasePeerAppAdvTable.getDbpAppId();
	}

	public String getObjectName(){
		return SnmpAgentMBeanConstant.LOCAL_APP_TABLE + diameterBasePeerAppAdvTable.getAppAdvName();
	}
}
