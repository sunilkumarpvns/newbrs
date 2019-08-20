package com.elitecore.elitesm.web.servermgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigParamValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigParamValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.NetConfParameterValueBean;

public abstract class BaseUpdateConfigurationAction extends BaseWebAction {



    private static final String ATTRIBUTE_ID = "attribute-id";

    /**
     * Add new node  
     * 
     * @author dhavan
     * 
     * @param netConfParamData
     * @param strNodeParameterId
     * @param lstParameters
     * @param configInstanceId
     * @param parentInstanceId
     * @return List
     */
    protected List<NetConfParameterValueBean> getRecursiveNewNetConfParameterData(INetConfigurationParameterData netConfParamData, String strNodeParameterId, List<NetConfParameterValueBean> lstParameters, String configInstanceId, String parentInstanceId,String strChildTotalInstanceVal,String baseParentInstanceId) {

        for (INetConfigurationParameterData netConfigParamChildData : netConfParamData.getNetConfigChildParameters()) {

            String strNewIntanceId = createNextInstance(lstParameters,parentInstanceId,netConfigParamChildData.getParameterId(),netConfigParamChildData.getSerialNo());

            INetConfigurationValuesData netConfigParamValuesData = new NetConfigurationValuesData(); 
            netConfigParamValuesData.setConfigInstanceId(configInstanceId);
            netConfigParamValuesData.setInstanceId(strNewIntanceId);
            netConfigParamValuesData.setParameterId(netConfigParamChildData.getParameterId());
            netConfigParamValuesData.setValue(netConfigParamChildData.getDefaultValue());
            netConfigParamValuesData.setConfigId(netConfigParamChildData.getConfigId());

            NetConfParameterValueBean  netConfParamValueBean = makeBean(netConfigParamChildData,netConfigParamValuesData);
            /* Used At time of Add Node*/
            if(baseParentInstanceId.equalsIgnoreCase(parentInstanceId))
                netConfParamValueBean.setTotalInstance(Integer.parseInt(strChildTotalInstanceVal));

            /* This is newly Added Parameter using Add Funcation :- Mark this parameter As new Parameter*/
            netConfParamValueBean.setNewAdded(Boolean.TRUE);
            lstParameters.add(netConfParamValueBean);
            if(netConfigParamChildData.getNetConfigChildParameters() != null  && netConfigParamChildData.getNetConfigChildParameters().size()>0){
                getRecursiveNewNetConfParameterData(netConfigParamChildData ,netConfigParamChildData.getParameterId(),lstParameters,configInstanceId,netConfigParamValuesData.getInstanceId(),strChildTotalInstanceVal,baseParentInstanceId);
            }
        }
        return lstParameters;
    }


    public static boolean isImmediateChild(String parentIntanceId,String childInstanceId){
        if(childInstanceId.startsWith(parentIntanceId)){
            StringTokenizer stringTokennizer = new StringTokenizer(childInstanceId.substring(parentIntanceId.length(),childInstanceId.length()),".");
            if(stringTokennizer.countTokens() ==1)
                return true;
        }
        return false;
    }

