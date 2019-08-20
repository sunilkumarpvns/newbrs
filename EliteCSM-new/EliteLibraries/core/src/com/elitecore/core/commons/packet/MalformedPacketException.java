/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 7th August 2007
 *  Author : Ezhava Baiju D  
 */

package com.elitecore.core.commons.packet;

public class MalformedPacketException extends Exception {

	private static final long serialVersionUID = 1L;

	public MalformedPacketException() {
		super("Malformed Request Packet");
	}

	public MalformedPacketException(String message) {
		super(message);
	}

	public MalformedPacketException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedPacketException(Throwable cause) {
		super(cause);
	}

}
