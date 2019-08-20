/**
 * 
 */
package com.elitecore.coregtp.commons.packet;

/**
 * @author dhaval.jobanputra
 *
 */
public class ParseException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ParseException() {
			this("GTP' Request Parse Error");
		}

		public ParseException(String message) {
			super(message);
		}

		public ParseException(String message, Throwable cause) {
			super(message, cause);
		}
}
