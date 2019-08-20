/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPServerCertificateAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

/**
 * @author pratikchauhan
 *
 */
@XmlRootElement(name="vendor-specific-certificate-data")
@XmlType(propOrder ={"oui","serverCertificateIdForVSC"})
@ValidObject
public class VendorSpecificCertificateData extends BaseData implements IVendorSpecificCertificateData,Serializable,Differentiable,Validator{

	private String id;
	private String eaptlsId;
	private String oui;
	private String serverCertificateIdForVSC;
	private Integer orderNumber;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	@XmlTransient
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlTransient
	public String getEaptlsId() {
		return eaptlsId;
	}
	public void setEaptlsId(String eaptlsId) {
		this.eaptlsId = eaptlsId;
	}
	
	@XmlElement(name="vendor-identifier")
	@NotEmpty(message="Vendor Identifier must be specified for Vendor Specific Certificate")
	public String getOui() {
		return oui;
	}
	public void setOui(String oui) {
		this.oui = oui;
	}
	
	@XmlElement(name="server-certificate-profile")
	@XmlJavaTypeAdapter(value=EAPServerCertificateAdapter.class)
	public String getServerCertificateIdForVSC() {
		return serverCertificateIdForVSC;
	}
	public void setServerCertificateIdForVSC(String serverCertificateIdForVSC) {
		this.serverCertificateIdForVSC = serverCertificateIdForVSC;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("OUI", oui);
		object.put("Server Certificate Profile", ( serverCertificateIdForVSC == null) ? "NONE" : EliteSMReferencialDAO.fetchServerCertificateDetails(serverCertificateIdForVSC));
		return object;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(RestValidationMessages.INVALID.equals(this.serverCertificateIdForVSC)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Invalid Server Certificate Profile name in Vendor Specific Certificate");
		}
		return isValid;
	}
	
}
