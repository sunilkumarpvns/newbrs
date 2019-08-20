/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil.base64;


import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * Reads base64 encoded binary data.
 * <p>
 * This class reads base64 text as defined in section 5.2 of RFC 1521.
 * </p>
 */
public final class Base64Input extends InputStream {
    private static final byte[] decode = {
            -3, -3, -3, -3, -3, -3, -3, -3, // 00 ... 07
            -3, -2, -2, -3, -3, -2, -3, -3, // 08 ... 0F
            -2, -3, -3, -3, -3, -3, -3, -3, // 10 ... 17
            -3, -3, -3, -3, -3, -3, -3, -3, // 18 ... 1F
            -3, -3, -3, -3, -3, -3, -3, -3, // 20 ... 27
            -3, -3, -3, 62, -3, -3, -3, 63, // 28 ... 2F
            52, 53, 54, 55, 56, 57, 58, 59, // 30 ... 37
            60, 61, -3, -3, -3, -1, -3, -3, // 38 ... 3F
            -3, 0,  1,  2,  3,  4,  5,  6,  // 40 ... 47
            7,  8,  9,  10, 11, 12, 13, 14, // 48 ... 4F
            15, 16, 17, 18, 19, 20, 21, 22, // 50 ... 57
            23, 24, 25, -3, -3, -3, -3, -3, // 58 ... 5F
            -3, 26, 27, 28, 29, 30, 31, 32, // 60 ... 67
            33, 34, 35, 36, 37, 38, 39, 40, // 68 ... 6F
            41, 42, 43, 44, 45, 46, 47, 48, // 70 ... 77
            49, 50, 51, -3, -3, -3, -3, -3, // 78 ... 7F
        };

    private Reader in;
    private int i;
    private int buffer;
    private int marked_i;
    private int marked_buffer;
    
    /**
     * Constructs a <code>Base64Input</code>.
     * @param   in      The input stream.
     */
    public Base64Input(Reader in) {
        this.in = in;
    }
    
    // InputStream interface.
    
    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * @return     The number of bytes that can be read from this input stream
     *             without blocking.
     */
    public int available() {
        return i;
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     */
    public void close() throws IOException {
        in.close();
    }
    
    /**
     * Marks the current position in this input stream.
     * @param   readlimit   The maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     */
    public void mark(int readlimit) {
        try {
            in.mark(readlimit);
            marked_i = i;
            marked_buffer = buffer;
        } catch(IOException e) {
            throw new UnspecifiedException(e);
        }
    }
    
    /**
     * Tests if this input stream supports the {@link #mark}
     * and {@link #reset} methods.
     * @return  <code>true</code> if this true type supports the mark and reset
     *          method; <code>false</code> otherwise.
     */
    public boolean markSupported() {
        return in.markSupported();
    }
    
    /**
     * Reads the next byte of data from this input stream.
     * @return     The next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     */
    public int read() throws IOException {
        if(i == 0) {
            
        	while(true) {
                int ch = in.read();
                if(ch < 0)
                    return -1;
                if(ch >= decode.length)
                    throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
                int a = decode[ch];
                switch(a) {
	                case -3:
	                    throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
	                case -2:
	                    // Ignore space characters.
	                    continue;
	                case -1:
	                    return -1;
	                default:
	                    buffer = a;
                }
                break;
            }
            while(true) {
                int ch = in.read();
                if(ch < 0)
                    throw new InvalidBase64Encoding("Incomplete encoding");
                if(ch >= decode.length)
                    throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
                int b = decode[ch];
                switch(b) {
	                case -3:
	                    throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
	                case -2:
	                    // Ignore space characters.
	                    continue;
	                case -1:
	                    throw new InvalidBase64Encoding("Invalid sequence");
	                default:
	                    buffer = buffer << 6 | b;
                }
                break;
            }
            while(true) {
            	
                int ch = in.read();
                if(ch < 0)
                    throw new InvalidBase64Encoding("Incomplete encoding");
                if(ch >= decode.length)
                    throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
                int c = decode[ch];
                switch(c) {
                	case -3:
                		throw new InvalidBase64Encoding("Invalid character: " + (char)ch);
                	case -2:
                		// Ignore space characters.
                		continue;
                	case -1:
                		while(true) {
                			int ch2 = in.read();
                			if(ch2 < 0)
                				throw new InvalidBase64Encoding("Incomplete encoding");
                			if(ch2 >= decode.length)
                				throw new InvalidBase64Encoding("Invalid character: " + (char)ch2);
                			int d = decode[ch2];
                			switch(d) {
                				case -3:
                					throw new InvalidBase64Encoding("Invalid character: " + (char)ch2);
                				case -2:
                					// Ignore space characters.
                					continue;
                				case -1:
                					break;
                				default:
                					throw new InvalidBase64Encoding("Invalid sequence");
                			}
                			break;
                		}
                		buffer >>>= 4;
                    	i = 1;
                    	break;
	                default:
	                	buffer = buffer << 6 | c;
                    	while(true) {
                    		int ch2 = in.read();
                    		if(ch2 < 0)
                    			throw new InvalidBase64Encoding("Incomplete encoding");
                    		if(ch2 >= decode.length)
                    			throw new InvalidBase64Encoding("Invalid character: " + (char)ch2);
                    		int d = decode[ch2];
                    		switch(d) {
                    			case -3:
                    				throw new InvalidBase64Encoding("Invalid character: " + (char)ch2);
                    			case -2:
                    				// Ignore space characters.
                    				continue;
                    			case -1:
                    				buffer >>= 2;
                            	i = 2;
                            	break;
                        	default:
                        		buffer = buffer << 6 | d;
                            	i = 3;
                    		}
                    		break;
                    	}	
                }
                break;
            } // end of whil loop
        }
       	return (buffer >>> --i * 8) & 0xFF;
    }
    
    /**
     * Repositions this stream to the position at the time the 
     * {@link #mark} method was last called on this input stream. 
     */
    public void reset() throws IOException {
        i = marked_i;
        buffer = marked_buffer;
        in.reset();
    }
    
    /**
     * Skips over and discards <code>n</code> bytes of data from this 
     * input stream.
     * @param      n   The number of bytes to be skipped.
     * @return     The actual number of bytes skipped.
     */
    public long skip(long n) throws IOException {
        for(long j = n; j != 0; --j){
            if(read() == -1)
                return n - j;
        }
        return n;
    }
}
