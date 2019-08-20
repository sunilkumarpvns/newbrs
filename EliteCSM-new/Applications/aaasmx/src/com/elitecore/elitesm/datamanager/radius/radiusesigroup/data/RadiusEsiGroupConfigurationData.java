package com.elitecore.elitesm.datamanager.radius.radiusesigroup.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.radius.correlatedradius.CorrelatedRadiusBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType;
import com.elitecore.elitesm.web.radius.radiusesigroup.RedundancyMode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "radius-esi-group")
@XmlType(propOrder = { "name","description","redundancyMode", "esiType", "stateful", "switchBackEnable", "primaryEsiList", "failOverEsiList", "activePassiveEsiList" })
@ValidObject
public class RadiusEsiGroupConfigurationData implements Differentiable ,Validator {

    @NotEmpty(message = "Radius ESI Group Name must be specified. ")
    @Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
    private String name;
    private String description;

    @NotEmpty(message = "Redundancy Mode field must be specified")
    private String redundancyMode;

    @NotEmpty(message = "Esi Type field must be specified")
    private String esiType;

    @NotEmpty(message = "Sticky Session field must be specified")
    @Pattern(regexp = "^$|true|false", message = "Invalid value of Sticky Session field. Value could be 'true' or 'false'.")
    private String stateful;

    @NotEmpty(message = "Switch Back Enable field must be specified")
    @Pattern(regexp = "^$|true|false", message = "Invalid value of Switch Back Enable field. Value could be 'true' or 'false'.")
    private String switchBackEnable;

    @Valid
    private List<CommunicatorData> primaryEsiList;

    @Valid
    private List<CommunicatorData> failOverEsiList;

    @Valid
    private List<ActivePassiveCommunicatorData> activePassiveEsiList;

    //for search page
    private String id;

