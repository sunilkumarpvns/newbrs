package com.elitecore.coreeap.util.tls.keyexchange;

public interface KeyExchange {
	public byte[] generateParameters();
	public byte[] generatePMS(byte[] keyExchangeValue);
}