    /**
     * Convert NetConfigurationParameterData,NetConfigurationValuesData to NetConfParameterValueBean
     * 
     * @author dhavan
     * 
     * @param netConfParamData
     * @param netConfigParamValues
     * @return NetConfParameterValueBean
     */
    protected NetConfParameterValueBean makeBean(INetConfigurationParameterData netConfParamData, INetConfigurationValuesData netConfigParamValues) {

        NetConfParameterValueBean netConfParamValueBean = new NetConfParameterValueBean();
        netConfParamValueBean.setParameterId(netConfParamData.getParameterId());
        netConfParamValueBean.setSerialNo(netConfParamData.getSerialNo());
        netConfParamValueBean.setName(netConfParamData.getName());
        netConfParamValueBean.setDisplayName(netConfParamData.getName());
        netConfParamValueBean.setAlias(netConfParamData.getAlias());
        netConfParamValueBean.setMaxInstances(netConfParamData.getMaxInstances());
        netConfParamValueBean.setMultipleInstances(netConfParamData.getMultipleInstances());
        netConfParamValueBean.setParentParameterId(netConfParamData.getParentParameterId());
        netConfParamValueBean.setType(netConfParamData.getType());
        netConfParamValueBean.setStatus(netConfParamData.getStatus());
        netConfParamValueBean.setRegexp(netConfParamData.getRegExp());
        netConfParamValueBean.setDescription(netConfParamData.getDescription());
        netConfParamValueBean.setMaxLength(netConfParamData.getMaxLength());
        netConfParamValueBean.setNetConfigParamValuePool(netConfParamData.getNetConfigParamValuePool());
        netConfParamValueBean.setStartUpMode(netConfParamData.getStartUpMode());
        netConfParamValueBean.setConfigId(netConfParamData.getConfigId());

        if(netConfParamData.getNetConfigParamValuePool() == null || netConfParamData.getNetConfigParamValuePool().size() <= 0)
            netConfParamValueBean.setPoolExists("N");
        else
            netConfParamValueBean.setPoolExists("Y");


        if(netConfParamData.getIsNotNull().equalsIgnoreCase("Y"))
            netConfParamValueBean.setNotNull(true);
        else
            netConfParamValueBean.setNotNull(false);

        if(netConfParamData.getEditable().equalsIgnoreCase("Y")){
            netConfParamValueBean.setEditable(true);}
        else
            netConfParamValueBean.setEditable(false);


        if(netConfigParamValues==null){
            netConfParamValueBean.setValue(EliteUtility.trim(netConfParamData.getDefaultValue()));
        }else{
            netConfParamValueBean.setParameterValueId(netConfigParamValues.getParameterValueId());
            netConfParamValueBean.setConfigInstanceId(netConfigParamValues.getConfigInstanceId());
            netConfParamValueBean.setInstanceId(netConfigParamValues.getInstanceId());
            netConfParamValueBean.setValue(EliteUtility.trim(netConfigParamValues.getValue()));
        }

        return netConfParamValueBean;
    }

    protected String createNextInstance(List<NetConfParameterValueBean> lstParameters, String strParentInstanceId, String strParameterId, int serialNo) {
        List<NetConfParameterValueBean> lstTemp = new ArrayList<NetConfParameterValueBean>();

        for (NetConfParameterValueBean netConfParam : lstParameters) {
            if(netConfParam.getParameterId().equalsIgnoreCase(strParameterId) && netConfParam.getInstanceId().startsWith(strParentInstanceId)){
                lstTemp.add(netConfParam);
            }
        }

        if(lstTemp.size()>0){
            Collections.<NetConfParameterValueBean>sort(lstTemp);
            NetConfParameterValueBean netConfParamLast= lstTemp.get(lstTemp.size()-1);
            String strInstanceId =netConfParamLast.getInstanceId();
            String prefix = strInstanceId.substring(0,strInstanceId.lastIndexOf("."));
            int nextInstance = Integer.parseInt(strInstanceId.substring(strInstanceId.lastIndexOf(".")+1))+1;
            return prefix+"."+nextInstance ;
        }

        return strParentInstanceId+"."+serialNo;
    }

    /**
     * This method will increase total instance 
     * on add node operation.
     * @param strParentNodeInstanceId
     * @param lstParameters
     */
    protected void incrementTotalInstance(String strNodeParameterId, List<NetConfParameterValueBean> lstParameters, String strParentNodeInstanceId) {
        for (NetConfParameterValueBean netBean : lstParameters) {
            String strNetParentParamId = netBean.getParentParameterId();
            if(netBean.getInstanceId().startsWith(strParentNodeInstanceId) && strNetParentParamId.equalsIgnoreCase(strNodeParameterId) ){
                if(netBean.getTotalInstance() > 1)
                    netBean.setTotalInstance(netBean.getTotalInstance()+1); 
                else
                    netBean.setTotalInstance(2);
            }
        }
    }

