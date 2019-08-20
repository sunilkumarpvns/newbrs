package com.elitecore.coreeap.util.tls;

import java.security.PrivateKey;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;
import com.elitecore.coreeap.util.tls.signature.AnonSignature;
import com.elitecore.coreeap.util.tls.signature.DSASignature;
import com.elitecore.coreeap.util.tls.signature.RSASignature;
import com.elitecore.coreeap.util.tls.signature.Signature;
import com.elitecore.coreeap.util.tls.signature.Tls1_2DSASignature;
import com.elitecore.coreeap.util.tls.signature.Tls1_2RSASignature;

public class SignatureFactory {

	private static final String MODULE = "SIGNATURE_FACTORY";

	private boolean isTestMode = false;
	
	public SignatureFactory(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}
	
	public Signature createSignature(SignatureAlgorithm signatureAlgo, ProtocolVersion protocolVersion, PrivateKey privateKey){
		Signature signature = null;
		switch (signatureAlgo) {
		case RSA:
			signature = (protocolVersion == ProtocolVersion.TLS1_2)
			?new Tls1_2RSASignature(privateKey,isTestMode)
			:new RSASignature(privateKey,isTestMode);
			break;
		case DSA:
			signature = (protocolVersion == ProtocolVersion.TLS1_2)
			?new Tls1_2DSASignature(privateKey,isTestMode)
			:new DSASignature(privateKey,isTestMode);
			break;
		case ANONYMOUS:
			signature = new AnonSignature(privateKey,isTestMode);
			break;
		default:
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, signatureAlgo.name() + " is not supported");
				break;
			}
		}
		return signature;
	}
}
