package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"stripIdentity", "separator", "idetityFormat",
		"trimIdentity", "trimPassword"})
public class UpdateIdentityParamsDetail implements UpdateIdentityParameters,Differentiable {

	private String stripIdentity;
	private String separator;
	private String trimIdentity;
	private String trimPassword;
	private String idetityFormat;
	
	@Override
	@XmlElement(name = "case")
	@Pattern(regexp = "No Change|Lower Case|Upper Case", message = "Invalid value of Select Case. Values are No Change, Lower Case and Upper Case")
	public String getIdetityFormat() {
		return idetityFormat;
	}
	
	public void setIdetityFormat(String idetityFormat) {
		this.idetityFormat = idetityFormat;
	}
	
	@Override
	@XmlElement(name = "strip-identity",type = String.class)
	@Pattern(regexp = "None|Prefix|Suffix", message = "Invalid value of Strip Identity. Values are None, Prefix and Suffix")
	public String getStripIdentity() {
		return stripIdentity;
	}
	public void setStripIdentity(String stripIdentity) {
		this.stripIdentity = stripIdentity;
	}

	@Override
	@XmlElement(name = "separator",type = String.class)
	@Length(min = 0, max = 1, message = "Length of Separator must not more than one character")
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	@XmlElement(name = "trim-identity",type = String.class)
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Trim User Identity. Values could be 'true' and 'false'")
	public String getTrimIdentity() {
		return trimIdentity;
	}
	
	public void setTrimIdentity(String trimIdentity) {
		this.trimIdentity = trimIdentity;
	}
	
	@Override
	@XmlElement(name = "trim-password",type = String.class)	
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Trim Password. Values could be 'true' and 'false'")
	public String getTrimPassword() {
		return trimPassword;
	}
	
	public void setTrimPassword(String trimPassword) {
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
		out.println(format("%-30s: %s", "Case", getIdetityFormat()));
		out.println(format("%-30s: %s", "Trim User Identity", getTrimIdentity()));
		out.println(format("%-30s: %s", "Trim Password", getTrimPassword()));
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
		if ("NONE".equalsIgnoreCase(idetityFormat)){
			object.put("Case", "No Change");
		} else if ("Lower Case".equalsIgnoreCase(idetityFormat)) {
			object.put("Case", "Lower Case");
		} if ("Upper Case".equalsIgnoreCase(idetityFormat)) {
			object.put("Case", "Upper Case");
		}
		object.put("Trim User Identity", trimIdentity);
		object.put("Trim Password", trimPassword);
		
		return object;
	}
}