    /**
     *@author dhavan
     *
     * This method will update the total instance 
     * on delete operation.
     * 
     * @param bean
     * @param lstParameters
     */
    protected void decrementTotalInstance(String parameterId, List<NetConfParameterValueBean> lstParameters, String strNodeInstanceId) {
        strNodeInstanceId = strNodeInstanceId.substring(0, strNodeInstanceId.lastIndexOf("."));

        NetConfParameterValueBean lastProcessedNetBean = null;
        for (NetConfParameterValueBean netBean : lstParameters) {
            if(netBean.getInstanceId().startsWith(strNodeInstanceId) && netBean.getParameterId().equalsIgnoreCase(parameterId)){
                if(netBean.getTotalInstance() > 1)
                    netBean.setTotalInstance(netBean.getTotalInstance()-1);
                else 
                    netBean.setTotalInstance(1);

                netBean.setRemoved(Boolean.TRUE);
                if(lastProcessedNetBean != null)
                    lastProcessedNetBean.setRemoved(Boolean.FALSE);
                lastProcessedNetBean = netBean;

            }
        }
    }

    /**
     * This method converts NetConfParameterValueBean to 
     * NetConfigurationValuesData
     * .
     * 
     * @author dhavan
     * 
     * @param netConfParamValueBean
     * @return
     */
    protected NetConfigurationValuesData makeValueDataFromBean(NetConfParameterValueBean netConfParamValueBean) {
        NetConfigurationValuesData netConfValuesData = new NetConfigurationValuesData();
        netConfValuesData.setParameterId(netConfParamValueBean.getParameterId());
        netConfValuesData.setConfigInstanceId(netConfParamValueBean.getConfigInstanceId());
        netConfValuesData.setInstanceId(netConfParamValueBean.getInstanceId());
        netConfValuesData.setValue("null".equals(netConfParamValueBean.getValue()) ? null :netConfParamValueBean.getValue());
        netConfValuesData.setConfigId(netConfParamValueBean.getConfigId());
        return netConfValuesData;
    }

    /**
     * This method will return the list of net configuration parameter alongwith 
     * list of values. 
     * List will contain element of  NetConfigurationParameterData
     * 
     * @author dhavan
     * 
     * @param netConfParamData
     * @param strConfigInstatnceId
     * @param lstParameters
     * @return List
     */
    public List<NetConfParameterValueBean> getRecursiveNetConfigurationParameterValues(INetConfigurationParameterData netConfParamData, String configInstatnceId, List<NetConfParameterValueBean> lstParameters) {
        Set<INetConfigurationValuesData> stConfigInstanceValues = new HashSet<INetConfigurationValuesData>();

        NetConfParameterValueBean netConfParamValueBean=new NetConfParameterValueBean();

        Map<String,Integer> tempMap = new HashMap<String, Integer>();
        for (INetConfigurationValuesData netConfigParamValues : netConfParamData.getNetConfigParamValues()) {
            if (netConfigParamValues.getConfigInstanceId().equals(configInstatnceId)) {
                String parentParameterIntanceId = getParentInstanceId(netConfigParamValues.getInstanceId());
                if (tempMap.containsKey(parentParameterIntanceId)) {
                    tempMap.put(parentParameterIntanceId, tempMap.get(parentParameterIntanceId) + 1);
                } else {
                    tempMap.put(parentParameterIntanceId, 1);
                }
            }
        }

        Iterator itrConfigParamValues = netConfParamData.getNetConfigParamValues().iterator();
        if(itrConfigParamValues!=null && itrConfigParamValues.hasNext()){
            int iCount=1;
            while(itrConfigParamValues.hasNext()){

                INetConfigurationValuesData netConfigParamValues = (INetConfigurationValuesData) itrConfigParamValues.next();
                if(netConfigParamValues.getConfigInstanceId().equals(configInstatnceId)){
                    stConfigInstanceValues.add(netConfigParamValues);

                    netConfParamValueBean = makeBean(netConfParamData,netConfigParamValues);

                    netConfParamValueBean.setCurrentInstanceNo(iCount++);
                    netConfParamValueBean.setTotalInstance(tempMap.get(getParentInstanceId(netConfParamValueBean.getInstanceId())));
                    lstParameters.add(netConfParamValueBean);

                    Iterator itrConfigChildParams = netConfParamData.getNetConfigChildParameters().iterator();
                    while(itrConfigChildParams.hasNext()){

                        boolean existFlag = false;
                        NetConfigurationParameterData netChildConfParamData = (NetConfigurationParameterData)itrConfigChildParams.next();

                        if(netChildConfParamData.getNetConfigParamValues() == null || netChildConfParamData.getNetConfigParamValues().size() <= 0 ){
                            existFlag = false;
                            Logger.logDebug(MODULE,netConfParamValueBean.getAlias() +"->" + netChildConfParamData.getAlias()+ " not Exist");
                        }else if(netChildConfParamData.getNetConfigParamValues().size() < netConfParamValueBean.getTotalInstance())
                        {
                            existFlag = false;
                            Logger.logDebug(MODULE,"Parent Instance " +netConfParamValueBean.getTotalInstance() + " Current Instance " + netChildConfParamData.getNetConfigParamValues().size());
                        }
                        else{
                            Iterator tempItr = netChildConfParamData.getNetConfigParamValues().iterator();
                            while(tempItr.hasNext()){
                                INetConfigurationValuesData valuesData = (INetConfigurationValuesData)tempItr.next(); 
                                if(valuesData.getConfigInstanceId().equals(configInstatnceId)){
                                    existFlag = true;
                                    break;
                                }
                            }
                        }
                        if(existFlag == false){
                            NetConfParameterValueBean netChildConfParamValueBean = makeBean(netChildConfParamData,null);
                            netChildConfParamValueBean.setCurrentInstanceNo(1);
                            netChildConfParamValueBean.setTotalInstance(tempMap.get(getParentInstanceId(netConfParamValueBean.getInstanceId())));
                            netChildConfParamValueBean.setConfigInstanceId(configInstatnceId);
                            netChildConfParamValueBean.setInstanceId(netConfigParamValues.getInstanceId()+"."+netChildConfParamData.getSerialNo());
                            netChildConfParamData.getNetConfigParamValues().add(makeValueDataFromBean(netChildConfParamValueBean));							
                        }

                    }

                }
            }
            netConfParamData.setNetConfigParamValues(stConfigInstanceValues);
        }

        if(netConfParamData.getNetConfigChildParameters() != null ){
            Iterator itrConfigChildParams = netConfParamData.getNetConfigChildParameters().iterator();
            while(itrConfigChildParams.hasNext()){
                NetConfigurationParameterData netChildConfParamData = (NetConfigurationParameterData)itrConfigChildParams.next();
                lstParameters = getRecursiveNetConfigurationParameterValues(netChildConfParamData,configInstatnceId,lstParameters);
            }

        }
        return lstParameters;
    }


