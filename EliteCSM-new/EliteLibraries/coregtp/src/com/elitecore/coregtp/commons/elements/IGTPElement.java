/**
 * 
 */
package com.elitecore.coregtp.commons.elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author dhaval.jobanputra
 *
 */
public interface IGTPElement {

		public int getElementType();
		
		public byte[] getValueByte ();
		
		public int readLength(InputStream in) throws IOException;
		public int getLength();
		
}
