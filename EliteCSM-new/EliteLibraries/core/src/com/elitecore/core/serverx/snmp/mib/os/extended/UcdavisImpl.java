package com.elitecore.core.serverx.snmp.mib.os.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableDskTable;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableLaTable;
import com.elitecore.core.serverx.snmp.mib.os.autogen.Ucdavis;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider;

public class UcdavisImpl extends Ucdavis{

	private static final long serialVersionUID = 1L;
	transient private final SystemDetailProvider systemDetailProvider;

	public UcdavisImpl(SystemDetailProvider systemDetailProvider) {
		this.systemDetailProvider = systemDetailProvider;
	}

	@Override
	@Table(name = "dskTable")
	public TableDskTable accessDskTable() {
		return this.systemDetailProvider.getDskTable();
	}
	
	@Override
	@Table(name = "laTable")
	public TableLaTable accessLaTable() {
		return this.systemDetailProvider.getLoadAverageTable();
	}
}