    protected List<NetConfParameterValueBean> getRecursiveNetConfigurationRootParameterValues(INetConfigurationParameterData netConfParamData, String configInstanceId, List<NetConfParameterValueBean> lstParameters, String parentInstance) {
        int iTotalInstance =1;
        String currentInstance = parentInstance + "."+ netConfParamData.getSerialNo();
        NetConfParameterValueBean  netConfParamValueBean = makeBean(netConfParamData,null);
        netConfParamValueBean.setCurrentInstanceNo(iTotalInstance);
        netConfParamValueBean.setTotalInstance(iTotalInstance);
        netConfParamValueBean.setConfigInstanceId(configInstanceId);
        netConfParamValueBean.setInstanceId(currentInstance);
        netConfParamValueBean.setConfigId(netConfParamData.getConfigId());
        lstParameters.add(netConfParamValueBean);
        netConfParamData.setNetConfigParamValues(new HashSet<INetConfigurationValuesData>());
        if(netConfParamData.getNetConfigChildParameters() != null ){
            for (INetConfigurationParameterData netChildConfParamData : netConfParamData.getNetConfigChildParameters()) {
                lstParameters = getRecursiveNetConfigurationRootParameterValues(netChildConfParamData,configInstanceId,lstParameters,currentInstance);
            }
        }
        return lstParameters;
    }

    protected void incrementTotalInstance(String strNodeInstanceId, List lstParameters) {
        String strNetParentParamId =null;
        Iterator itrParameters = lstParameters.iterator();
        int intTotalInstance = 0;
        while(itrParameters.hasNext()){
            NetConfParameterValueBean netBean = (NetConfParameterValueBean)itrParameters.next();
            strNetParentParamId = netBean.getParentParameterId();
            if(strNetParentParamId.equalsIgnoreCase(strNodeInstanceId)){
                if(netBean.getTotalInstance() > intTotalInstance){
                    intTotalInstance = netBean.getTotalInstance(); 
                }
                netBean.setTotalInstance(intTotalInstance+1);
            }
        }
    }


