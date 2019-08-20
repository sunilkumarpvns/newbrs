package com.elitecore.diameterapi.core.common.peer.exception;

/**
 * Created by harsh on 6/17/15.
 */
public class PeerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PeerNotFoundException(String message) {
        super(message);
    }
}
