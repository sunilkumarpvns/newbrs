package com.elitecore.elitesm.datamanager.radius.radiusesigroup.data;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.adapter.IntegerValidatorAdaptor;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = {"activeEsiName","passiveEsiName","loadFactor"})
@ValidObject
public class ActivePassiveCommunicatorData implements Differentiable,Validator {

    private String activeEsiName;
    private String passiveEsiName;
    private Integer loadFactor;

    @XmlElement(name = "active-esi-name")
    public String getActiveEsiName() {
        return activeEsiName;
    }

    public void setActiveEsiName(String activeEsiName) {
        this.activeEsiName = activeEsiName;
    }

    @XmlElement(name = "passive-esi-name")
    public String getPassiveEsiName() {
        return passiveEsiName;
    }

    public void setPassiveEsiName(String passiveEsiName) {
        this.passiveEsiName = passiveEsiName;
    }

    @XmlElement(name = "load-factor")
    @Min(value=1,message="Load Factor value must be numeric and greater then 0")
    @Max(value = 10, message = "Load Factor value must less then or equal to 10")
    @XmlJavaTypeAdapter(value = IntegerValidatorAdaptor.class)
    public Integer getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(Integer loadFactor) {
        this.loadFactor = loadFactor;
    }

    @Override
    public String toString() {
        return "Active ESI Name = " + this.activeEsiName + ", Passive ESI Name = " + this.passiveEsiName + ", Load-Factor = " + this.loadFactor + "";
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("Active ESI Name", activeEsiName);
        object.put("Passive ESI Name", passiveEsiName);
        object.put("Load Factor", loadFactor);
        return object;
    }

    @Override
    public boolean validate(ConstraintValidatorContext context) {
        boolean isValid = true;

        if(Strings.isNullOrBlank(activeEsiName) && Strings.isNullOrBlank(passiveEsiName) && (loadFactor == null || loadFactor == 0)){
            RestUtitlity.setValidationMessage(context,"Active Esi name,Passive Esi name and Load factor must be specified");
            isValid = false;
        }else if(Strings.isNullOrBlank(activeEsiName) == false && Strings.isNullOrBlank(passiveEsiName) && (loadFactor == null || loadFactor == 0)){
            RestUtitlity.setValidationMessage(context,"Passive Esi name and Load factor of "+activeEsiName+ " esi must be specified");
            isValid = false;
        }else if(Strings.isNullOrBlank(passiveEsiName) == false && (loadFactor == null || loadFactor == 0) && Strings.isNullOrBlank(activeEsiName)){
            RestUtitlity.setValidationMessage(context,"Active Esi name and Load factor of "+passiveEsiName+ " esi must be specified");
            isValid = false;
        }else if(loadFactor != null && Strings.isNullOrBlank(passiveEsiName) && Strings.isNullOrBlank(activeEsiName)){
            RestUtitlity.setValidationMessage(context,"Active and Passive Esi name of "+ loadFactor + " load factor must be specified");
            isValid = false;
        }else if(Strings.isNullOrBlank(activeEsiName) == false && Strings.isNullOrBlank(passiveEsiName)){
            RestUtitlity.setValidationMessage(context,"Passive Esi name of "+ activeEsiName + " esi must be specified");
            isValid = false;
        }else if(Strings.isNullOrBlank(passiveEsiName) == false && (loadFactor == null || loadFactor == 0)){
            RestUtitlity.setValidationMessage(context,"Load factor of "+ passiveEsiName + " esi must be specified");
            isValid = false;
        }else if((loadFactor != null && loadFactor > 0)  && Strings.isNullOrBlank(activeEsiName)){
            RestUtitlity.setValidationMessage(context,"Active Esi of "+ loadFactor + " load factor must be specified");
            isValid = false;
        }
        return isValid;
    }
}
