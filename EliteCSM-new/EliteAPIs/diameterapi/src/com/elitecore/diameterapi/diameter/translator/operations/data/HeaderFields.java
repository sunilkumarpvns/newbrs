package com.elitecore.diameterapi.diameter.translator.operations.data;


public interface HeaderFields<P> {
	public boolean apply(P packet, String value) throws NumberFormatException;
	public String key();
}
