/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil.base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;



/**
 * 
 * Class for Base64 encoding and decoding.
 * @author Elitecore Technologies Ltd.
 * 
 */
public final class Base64 {
	
	public static final int MAX_COLUMNS     = 76;
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @param columns
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(InputStream in, Writer out, int columns) throws IOException {
		StreamCopying.transfer(in, new Base64Output(new Cluster(out, "\n", columns))).flush();
		return out;
	}
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(InputStream in, Writer out) throws IOException {
		return encode(in, out, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param b
	 * @param first
	 * @param last
	 * @param out
	 * @param columns
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(byte[] b, int first, int last, Writer out, int columns) 
		throws IOException {
		
		Base64Output s = new Base64Output(new Cluster(out, "\n", columns));
		s.write(b, first, last - first);
		s.flush();
		return out;
	}
	
	/**
	 * 
	 * @param b
	 * @param first
	 * @param last
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(byte[] b, int first, int last, Writer out) 
		throws IOException {
		
		return encode(b, first, last, out, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param b
	 * @param out
	 * @param columns
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(byte[] b, Writer out, int columns) 
		throws IOException {
		
		return encode(b, 0, b.length, out, columns);
	}
	
	/**
	 * 
	 * @param b
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Writer encode(byte[] b, Writer out) throws IOException {
		return encode(b, out, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param in
	 * @param columns
	 * @return
	 * @throws IOException
	 */
	public static String encode(InputStream in, int columns) throws IOException {
		return encode(in, new StringWriter(), columns).toString();
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String encode(InputStream in) throws IOException {
		return encode(in, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param b
	 * @param first
	 * @param last
	 * @param columns
	 * @return
	 */
	public static String encode(byte[] b, int first, int last, int columns) {
		try {
			return encode(b, first, last, new StringWriter(), columns).toString();
		} catch(IOException e) {
			throw new UnexpectedException(e);
		}
	}
	
	/**
	 * 
	 * @param b
	 * @param first
	 * @param last
	 * @return
	 */
	public static String encode(byte[] b, int first, int last) {
		return encode(b, first, last, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param b
	 * @param columns
	 * @return
	 */
	public static String encode(byte[] b, int columns) {
		return encode(b, 0, b.length, columns);
	}
	
	/**
	 * 
	 * @param b
	 * @return
	 */
	public static String encode(byte[] b) {
		return encode(b, MAX_COLUMNS);
	}
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static OutputStream decode(Reader in, OutputStream out) 
		throws IOException {
		
		return StreamCopying.transfer(new Base64Input(in), out);
	}
	
	/**
	 * 
	 * @param encoded
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static OutputStream decode(String encoded, OutputStream out) 
		throws IOException	{
		
		return decode(new StringReader(encoded), out);
	}
	
	/**
	 * 
	 * @param encoded
	 * @return
	 * @throws InvalidBase64Encoding
	 */
	public static byte[] decode(String encoded) throws InvalidBase64Encoding {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(encoded.length() * 6 / 8);
			decode(encoded, out);
			return out.toByteArray();
		} catch(IOException e) {
			if(e instanceof InvalidBase64Encoding)
				throw (InvalidBase64Encoding)e.fillInStackTrace();
			throw new UnexpectedException(e);
		}
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] decode(Reader in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		decode(in, out);
		return out.toByteArray();
	}
}
