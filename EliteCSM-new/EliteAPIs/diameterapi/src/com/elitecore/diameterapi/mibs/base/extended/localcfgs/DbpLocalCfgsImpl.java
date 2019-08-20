package com.elitecore.diameterapi.mibs.base.extended.localcfgs;

import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.mibs.base.autogen.DbpLocalCfgs;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvToPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpLocalApplTable;

public class DbpLocalCfgsImpl extends DbpLocalCfgs {

	private static final long serialVersionUID = 1L;
	private TableDbpAppAdvToPeerTable tableDbpAppAdvToPeerTable;
	private TableDbpLocalApplTable tableDbpLocalApplTable;

	@Override
	public String getDbpLocalRealm() {
		return Parameter.getInstance().getOwnDiameterRealm();
	}

	@Override
	public void setDbpLocalRealm(String x) {}

	@Override
	public void checkDbpLocalRealm(String x) {}

	@Override
	public String getDbpLocalOriginHost() {
		return Parameter.getInstance().getOwnDiameterIdentity();
	}

	@Override
	public void setDbpLocalOriginHost(String x) {}

	@Override
	public void checkDbpLocalOriginHost(String x) {}

	@Override
	public Long getDbpLocalSctpListenPort() {
		return 0L;
	}

	@Override
	public Long getDbpLocalTcpListenPort() {
		return (long) Parameter.getInstance().getHostListeningPort();
	}

	@Override
	public String getDbpLocalId() {
		return Parameter.getInstance().getOwnDiameterIdentity();
	}

	@Override
	public TableDbpAppAdvToPeerTable accessDbpAppAdvToPeerTable() {
		return tableDbpAppAdvToPeerTable;
	}

	@Override
	public TableDbpLocalApplTable accessDbpLocalApplTable() {
		return tableDbpLocalApplTable;
	}

	public void setTableDbpAppAdvToPeerTable(
			TableDbpAppAdvToPeerTable tableDbpAppAdvToPeerTable) {
		this.tableDbpAppAdvToPeerTable = tableDbpAppAdvToPeerTable;
	}

	public void setTableDbpLocalApplTable(
			TableDbpLocalApplTable tableDbpLocalApplTable) {
		this.tableDbpLocalApplTable = tableDbpLocalApplTable;
	}

}
