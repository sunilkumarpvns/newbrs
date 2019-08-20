package com.elitecore.aaa.core.conf.impl;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;


@XmlType(propOrder = {})
public class UpdateIdentityParamsDetail implements UpdateIdentityParameters,Differentiable {

	private String stripIdentity;
	private String separator;
	private boolean trimIdentity;
	private boolean trimPassword;
	private int iCase;
	
	
	public UpdateIdentityParamsDetail(){
		//required by Jaxb.
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters#getCaseSensitivity()
	 */
	@Override
	@XmlElement(name = "case",type = int.class)
	public int getCase() {
		return iCase;
	}
	public void setCase(int caseSensitivity) {
		this.iCase = caseSensitivity;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters#getIsStripIdentity()
	 */
	@Override
	@XmlElement(name = "strip-identity",type = String.class)
	public String getStripIdentity() {
		return stripIdentity;
	}
	public void setStripIdentity(String stripIdentity) {
		this.stripIdentity = stripIdentity;
	}
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters#getSeparator()
	 */
	@Override
	@XmlElement(name = "separator",type = String.class)
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters#getIsTrimIdentity()
	 */
	@Override
	@XmlElement(name = "trim-identity",type = boolean.class)
	public boolean getIsTrimIdentity() {
		return trimIdentity;
	}
	public void setIsTrimIdentity(boolean trimIdentity) {
		this.trimIdentity = trimIdentity;
	}
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters#getIsTrimPassword()
	 */
	@Override
	@XmlElement(name = "trim-password",type = boolean.class)	
	public boolean getIsTrimPassword() {
		return trimPassword;
	}
	public void setIsTrimPassword(boolean trimPassword) {
		this.trimPassword = trimPassword;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(padStart("Update Identity", 10, ' '));
		out.println(format("%-30s: %s", "Strip Identity", getStripIdentity()));
		out.println(format("%-30s: %s", "Separator", 
				getSeparator() != null ? getSeparator() : ""));
		out.println(format("%-30s: %s", "Case", getCase()));
		out.println(format("%-30s: %s", "Trim User Identity", getIsTrimIdentity()));
		out.println(format("%-30s: %s", "Trim Password", getIsTrimPassword()));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		if(stripIdentity.equals("0")){
			object.put("Strip Identity", null);
		} else {
			object.put("Strip Identity", stripIdentity);
		}
		object.put("Separator", separator.trim());
		switch(iCase){
			case 1:	object.put("Case", "No Change");
					break;
			
			case 2:	object.put("Case", "Lower Case");
					break;
			
			case 3:	object.put("Case", "Upper Case");
					break;
		}
		object.put("Trim User Identity", trimIdentity);
		object.put("Trim Password", trimPassword);
		
		return object;
	}
}
