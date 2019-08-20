package com.elitecore.netvertex.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class contains radius attribute mapping
 * 
 * @author Manjil Purohit
 */
public class AttributeMapping {
	private String radiusAttribute;
	private String policyKey;
	private String type;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setRadiusAttribute(String radiusAttribute) {
		this.radiusAttribute = radiusAttribute;
	}
	
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	
	public String getRadiusAttribute() {
		return radiusAttribute;
	}
	
	public String getPolicyKey() {
		return policyKey;
	}
	
	public String toString(){
		StringWriter stringBuffer=new StringWriter();
		PrintWriter out=new PrintWriter(stringBuffer);
        out.println("          Radius Attribute = " + radiusAttribute);
        out.println("          Policy Key = " + policyKey);
        out.println("          Type = " + type);
		return stringBuffer.toString();
	}

}
