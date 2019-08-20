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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@ValidObject
public class CommunicatorData implements Differentiable,Validator {

    private Integer loadFactor;

    private String name;

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "load-factor")
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
        return "Name = " + this.name + ", Load-Factor = " + this.loadFactor + "";
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("Load Factor", loadFactor);
        object.put("Name", name);
        return object;
    }

    @Override
    public boolean validate(ConstraintValidatorContext context) {
        boolean isValid = true;

        if(Strings.isNullOrBlank(name) && (loadFactor == null || loadFactor == 0)){
            RestUtitlity.setValidationMessage(context,"Esi name and Load factor must be specified");
            isValid=false;
        }else if(Strings.isNullOrBlank(name) == false && (loadFactor == null || loadFactor == 0)){
            RestUtitlity.setValidationMessage(context,"Load factor for Esi " + name + " must be specified");
            isValid=false;
        }else if((loadFactor != null && loadFactor > 0) && Strings.isNullOrBlank(name)){
            RestUtitlity.setValidationMessage(context,"Esi name for Load factor " + loadFactor + " must be specified");
            isValid=false;
        }
        return isValid;
    }
}
