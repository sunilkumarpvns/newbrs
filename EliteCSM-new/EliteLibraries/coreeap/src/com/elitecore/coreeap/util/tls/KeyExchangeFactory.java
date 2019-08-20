package com.elitecore.coreeap.util.tls;

import java.security.PrivateKey;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.tls.KeyExchangeAlgorithm;
import com.elitecore.coreeap.util.tls.keyexchange.DHKeyExchange;
import com.elitecore.coreeap.util.tls.keyexchange.KeyExchange;
import com.elitecore.coreeap.util.tls.keyexchange.RSAKeyExchange;

public class KeyExchangeFactory {

	private static final String MODULE = "KEY_EXCHANGE_FACTORY";
	
	private boolean isTestMode = false;
	
	public KeyExchangeFactory(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}
	
	public KeyExchange createKeyExchange(KeyExchangeAlgorithm keyExchangeAlgorithm, PrivateKey privateKey){
		KeyExchange keyExchange = null;
		
		switch (keyExchangeAlgorithm){
		case DHE:
			keyExchange = new DHKeyExchange(isTestMode);
			break;
		case RSA:
			keyExchange = new RSAKeyExchange(privateKey);
			break;
		default:
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, keyExchangeAlgorithm.name() + " is not supported");
			}
			break;
		}
		
		return keyExchange;
	}
}
