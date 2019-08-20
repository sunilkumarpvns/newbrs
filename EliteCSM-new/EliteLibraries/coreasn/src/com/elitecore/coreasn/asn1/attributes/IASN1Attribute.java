package com.elitecore.coreasn.asn1.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IASN1Attribute {

	public int readFrom(InputStream in) throws IOException;
	public void writeTo(OutputStream o) throws IOException;

	public int getIntValue();
	public String getStringValue();
	
	public int getCalculatedLength();
	
	public String toString();
}
