package com.elitecore.coreeap.dictionary.tls;

import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Certificate;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.CertificateRequest;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.CertificateVerify;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientHello;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientKeyExchange;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Finished;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ITLSHandshakeMessage;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerHello;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerHelloDone;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerKeyExchange;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateRequest;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateVerify;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class TLSHandshakeMessageDictionary {
	private static final String MODULE = "HANDSHAKEMESSAGE DICTIONARY";
	static private TLSHandshakeMessageDictionary tlsHandshakeMessageDictionary;
	/**
	 * Contains TLS HandshakeRecord Type Code - TLSHandshakeRecord pair.
	 */
	private HashMap<ProtocolVersion, HashMap<Integer, ITLSHandshakeMessage>> protocolVersionToHandshakeMessages;
	 
	private TLSHandshakeMessageDictionary() {
		loadHandshakeMessageInstances();
	}
	 
	private void loadHandshakeMessageInstances() {		
		
		HashMap<Integer, ITLSHandshakeMessage> tlsv1_0andv1_1TypeToHandshakeMessages = new HashMap<Integer, ITLSHandshakeMessage>();
		HashMap<Integer, ITLSHandshakeMessage> tlsv1_2TypeToHandshakeMessages = new HashMap<Integer, ITLSHandshakeMessage>();
		protocolVersionToHandshakeMessages = new HashMap<ProtocolVersion, HashMap<Integer,ITLSHandshakeMessage>>();
		
		ClientHello clientHello = new ClientHello();		
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.ClientHello.value, clientHello);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.ClientHello.value, clientHello);
		
		ServerHello serverHello = new ServerHello();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerHello.value, serverHello);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerHello.value, serverHello);
		
		Certificate certificate = new Certificate();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.Certificate.value, certificate);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.Certificate.value, certificate);
		
		CertificateRequest certificateRequest = new CertificateRequest();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.CertificateRequest.value, certificateRequest);
		
		Tls1_2CertificateRequest tls1_2CertificateRequest = new Tls1_2CertificateRequest();
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.CertificateRequest.value, tls1_2CertificateRequest);
		
		ServerKeyExchange serverKeyExchange = new ServerKeyExchange();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerKeyExchange.value, serverKeyExchange);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerKeyExchange.value, serverKeyExchange);
		
		ServerHelloDone serverHelloDone = new ServerHelloDone();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerHelloDone.value, serverHelloDone);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.ServerHelloDone.value, serverHelloDone);
		
		ClientKeyExchange clientKeyExchange = new ClientKeyExchange();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.ClientKeyExchange.value, clientKeyExchange);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.ClientKeyExchange.value, clientKeyExchange);
		
		CertificateVerify certificateVerify = new CertificateVerify();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.CertificateVerify.value, certificateVerify);
		
		Tls1_2CertificateVerify tls1_2CertificateVerify = new Tls1_2CertificateVerify();
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.CertificateVerify.value, tls1_2CertificateVerify);
		
		Finished finished = new Finished();
		tlsv1_0andv1_1TypeToHandshakeMessages.put(HandshakeMessageConstants.Finished.value, finished);
		tlsv1_2TypeToHandshakeMessages.put(HandshakeMessageConstants.Finished.value, finished);
		
		protocolVersionToHandshakeMessages.put(ProtocolVersion.TLS1_0, tlsv1_0andv1_1TypeToHandshakeMessages);
		protocolVersionToHandshakeMessages.put(ProtocolVersion.TLS1_1, tlsv1_0andv1_1TypeToHandshakeMessages);
		
		protocolVersionToHandshakeMessages.put(ProtocolVersion.TLS1_2, tlsv1_2TypeToHandshakeMessages);
	}	 
	 
	public static TLSHandshakeMessageDictionary getInstance() {
		if(tlsHandshakeMessageDictionary == null){
			tlsHandshakeMessageDictionary = new TLSHandshakeMessageDictionary();
		}
		return tlsHandshakeMessageDictionary;
	}
	 
	public ITLSHandshakeMessage createHandshakeMessage(int TLSHandshakeMessageType, ProtocolVersion protocolVersion) {
		ITLSHandshakeMessage tlsHandshakeRecord = null;

		try {
			tlsHandshakeRecord = (ITLSHandshakeMessage)protocolVersionToHandshakeMessages.get(protocolVersion).get(TLSHandshakeMessageType).clone();
		} catch (CloneNotSupportedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during creating an handshake Messages,reason :"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		return tlsHandshakeRecord;
	}	 
}
