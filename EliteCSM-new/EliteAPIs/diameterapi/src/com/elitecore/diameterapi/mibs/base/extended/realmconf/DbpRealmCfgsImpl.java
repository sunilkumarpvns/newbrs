package com.elitecore.diameterapi.mibs.base.extended.realmconf;

import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmCfgs;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmCfgsMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpRealmKnownPeersTable;
import com.sun.management.snmp.SnmpStatusException;

public class DbpRealmCfgsImpl extends DbpRealmCfgs {

	@Override
	public TableDbpRealmKnownPeersTable accessDbpRealmKnownPeersTable()
			throws SnmpStatusException {
		// TODO Currently not implemented but will implemented in future
		return null;
	}

}
