package com.elitecore.aaa.rm.conf.impl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.conf.impl.PeersConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "peers")
@ConfigurationProperties(moduleName = "RM-DIA-PEERS-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "peer", schemaDirectories = {"system","schema"},configDirectories = {"conf","diameter"})
public class RMDiameterPeerConfigurable extends PeersConfigurable {

	private static final String MODULE ="RM-DIA-PEERS-CONFIGURABLE";
	
	public RMDiameterPeerConfigurable() {
		// Do nothing this use by JAXB
	}
	
	@Override
	protected String getQueryForPeers() {
		DiameterStackConfigurable diameterStackConfigurable = getConfigurationContext().get(DiameterStackConfigurable.class);
		Set<String> peerNamesEligibleForReading = new HashSet<String>(diameterStackConfigurable.getSelectedPeerConfDetail().getPeerList());

		String routingTable = diameterStackConfigurable.getRoutingTableName();

		if(peerNamesEligibleForReading.contains(AAAServerConstants.ALL)){
			return "SELECT DISTINCT A.PEERUUID,A.PEERID,A.PEERNAME,A.HOSTIDENTITY,A.REALMNAME,A.REMOTEADDRESS,A.LOCALADDRESS,A.URIFORMAT,A.SECONDARYPEER,A.REQUESTTIMEOUT,A.RETRANSMISSIONCOUNT,A.CREATEDATE,B.PROFILENAME FROM TBLMPEER A, TBLMPEERPROFILE B WHERE A.PEERPROFILEID = B.PEERPROFILEID";
		}else if (peerNamesEligibleForReading.contains(AAAServerConstants.ROUTING)) {
			return getQueryForRoutingTablePeer(routingTable)+" UNION "+"SELECT DISTINCT A.PEERUUID,A.PEERID,A.PEERNAME,A.HOSTIDENTITY,A.REALMNAME,A.SECONDARYPEER,A.REMOTEADDRESS,A.LOCALADDRESS,A.URIFORMAT,A.REQUESTTIMEOUT,A.RETRANSMISSIONCOUNT,A.CREATEDATE,B.PROFILENAME FROM TBLMPEER A, TBLMPEERPROFILE B WHERE ((A.PEERPROFILEID = B.PEERPROFILEID) AND A.PEERNAME IN ("+convertSetToStringForWhereClause(peerNamesEligibleForReading)+"))";
		}else {
			return "SELECT DISTINCT A.PEERUUID,A.PEERID,A.PEERNAME,A.HOSTIDENTITY,A.REALMNAME,A.REMOTEADDRESS,A.LOCALADDRESS,A.URIFORMAT,A.SECONDARYPEER,A.REQUESTTIMEOUT,A.RETRANSMISSIONCOUNT,A.CREATEDATE,B.PROFILENAME FROM TBLMPEER A, TBLMPEERPROFILE B WHERE ((A.PEERPROFILEID = B.PEERPROFILEID) AND A.PEERNAME IN ("+convertSetToStringForWhereClause(peerNamesEligibleForReading)+"))";
		}
	}

	private String getQueryForRoutingTablePeer(String routingTable) {
		return "SELECT A.PEERUUID,A.PEERID,A.PEERNAME,A.HOSTIDENTITY,A.REALMNAME,A.REMOTEADDRESS,A.LOCALADDRESS,A.URIFORMAT,A.SECONDARYPEER,A.REQUESTTIMEOUT,A.RETRANSMISSIONCOUNT,A.CREATEDATE,B.PROFILENAME FROM TBLMPEER A,TBLMPEERPROFILE B WHERE ((PEERUUID IN ("+
				"SELECT PEERUUID FROM TBLMPEERGROUPREL WHERE PEERGROUPID in("+
				"SELECT PEERGROUPID FROM TBLMPEERGROUP WHERE ROUTINGCONFIGID IN (SELECT ROUTINGCONFIGID FROM TBLMROUTINGCONFIG WHERE ROUTINGTABLEID IN"+
				"(SELECT ROUTINGTABLEID FROM TBLMROUTINGTABLE WHERE ROUTINGTABLENAME='"+routingTable+"'))))) AND (A.peerprofileid = B.peerprofileid))";
	}
	
	private String convertSetToStringForWhereClause(Set<String> set) {
		return Strings.join(",", set, Strings.WITHIN_SINGLE_QUOTES);
	}

	@Override
	protected String getModule() {
		return MODULE;
	}
}
