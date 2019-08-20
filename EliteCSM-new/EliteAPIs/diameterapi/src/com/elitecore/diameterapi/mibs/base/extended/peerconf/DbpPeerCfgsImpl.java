package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerCfgs;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvFromPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerIpAddrTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerVendorTable;
import com.sun.management.snmp.SnmpStatusException;

public class DbpPeerCfgsImpl extends DbpPeerCfgs {

	private static final long serialVersionUID = 1L;
	private TableDbpPeerVendorTable tableDbpPeerVendorTable;
	private TableDbpAppAdvFromPeerTable tableDbpAppAdvFromPeerTable;
	private TableDbpPeerIpAddrTable tableDbpPeerIpAddrTable;
	private TableDbpPeerTable tableDbpPeerTable;

	@Override
	public TableDbpPeerVendorTable accessDbpPeerVendorTable()
			throws SnmpStatusException {
		return tableDbpPeerVendorTable;
	}

	@Override
	public TableDbpAppAdvFromPeerTable accessDbpAppAdvFromPeerTable()
			throws SnmpStatusException {
		return tableDbpAppAdvFromPeerTable;
	}

	@Override
	public TableDbpPeerIpAddrTable accessDbpPeerIpAddrTable()
			throws SnmpStatusException {
		return tableDbpPeerIpAddrTable;
	}

	@Override
	public TableDbpPeerTable accessDbpPeerTable() throws SnmpStatusException {
		return tableDbpPeerTable;
	}

	public void setTableDbpPeerVendorTable(
			TableDbpPeerVendorTable tableDbpPeerVendorTable) {
		this.tableDbpPeerVendorTable = tableDbpPeerVendorTable;
	}

	public void setTableDbpAppAdvFromPeerTable(
			TableDbpAppAdvFromPeerTable tableDbpAppAdvFromPeerTable) {
		this.tableDbpAppAdvFromPeerTable = tableDbpAppAdvFromPeerTable;
	}

	public void setTableDbpPeerIpAddrTable(
			TableDbpPeerIpAddrTable tableDbpPeerIpAddrTable) {
		this.tableDbpPeerIpAddrTable = tableDbpPeerIpAddrTable;
	}

	public void setTableDbpPeerTable(TableDbpPeerTable tableDbpPeerTable) {
		this.tableDbpPeerTable = tableDbpPeerTable;
	}

}