    public RadiusEsiGroupConfigurationData(){
        description = RestUtitlity.getDefaultDescription();
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

    @XmlElement(name = "esi-type")
    public String getEsiType() {
        return esiType;
    }

    public void setEsiType(String esiType) {
        this.esiType = esiType;
    }

    @XmlElement(name = "is-sticky-session-enable")
    public String getStateful() {
        return stateful;
    }

    public void setStateful(String stateful) {
        this.stateful = stateful;
    }

    @XmlElement(name = "redundancy-mode")
    public String getRedundancyMode() {
        return redundancyMode;
    }

    public void setRedundancyMode(String redundancyMode) {
        this.redundancyMode = redundancyMode;
    }

    @XmlElement(name = "is-switch-back-enable")
    public String getSwitchBackEnable() {
        return switchBackEnable;
    }

    public void setSwitchBackEnable(String switchBackEnable) {
        this.switchBackEnable = switchBackEnable;
    }

    @XmlElementWrapper(name = "primary-esi-list")
    @XmlElement(name = "esi-entry-detail")
    public List<CommunicatorData> getPrimaryEsiList() {
        return primaryEsiList;
    }

    public void setPrimaryEsiList(List<CommunicatorData> primaryEsiList) {
        this.primaryEsiList = primaryEsiList;
    }

    @XmlElementWrapper(name = "failover-esi-list")
    @XmlElement(name = "esi-entry-detail")
    public List<CommunicatorData> getFailOverEsiList() {
        return failOverEsiList;
    }

    public void setFailOverEsiList(List<CommunicatorData> failOverEsiList) {
        this.failOverEsiList = failOverEsiList;
    }

    @XmlElementWrapper(name = "esi-list")
    @XmlElement(name = "active-passive-esi")
    public List<ActivePassiveCommunicatorData> getActivePassiveEsiList() {
        return activePassiveEsiList;
    }

    public void setActivePassiveEsiList(List<ActivePassiveCommunicatorData> activePassiveEsiList) {
        this.activePassiveEsiList = activePassiveEsiList;
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public JSONObject toJson() {

        JSONObject object = new JSONObject();
        object.put("Name : ", name);
        object.put("Description : ", description);
        object.put("Redundancy Mode : ", redundancyMode);
        object.put("ESI Type : ", esiType);
        object.put("Sticky Session : ", stateful);
        object.put("Switch Back : ", switchBackEnable);

        JSONArray primaryEsiJsonArray = new JSONArray();
        if(Collectionz.isNullOrEmpty(primaryEsiList) == false){
            for (CommunicatorData primaryEsi:primaryEsiList) {
                primaryEsiJsonArray.add(primaryEsi.toJson());
            }
        }
        object.put("Primary Esi Value : ",primaryEsiJsonArray);

        JSONArray secondaryEsiJsonArray = new JSONArray();
        if(Collectionz.isNullOrEmpty(failOverEsiList) == false){
            for (CommunicatorData secondaryEsi:failOverEsiList) {
                secondaryEsiJsonArray.add(secondaryEsi.toJson());
            }
        }
        object.put("Secondary Esi Value : ",secondaryEsiJsonArray);

        JSONArray activePassiveEsiJsonArray = new JSONArray();
        if(Collectionz.isNullOrEmpty(activePassiveEsiList) == false){
            for (ActivePassiveCommunicatorData esi:activePassiveEsiList) {
                activePassiveEsiJsonArray.add(esi.toJson());
            }
        }
        object.put("Active Passive Esi Value : ",activePassiveEsiJsonArray);
        return object;
    }

    @Override
    public boolean validate(ConstraintValidatorContext context) {
        boolean isValid = true;

        if(Strings.isNullOrBlank(redundancyMode) == false && RedundancyMode.getRedundancyModeNames().contains(this.redundancyMode) == false){
            RestUtitlity.setValidationMessage(context,"Redundancy mode type must be 'N+M' or 'ACTIVE-PASSIVE'");
            isValid=false;
        }

        if(Strings.isNullOrBlank(esiType) == false && RadiusEsiType.getEsiTypeNames().contains(esiType) == false){
            RestUtitlity.setValidationMessage(context,"Esi Type must be of 'AUTH','ACCT','CHARGING GATEWAY','CORRELATED RADIUS'");
            isValid=false;
        }

        if(Strings.isNullOrBlank(stateful) == false && Strings.isNullOrBlank(switchBackEnable) == false){
            if(RadiusEsiType.ACCT.name.equals(esiType) && stateful.equalsIgnoreCase("false") && switchBackEnable.equalsIgnoreCase("true")){
                RestUtitlity.setValidationMessage(context,"Switch back can not be enable without enabling Stateful Message");
                isValid=false;
            }else if(!RadiusEsiType.ACCT.name.equals(esiType) && stateful.equalsIgnoreCase("false")){
                RestUtitlity.setValidationMessage(context,"Sticky Session must be enable for Esi type AUTH,CHARGING GATEWAY,CORRELATED RADIUS");
                isValid=false;
            }
        }

        if(RedundancyMode.NM.redundancyModeName.equalsIgnoreCase(this.redundancyMode)){
            if(Collectionz.isNullOrEmpty(primaryEsiList)){
                RestUtitlity.setValidationMessage(context,"Primary Esi must be specified");
                isValid=false;
            }

            if(Collectionz.isNullOrEmpty(activePassiveEsiList) == false){
                RestUtitlity.setValidationMessage(context,"In N+M Redundancy mode ACTIVE-PASSIVE Esi configuration is not allowed");
                isValid=false;
            }
        }else if(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName.equalsIgnoreCase(this.redundancyMode)){
            if(Collectionz.isNullOrEmpty(activePassiveEsiList)){
                RestUtitlity.setValidationMessage(context,"Active-Passive Esi must be specified");
                isValid=false;
            }

            if(Collectionz.isNullOrEmpty(primaryEsiList) == false){
                RestUtitlity.setValidationMessage(context,"In ACTIVE-PASSIVE Redundancy mode N+M Esi configuration is not allowed");
                isValid=false;
            }
        }

        ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager =new ExternalSystemInterfaceBLManager();
        List<String> authTypeEsiDataList = new ArrayList<>();
        List<String> acctTypeEsiDataList = new ArrayList<>();
        List<String> chargingGatewayEsiDataList = new ArrayList<>();
        List<String> correlatedRadiusEsiDataList = new ArrayList<>();
        try{
            List<ExternalSystemInterfaceInstanceData> esiDataList = externalSystemInterfaceBLManager.getAllExternalSystemInstanceDataList();
            List<CorrelatedRadiusData> correlatedRadiusDataList = new CorrelatedRadiusBLManager().getCorrelatedRadiusDataList();
            if(Collectionz.isNullOrEmpty(esiDataList) == false){
                for(ExternalSystemInterfaceInstanceData esi : esiDataList){
                    if(ExternalSystemConstants.AUTH_PROXY == esi.getEsiTypeId()){
                        authTypeEsiDataList.add(esi.getName());
                    }else if(ExternalSystemConstants.ACCT_PROXY == esi.getEsiTypeId()){
                        acctTypeEsiDataList.add(esi.getName());
                    }else if(ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION == esi.getEsiTypeId()){
                        chargingGatewayEsiDataList.add(esi.getName());
                    }
                }
            }else if(Collectionz.isNullOrEmpty(correlatedRadiusDataList) == false){
                for(CorrelatedRadiusData esi:correlatedRadiusDataList){
                    correlatedRadiusEsiDataList.add(esi.getName());
                }
            }else{
                throw new DataManagerException();
            }
        }catch (Exception e){
            RestUtitlity.setValidationMessage(context,"No Esi data found in Database");
        }

        boolean isValidEsi = true;
        if(RadiusEsiType.AUTH.name.equalsIgnoreCase(esiType)){
            isValidEsi = validateEsiNames(context, authTypeEsiDataList,RadiusEsiType.AUTH.name); }
        else if(RadiusEsiType.ACCT.name.equalsIgnoreCase(esiType)){
            isValidEsi = validateEsiNames(context, acctTypeEsiDataList,RadiusEsiType.ACCT.name);
        }else if(RadiusEsiType.CHARGING_GATEWAY.name.equalsIgnoreCase(esiType)){
            isValidEsi = validateEsiNames(context, chargingGatewayEsiDataList,RadiusEsiType.CHARGING_GATEWAY.name);
        }else if(RadiusEsiType.CORRELATED_RADIUS.name.equalsIgnoreCase(esiType)){
            isValidEsi = validateEsiNames(context, correlatedRadiusEsiDataList,RadiusEsiType.CORRELATED_RADIUS.name);
        }

        if (!isValid || !isValidEsi){
            return false;
        }
        return isValid;
    }

    private boolean validateEsiNames(ConstraintValidatorContext context, List<String> esiNames, String name) {
        boolean isValid = true;
        Set<String> configuredEsiNames = new HashSet<>();
        if(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName.equalsIgnoreCase(redundancyMode)){
            for (ActivePassiveCommunicatorData esi: activePassiveEsiList){
                if(Strings.isNullOrBlank(esi.getActiveEsiName()) == false && esiNames.contains(esi.getActiveEsiName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,getMessage(name,esi.getActiveEsiName()));
                }else if(configuredEsiNames.add(esi.getActiveEsiName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,"Esi with the name "+esi.getActiveEsiName()+ " is already configured");
                }
                if(Strings.isNullOrBlank(esi.getPassiveEsiName()) == false && esiNames.contains(esi.getPassiveEsiName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,getMessage(name,esi.getPassiveEsiName()));
                }else if(configuredEsiNames.add(esi.getPassiveEsiName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,"Esi with the name "+esi.getPassiveEsiName()+ " is already configured");
                }
            }
        }else if(RedundancyMode.NM.redundancyModeName.equalsIgnoreCase(redundancyMode)){
            for (CommunicatorData esi: primaryEsiList){
                if(Strings.isNullOrBlank(esi.getName()) == false && esiNames.contains(esi.getName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,getMessage(name,esi.getName()));
                }else if(configuredEsiNames.add(esi.getName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,"Esi with the name "+esi.getName()+ " is already configured");
                }
            }
            for (CommunicatorData esi: failOverEsiList){
                if(Strings.isNullOrBlank(esi.getName()) == false && esiNames.contains(esi.getName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,getMessage(name,esi.getName()));
                }else if(configuredEsiNames.add(esi.getName()) == false){
                    isValid = false;
                    RestUtitlity.setValidationMessage(context,"Esi with the name "+esi.getName()+ " is already configured");
                }
            }
        }
        return isValid;
    }

    private String getMessage(String esiType, String esiName){
        return "No valid "+ esiType + " Type of Esi found with name "+ esiName;
    }
}
