package com.elitecore.diameterapi.diameter.common.peers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.commons.tls.checker.CertificateSubjectCnChecker;
import com.elitecore.diameterapi.diameter.common.data.PeerData;

public class DiameterCertificateSubjectCnChecker extends
		CertificateSubjectCnChecker {
	
	private PeerData peerData;

	public DiameterCertificateSubjectCnChecker(boolean ignoreException, PeerData peerData) {
		super(ignoreException);
		this.peerData = peerData;
	}

	@Override
	public List<String> getPossibleSubjectCN() {
		
		ArrayList<String> subjectsCN = new ArrayList<String>();
		
		String name = peerData.getPeerName();
		if(name != null){ subjectsCN.add(name);}
		
		String hostIdentity = peerData.getHostIdentity();
		if(hostIdentity != null){ subjectsCN.add(hostIdentity);}
	
		InetAddress inetAddress = peerData.getRemoteInetAddress();
		if(inetAddress != null){
			subjectsCN.add(inetAddress.getHostName());
			subjectsCN.add(inetAddress.getHostAddress());	
		}
		
		return subjectsCN;
	}

}