    protected Set<INetConfigParamValuePoolData> getDictionaryAttributeSet(){
        RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
        List<IDictionaryParameterDetailData> listByAttributeId = null;
        try{listByAttributeId = dictionaryBLManager.getDictionaryParameterDetailAllList();  
        }
        catch(DataManagerException dme){
            Logger.logTrace(MODULE,dme);
            Logger.logWarn(MODULE,"unable to Get dictionary Attribute values");
            listByAttributeId = new ArrayList<IDictionaryParameterDetailData>();
        }
        Set<INetConfigParamValuePoolData> dictionaryAttributeList = new HashSet<INetConfigParamValuePoolData>();
        for (IDictionaryParameterDetailData dictionaryParameterDetailData : listByAttributeId) {
            INetConfigParamValuePoolData poolData = new NetConfigParamValuePoolData();
            poolData.setName(dictionaryParameterDetailData.getName());
            poolData.setValue(dictionaryParameterDetailData.getVendorId()+":"+dictionaryParameterDetailData.getVendorParameterId());
            dictionaryAttributeList.add(poolData);
        }
        return dictionaryAttributeList;
    }

    protected List<NetConfParameterValueBean> updateValuePoolListForAttributeId(List<NetConfParameterValueBean> lstNetConfParamValueBean){
        for (NetConfParameterValueBean netValueBean : lstNetConfParamValueBean) {
            if(netValueBean.getAlias().equalsIgnoreCase(ATTRIBUTE_ID))
                netValueBean.setNetConfigParamValuePool(getDictionaryAttributeSet());
        }
        return lstNetConfParamValueBean;
    }

    protected List<NetConfParameterValueBean> updateValuePoolList(List<NetConfParameterValueBean> lstNetConfParamValueBean)throws DataManagerException{
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        Map<String,Set<INetConfigParamValuePoolData>> valuesMap = new HashMap<String, Set<INetConfigParamValuePoolData>>();
        for (NetConfParameterValueBean netValueBean : lstNetConfParamValueBean) {
            if(netValueBean.getAlias().equalsIgnoreCase(ATTRIBUTE_ID)){
                netValueBean.setNetConfigParamValuePool(getDictionaryAttributeSet());
            }
            else{
                if(!valuesMap.containsKey(netValueBean.getParameterId())  && netValueBean.getPoolExists().equalsIgnoreCase("Y")){
                    INetConfigurationParameterData configParamData = netServerBLManager.getNetConfigurationParameterData(netValueBean.getParameterId(),netValueBean.getConfigId());
                    valuesMap.put(netValueBean.getParameterId(), configParamData.getNetConfigParamValuePool());
                }
                netValueBean.setNetConfigParamValuePool(valuesMap.get(netValueBean.getParameterId()));
            }
        }
        return lstNetConfParamValueBean;
    }

    protected static String getParentInstanceId (String childInstanceId){
        return childInstanceId.substring(0, childInstanceId.lastIndexOf(".")==-1?0:childInstanceId.lastIndexOf("."));
    }


    protected List<NetConfParameterValueBean> updateDivStatus(List<NetConfParameterValueBean> lstParameter){

        Stack<String> divStatusStack = new Stack<String>();
        divStatusStack.push("");
        for(int i=0;i<lstParameter.size();i++){
            NetConfParameterValueBean netConfParameterValueBean = lstParameter.get(i);
            if(netConfParameterValueBean.getInstanceId().startsWith(divStatusStack.peek())){
                if(netConfParameterValueBean.getType().equalsIgnoreCase("P")){

                    if(lstParameter.get(i)!=null){
                        lstParameter.get(i).setStartDivStatus(netConfParameterValueBean.getInstanceId());
                    }
                    divStatusStack.push(netConfParameterValueBean.getInstanceId());
                }
            }else{
                netConfParameterValueBean.addCloseDivStatus(divStatusStack.pop());
                if(i != 0)
                    i--;
            }
        }

        NetConfParameterValueBean lstNetConfParameterValueBean =  lstParameter.get(lstParameter.size()-1);
        while(!divStatusStack.empty())
            lstNetConfParameterValueBean.addCloseDivStatus(divStatusStack.pop());

        return lstParameter;
    }
    
    

}