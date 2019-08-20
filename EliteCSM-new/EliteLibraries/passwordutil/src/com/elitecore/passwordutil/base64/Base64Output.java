/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil.base64;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Writes binary data in base64.
 * <p>
 * This class writes base64 text as defined in section 5.2 of RFC 1521.
 * </p>
 */
public final class Base64Output extends OutputStream {
    private static final char[] encode = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', 
            '4', '5', '6', '7', '8', '9', '+', '/'
        };
    
    private static final char PAD   = '=';
    
    private Writer out;
    private int i;
    private int buffer;
    
    /**
     * Constructs a <code>Base64Output</code>.
     * <p>
     * The given character output stream must break the output
     * into lines of no more than 76 characters.
     * </p>
     * @param   out     The character output stream.
     */
    public Base64Output(Writer out) {
        this.out = out;
    }
    
    private void terminate() throws IOException {
        switch(i) {
	        case 1:
	            buffer <<= 4;
	            out.write(encode[(buffer >>> 6) & 0x3F]);
	            out.write(encode[(buffer >>> 0) & 0x3F]);
	            out.write(PAD);
	            out.write(PAD);
	            break;
	        case 2:
	            buffer <<= 2;
	            out.write(encode[(buffer >>> 12) & 0x3F]);
	            out.write(encode[(buffer >>> 6) & 0x3F]);
	            out.write(encode[(buffer >>> 0) & 0x3F]);
	            out.write(PAD);
	            break;
	        default:
        }
    }
    
    // OutputStream interface.
    
    /**
     * Writes a <code>byte</code>.
     * @param   b       The byte to write.
     */
    public void write(int b) throws IOException {
        buffer = (buffer << 8) | (b & 0x000000FF);
        if(++i == 3) {
            out.write(encode[(buffer >>> 18) & 0x3F]);
            out.write(encode[(buffer >>> 12) & 0x3F]);
            out.write(encode[(buffer >>> 6) & 0x3F]);
            out.write(encode[(buffer >>> 0) & 0x3F]);
            i = 0;
        }
    }
    
    /**
     * Flushes the contained stream.
     * This will terminate the base64 encoding.
     */
    public void flush() throws IOException {
        terminate();
        out.flush();
    }
    
    /**
     * Flushes all data and closes the output stream.
     */
    public void close() throws IOException {
        terminate();
        out.close();
    }
}
