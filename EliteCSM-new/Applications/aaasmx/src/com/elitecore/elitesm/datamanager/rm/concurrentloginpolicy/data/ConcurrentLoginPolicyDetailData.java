package com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data;

import java.io.Serializable;
import java.io.StringWriter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

import net.sf.json.JSONObject;

@XmlRootElement(name = "attribute-detail")
@XmlType(propOrder = { "attributeValue", "loginLimit", "login" })
public class ConcurrentLoginPolicyDetailData extends BaseData implements IConcurrentLoginPolicyDetailData, Serializable, Differentiable {
	
	private static final long serialVersionUID = 1L;

	private String concurrentLoginId;
	private int serialNumber;
	private Integer orderNumber ;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@NotEmpty(message = "Attribute of Attribute Detail must be specified.")
	private String attributeValue;
	
	private Integer login;
	
	@NotNull(message = "Login Limit of Attribute Detail must be specified.")
	@Pattern(regexp = "(?i)(Limited|Unlimited)", message = "Invalid value of Login Limit of Attribute Detail. Value could be 'Limited' or 'Unlimited'.")
	private String loginLimit;

	@XmlTransient
	public String getConcurrentLoginId() {
		return concurrentLoginId;
	}

	public void setConcurrentLoginId(String concurrentLoginId) {
		this.concurrentLoginId = concurrentLoginId;
	}

	@XmlElement(name = "max-concurrent-login")
	public Integer getLogin() {
		return login;
	}

	public void setLogin(Integer login) {
		this.login = login;
	}

	@XmlElement(name = "login-limit")
	public String getLoginLimit() {
		return loginLimit;
	}
	
	public void setLoginLimit(String loginLimit) {
		this.loginLimit = loginLimit;
	}
	
	@XmlElement(name = "attribute-value")
	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@XmlTransient
	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Concurrent LoginId :" + concurrentLoginId);
		writer.println("Serial Number :" + serialNumber);
		writer.println("Attribute Value :" + attributeValue);
		writer.println("Login :" + login);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("attributeValue", attributeValue);
		object.put("Max. Concurrent Login", login);
		return object;
	}

}
