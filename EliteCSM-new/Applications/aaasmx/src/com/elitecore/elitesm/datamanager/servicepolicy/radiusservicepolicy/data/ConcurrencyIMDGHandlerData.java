package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.inmemorydatagrid.InMemoryDataGridBLManager;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

@XmlRootElement(name = "rad-imdg-concurrency-handler")
@XmlType(propOrder = {"ruleSet", "imdgFieldValue"})
@ValidObject
public class ConcurrencyIMDGHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData, Validator {

    private String ruleSet;
    private String imdgFieldValue;

    @XmlElement(name = "ruleset")
    public String getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(String ruleSet) {
        this.ruleSet = ruleSet;
    }

    @NotEmpty(message = "Concurrency Identity Field name must be specified in Concurrency IMDG handler of Authentication Flow")
    @XmlElement(name = "concurrency-identity-field")
    public String getImdgFieldValue() {
        return imdgFieldValue;
    }

    public void setImdgFieldValue(String imdgFieldValue) {
        this.imdgFieldValue = imdgFieldValue;
    }

    @Override
    public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
        return null;
    }

    @Override
    public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
        return null;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Enabled", getEnabled());
        if ( Strings.isNullOrEmpty(ruleSet) == false )
            jsonObject.put("Ruleset", ruleSet);

        jsonObject.put("Imdg Field",imdgFieldValue);
        return jsonObject;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println(repeat("-", 70));
        out.println(format(padStart("Concurrency IMDG Handler | Enabled: %s", 10, ' '), getEnabled()));
        out.println(repeat("-", 70));
        out.println(format("%-30s: %s", "Ruleset", getRuleSet()));
        out.println(format("%-30s: %s", "IMDG Field", getImdgFieldValue()));
        out.println(repeat("-", 70));
        out.close();
        return writer.toString();
    }

    @Override
    public boolean validate(ConstraintValidatorContext context) {
        boolean isValid = true;

        List<String> imdgFieldValues = new ArrayList<>();
        if( Strings.isNullOrBlank(imdgFieldValue) == false ){
            try{
                InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
                InMemoryDataGridData imdgData = inMemoryDataGridBLManager.getInMemoryDatagridConfiguration();
                if(imdgData == null){
                    RestUtitlity.setValidationMessage(context, "You are not allowed to add Concurrency IMDG Handler as Imdg is Disabled or not Configured");
                    return false;
                }else{
                    ImdgConfigData existingIMDGData;
                    String existingxmlDatas = new String(imdgData.getImdgXml());
                    StringReader stringReader =new StringReader(existingxmlDatas.trim());

                    //Convert into relevant POJO
                    JAXBContext jaxbContext = JAXBContext.newInstance(ImdgConfigData.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    existingIMDGData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);

                    List<ImdgIndexDetail> radiusIMDGFieldMapping = existingIMDGData.getImdgRadiusConfig().getRadiusIMDGFieldMapping();
                    for (ImdgIndexDetail imdgDetail : radiusIMDGFieldMapping) {
                        imdgFieldValues.add(imdgDetail.getImdgFieldValue());
                    }

                    if(imdgFieldValues.contains(imdgFieldValue)){
                        return true;
                    } else {
                        RestUtitlity.setValidationMessage(context, "Configured Concurrency Identity Field "+ imdgFieldValue +" is invalid in Concurrency IMDG handler of Authentication Flow");
                        return false;
                    }
                }
            }catch(Exception e){
                RestUtitlity.setValidationMessage(context, "Configured Concurrency Identity Field name is invalid in Concurrency IMDG handler of Authentication Flow");
                isValid = false;
            }
        }
        return isValid;
    }

}
