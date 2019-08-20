package com.elitecore.aaa.diameter.conf.impl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;


@XmlType(propOrder = {})
@XmlRootElement(name = "peers")
@ConfigurationProperties(moduleName = "PEERS-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "peers",schemaDirectories = {"system","schema"},configDirectories = {"conf","diameter"})
public class DiameterPeersConfigurable extends PeersConfigurable{
	
	private static final String MODULE ="PEERS-CONFIGURABLE";
	
	public DiameterPeersConfigurable() {
		
	}

	protected String getQueryForPeers() {
		DiameterStackConfigurable diameterStackConfigurable = getConfigurationContext().get(DiameterStackConfigurable.class);
		Set<String> peerNamesEligibleForReading = new HashSet<String>(diameterStackConfigurable.getSelectedPeerConfDetail().getPeerList());
		
		AAAServerConfigurable serverConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		if (serverConfigurable.isServiceEnabled(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID)) {
			TGPPServerPolicyConfigurable policyConfigurable = getConfigurationContext().get(TGPPServerPolicyConfigurable.class);
			peerNamesEligibleForReading.addAll(policyConfigurable.getRequiredPeers());
		}
		
		String routingTable = diameterStackConfigurable.getRoutingTableName();
		
		if(peerNamesEligibleForReading==null || !(peerNamesEligibleForReading.size()>0) || peerNamesEligibleForReading.contains(AAAServerConstants.ALL)){
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
