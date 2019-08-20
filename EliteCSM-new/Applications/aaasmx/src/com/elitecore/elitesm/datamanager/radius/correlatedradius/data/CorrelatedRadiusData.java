package com.elitecore.elitesm.datamanager.radius.correlatedradius.data;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "correlated-radius")
@ValidObject
@XmlType(propOrder = { "name", "description", "authEsiName", "acctEsiName" })
public class CorrelatedRadiusData implements Differentiable, Validator{

    private String id;
    @NotEmpty(message = "Correlated Radius Name must be specify")
    @Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
    private String name;
    private String description;
    private String authEsiId;
    @NotEmpty(message = "Correlated Radius Auth type ESI name must be specifed")
    private String authEsiName;
    private String acctEsiId;
    @NotEmpty(message = "Correlated Radius Acct type ESI name must be specifed")
    private String acctEsiName;
    private String auditUId;

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public String getAuthEsiId() {
        return authEsiId;
    }

    public void setAuthEsiId(String authEsiId) {
        this.authEsiId = authEsiId;
    }

    @XmlElement(name = "auth-esi-name")
    public String getAuthEsiName() {
        return authEsiName;
    }

    public void setAuthEsiName(String authEsiName) {
        this.authEsiName = authEsiName;
    }

    @XmlTransient
    public String getAcctEsiId() {
        return acctEsiId;
    }

    public void setAcctEsiId(String acctEsiId) {
        this.acctEsiId = acctEsiId;
    }

    @XmlElement(name = "acct-esi-name")
    public String getAcctEsiName() {
        return acctEsiName;
    }

    public void setAcctEsiName(String acctEsiName) {
        this.acctEsiName = acctEsiName;
    }

    @XmlTransient
    public String getAuditUId() {
        return auditUId;
    }

    public void setAuditUId(String auditUId) {
        this.auditUId = auditUId;
    }

    @Override
    public JSONObject toJson() {

        JSONObject object = new JSONObject();
        object.put("Name", name);
        object.put("Description", description);
        object.put("Auth ESI Name", authEsiName);
        object.put("Acct ESI Name", acctEsiName);
        return object;
    }

    @Override
    public boolean validate(ConstraintValidatorContext context) {
        boolean validateAuthESI;
        boolean validateAcctESI;

        ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
        validateAuthESI = validateAuthESI(context,externalSystemInterfaceBLManager);
        validateAcctESI = validateAcctESI(context,externalSystemInterfaceBLManager);

        return validateAuthESI && validateAcctESI;
    }

    private boolean validateAuthESI(ConstraintValidatorContext context, ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager) {
        boolean isValid = true;
        if(Strings.isNullOrBlank(authEsiName) == false){
            try {
                ExternalSystemInterfaceInstanceData authESIData = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataByName(authEsiName);

                if(ExternalSystemConstants.AUTH_PROXY != authESIData.getEsiTypeId()){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,authEsiName +" is not Auth type ESI");
                }
            }catch (Exception e){
                isValid = false;
                RestUtitlity.setValidationMessage(context,"No Auth ESI data is found with the name "+authEsiName);
            }
        }
        return isValid;
    }

    private boolean validateAcctESI(ConstraintValidatorContext context, ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager) {
        boolean isValid = true;
        if(Strings.isNullOrBlank(acctEsiName) == false){
            try{
                ExternalSystemInterfaceInstanceData acctESIData = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataByName(acctEsiName);

                if(ExternalSystemConstants.ACCT_PROXY != acctESIData.getEsiTypeId()){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,acctEsiName +" is not Acct type ESI");
                }
            }catch (Exception e){
                isValid = false;
                RestUtitlity.setValidationMessage(context,"No Acct ESI data is found with the name "+acctEsiName);
            }
        }
        return isValid;
    }
}
