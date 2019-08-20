package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class contain combined data 
 * from NetConfigurationParameter and
 * NetConfigurationValues 
 * @author dhavan
 *
 */
public class NetConfParameterValueBean implements Comparable<NetConfParameterValueBean>{

    //fields related to NetConfigurationParameter 
    private String parameterId;
    private int serialNo;  
    private String name;
    private String displayName;
    private String alias;
    private int maxInstances;
    private String multipleInstances;
    private String parentParameterId;
    private String type;
    private String description;
    private int maxLength;
    private boolean editable;
    private String status;
    private String regexp;
    private String startUpMode;
    private String configId;


    /*
     *  fields related to NetConfigurationValues
     */
    private long parameterValueId;
    private long configInstanceId;
    private String instanceId;
    private String value;
    private String poolExists;

    private boolean isNotNull;
    private boolean isNewAdded;
    private boolean isRemoved;
    private String  startDivStatus;
    private List<String> closeDivStatusLst = new ArrayList<String>();
   
    /*
     * currentInstanceNo will be used for maintaining serial no
     */
    private int currentInstanceNo;

    /* totalInstance will be used for keeping track of the  total instance of same type*/
    private int totalInstance;
    private Set netConfigParamValuePool;



    public boolean getEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public Set getNetConfigParamValuePool() {
        return netConfigParamValuePool;
    }
    public void setNetConfigParamValuePool(Set netConfigParamValuePool) {
        this.netConfigParamValuePool = netConfigParamValuePool;
    }
    public int getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getTotalInstance() {
        return totalInstance;
    }
    public void setTotalInstance(int totalInstance) {
        this.totalInstance = totalInstance;
    }
    public int getCurrentInstanceNo() {
        return currentInstanceNo;
    }
    public void setCurrentInstanceNo(int currentInstanceNo) {
        this.currentInstanceNo = currentInstanceNo;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public long getConfigInstanceId() {
        return configInstanceId;
    }
    public void setConfigInstanceId(long configInstanceId) {
        this.configInstanceId = configInstanceId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public int getMaxInstances() {
        return maxInstances;
    }
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }
    public String getMultipleInstances() {
        return multipleInstances;
    }
    public void setMultipleInstances(String multipleInstances) {
        this.multipleInstances = multipleInstances;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParameterId() {
        return parameterId;
    }
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }
    public long getParameterValueId() {
        return parameterValueId;
    }
    public void setParameterValueId(long parameterValueId) {
        this.parameterValueId = parameterValueId;
    }
    public String getParentParameterId() {
        return parentParameterId;
    }
    public void setParentParameterId(String parentParameterId) {
        this.parentParameterId = parentParameterId;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int compareTo(NetConfParameterValueBean arg0) {
        int retValue=0;
        NetConfParameterValueBean netConfArg = (NetConfParameterValueBean )arg0;
        StringTokenizer stThis = new StringTokenizer(instanceId,".");
        StringTokenizer stNetConfArg = new StringTokenizer(netConfArg.instanceId,".");
        int loop = stThis.countTokens()<stNetConfArg.countTokens() ? stThis.countTokens():stNetConfArg.countTokens();

        while(loop-- >0){
            int thisToken= Integer.parseInt(stThis.nextToken());
            int iNetConfArg = Integer.parseInt(stNetConfArg.nextToken());
            if(thisToken < iNetConfArg){
                return retValue-1;
            }
            if(thisToken > iNetConfArg){
                return retValue+1;
            }
            if(thisToken == iNetConfArg){
                continue;
            }

        }
        return stThis.countTokens() == stNetConfArg.countTokens() ?0 : stThis.countTokens() < stNetConfArg.countTokens()? -1 : 1;

    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getStartUpMode() {
        return startUpMode;
    }
    public void setStartUpMode(String startUpMode) {
        this.startUpMode = startUpMode;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRegexp() {
        return regexp;
    }
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getConfigId() {
        return configId;
    }
    public void setConfigId(String configId) {
        this.configId = configId;
    }
    public String getPoolExists() {
        return poolExists;
    }
    public void setPoolExists(String poolExists) {
        this.poolExists = poolExists;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean isNotNull) {
        this.isNotNull = isNotNull;
    }

    public boolean isNewAdded() {
        return isNewAdded;
    }
    public void setNewAdded(boolean isNew) {
        this.isNewAdded = isNew;
    }
    
    public void addCloseDivStatus(String divStatus) {
        this.closeDivStatusLst.add(divStatus);
    }
    
    public boolean isRemoved() {
        return isRemoved;
    }
    public void setRemoved(boolean isChildRemoved) {
        this.isRemoved = isChildRemoved;
    }
    
    public List<String> getCloseDivStatusLst() {
        return closeDivStatusLst;
    }
    public void setCloseDivStatusLst(List<String> divStatusLst) {
        this.closeDivStatusLst = divStatusLst;
    }
    public String getStartDivStatus() {
        return startDivStatus;
    }
    public void setStartDivStatus(String startDivStatus) {
        this.startDivStatus = startDivStatus;
    }
    
    
    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigurationData-----------------");
        writer.println("parameterId      =" +parameterId);  
        writer.println("serialNo         =" +serialNo         );  
        writer.println("name             =" +name             );  
        writer.println("displayName      =" +displayName      );  
        writer.println("alias            =" +alias            );  
        writer.println("maxInstances     =" +maxInstances     );  
        writer.println("multipleInstances=" +multipleInstances);  
        writer.println("parentParameterId=" +parentParameterId);  
        writer.println("type             =" +type             );  
        writer.println("description      =" +description      );  
        writer.println("maxLength        =" +maxLength        );  
        writer.println("editable         =" +editable         );  
        writer.println("status           =" +status           );  
        writer.println("regexp           =" +regexp           );  
        writer.println("startUpMode      =" +startUpMode      );  
        writer.println("configId         =" +configId         );  
        writer.println("parameterValueId =" +parameterValueId );  
        writer.println("configInstanceId =" +configInstanceId );  
        writer.println("instanceId       =" +instanceId       );  
        writer.println("value            =" +value            );  
        writer.println("poolExists       =" +poolExists       );  
        writer.println("isNotNull        =" +isNotNull        );  
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }
   
    

}
