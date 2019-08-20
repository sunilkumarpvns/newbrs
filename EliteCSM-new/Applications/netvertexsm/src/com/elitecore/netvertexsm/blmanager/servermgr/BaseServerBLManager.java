/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   BaseServerBLManager.java                            
 * ModualName com.elitecore.netvertexsm.blmanager.servermgr                                      
 * Created on Mar 15, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.blmanager.servermgr;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerVersionData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationParameterData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.netvertexsm.datamanager.servermgr.server.NetServerDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.service.NetServiceDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.logger.Logger;


public abstract class BaseServerBLManager extends BaseBLManager{
    
    private final static String MODULE = "BaseServerBLManager"; 
    
    /**
     * @author kaushik vira
     * @param  configTypeId - identify configuration Type.
     * @param  netServerType - Server Type
     * @param  netServerVersion - Server Version
     * @return Compatible Upgrade Version of ConfigurationData
     */
    protected INetConfigurationData getUpgradeConfigurationVersion(String configTypeId,String netServerType,String netServerVersion,IDataManagerSession session) throws DataManagerException {
         try {
            NetServerDataManager serverDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(serverDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
            INetServerVersionData netServerVersionData = serverDataManager.getCompatibleVersion(netServerType, netServerVersion);
            INetConfigurationData netConfigurationData = serverDataManager.getNetConfigurationData(configTypeId);
            return serverDataManager.getCompatibleUpgradeVersionOfConfigrationData(netConfigurationData.getAlias(),netServerVersionData.getConfigVersion());
        }
        catch(DataManagerException e) {
            throw e;
        }
        catch(Exception e) {
            throw new DataManagerException("getCompatibleUpgradeVersionOfConfigrationData Operation Faild. Reason :"+e.getMessage(),e);
        }
    }    
    
    
    protected void upgradeNetConfigurationValues( INetConfigurationInstanceData netConfigurationInstanceData , String targetConfigurationVersion , IDataManagerSession session ) throws DataManagerException {
        
        EliteAssert.notNull(netConfigurationInstanceData,"configTypeId must be Specified.");
        EliteAssert.notNull(targetConfigurationVersion,"targetConfigurationVersion must be Specified.");
        EliteAssert.notNull(session,"session  Must be specified.");
        
        NetServerDataManager serverDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(serverDataManager,"Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        Logger.logDebug(MODULE, "--Start-- Updating Configration. ConfigInstanceId:-" + netConfigurationInstanceData.getConfigInstanceId());
        
        Logger.logDebug(MODULE, "Step 1 : Updating NetConfigurationValuesData");
        List<INetConfigurationValuesData> lstnewConfigurationValueData = new ArrayList<INetConfigurationValuesData>();
        upgradeConfigurationParametersValues(netConfigurationInstanceData, targetConfigurationVersion, lstnewConfigurationValueData);
        
        Logger.logDebug(MODULE, "Step 1.1 :Updating ConfigID For all Value");
        serverDataManager.saveNetConfValuesData(lstnewConfigurationValueData, netConfigurationInstanceData.getConfigInstanceId());
        
        List<INetConfigurationValuesData> lstConfigParamValue = generateInstanceId(netConfigurationInstanceData.getConfigInstanceId(), targetConfigurationVersion, session);
        Logger.logDebug(MODULE, "Step 1.2 :Updating InstanceId For all Value");
        serverDataManager.saveNetConfValuesData(lstConfigParamValue, netConfigurationInstanceData.getConfigInstanceId());
    }
    
    
    protected List<INetConfigurationValuesData> generateInstanceId( long netConfigurationInstanceId , String netConfigId , IDataManagerSession session ) throws DataManagerException {
        
        EliteAssert.notNull(netConfigId, "netConfigId Must be specified");
        EliteAssert.notNull(session, "session Must be specified");
        
        
        NetServerDataManager serverDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        Logger.logDebug(MODULE,"-- Start -- generating new Intance Id");
        
        INetConfigurationData netConfigurationData = serverDataManager.getRootParameterConfigurationData(netConfigId);
        Iterator itrChildParamSet = netConfigurationData.getNetConfigParameters().iterator();
        List<INetConfigurationValuesData> lstNetConfigParamValueData = new ArrayList<INetConfigurationValuesData>();
        if (itrChildParamSet.hasNext()) {
            INetConfigurationParameterData configParamData = (INetConfigurationParameterData) itrChildParamSet.next();
            generateInstanceIdRev(configParamData, lstNetConfigParamValueData, netConfigurationInstanceId,"","");
        }else
            Logger.logWarn(MODULE,"Unable to find Root Intance");
        
        Logger.logDebug(MODULE,"-- End -- generating new Intance Id");
        
        return lstNetConfigParamValueData;
    }
    
    private void generateInstanceIdRev( INetConfigurationParameterData configParamData , List<INetConfigurationValuesData> lstConfigParameterValue , long configurationInstanceId , String oldInstanceId, String newInstanceId ) {
        int indexCount = 0;
        for ( INetConfigurationValuesData netConfigurationValuesData : configParamData.getNetConfigParamValues() ) {
            
            if(netConfigurationValuesData.getConfigInstanceId()==configurationInstanceId
            &&   netConfigurationValuesData.getInstanceId().startsWith(oldInstanceId)) {
                
                int instanceIndex = indexCount > 0?indexCount+1:configParamData.getSerialNo();
                String instanceId = newInstanceId.equals("")?""+instanceIndex:newInstanceId +"."+instanceIndex;
                INetConfigurationValuesData newNetConfigurationValueData =  netConfigurationValuesData.clone();
                newNetConfigurationValueData.setInstanceId(instanceId);
                lstConfigParameterValue.add(newNetConfigurationValueData);
                
                Logger.logDebug(MODULE,netConfigurationValuesData.getParameterId() +":"+netConfigurationValuesData.getInstanceId() +" -> "+ netConfigurationValuesData.getParameterId()+ ":"+ newNetConfigurationValueData.getInstanceId());
                
                for(INetConfigurationParameterData childNetConfigurationParameterData : configParamData.getNetConfigChildParameters()) {
                    generateInstanceIdRev(childNetConfigurationParameterData, lstConfigParameterValue,configurationInstanceId,netConfigurationValuesData.getInstanceId(),newNetConfigurationValueData.getInstanceId());
                }
                indexCount++;
            }
        }
    }
    
    
    
    /**
     * @author kaushik vira
     * @param configId - configId
     * @param targetedServerVersion - Target Server Version
     * @return Upgrade server instance to targeted server version.
     */
    protected void upgradeConfigurationParametersValues( INetConfigurationInstanceData netConfigurationInstanceData , String upgradeConfigurationVersion , List lstnewConfigurationValueData ) throws DataManagerException {
        
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData Must be specified");
        EliteAssert.notNull(upgradeConfigurationVersion, "upgradeVersion Must be specified");
        Logger.logDebug(MODULE, "--Start-- Updating Configration Parameter configInstanceId:-" + netConfigurationInstanceData.getConfigInstanceId());
        
        INetConfigurationData currentConfigData =  getRootParameterConfigurationData(netConfigurationInstanceData.getConfigId());
        INetConfigurationData targetConfigData =   getRootParameterConfigurationData(upgradeConfigurationVersion);
        
        for ( INetConfigurationParameterData targetConfigurationParameterData : targetConfigData.getNetConfigParameters() ) {
            
            INetConfigurationParameterData currentConfigurationParameterData = searchMatchingConfigurationParameter(targetConfigurationParameterData, currentConfigData.getNetConfigParameters());
            
            if (currentConfigurationParameterData == null)
                Logger.logWarn(MODULE, "Root parameter Not Matching");
            else {
                
                for ( INetConfigurationValuesData netConfigurationValuesData : currentConfigurationParameterData.getNetConfigParamValues() ) {
                    
                    if (netConfigurationValuesData.getConfigInstanceId()==netConfigurationInstanceData.getConfigInstanceId()) {
                        Logger.logDebug(MODULE, "Updateing Instance : [" + netConfigurationValuesData.getParameterId() + ":" + netConfigurationValuesData.getInstanceId() + ":" + netConfigurationValuesData.getConfigId() + "->" + targetConfigurationParameterData.getParameterId() + ":" + upgradeConfigurationVersion + "]");
                        INetConfigurationValuesData tempConfigurationValuesData = netConfigurationValuesData.clone();
                        tempConfigurationValuesData.setConfigId(upgradeConfigurationVersion);
                        lstnewConfigurationValueData.add(tempConfigurationValuesData);
                    }
                }
                upgradeConfigurationParametersValuesRev(netConfigurationInstanceData, targetConfigurationParameterData, currentConfigurationParameterData, upgradeConfigurationVersion, lstnewConfigurationValueData);
            }
        }
        Logger.logDebug(MODULE, " --END--  Updating Configration Parameter configInstanceId:-" + netConfigurationInstanceData.getConfigInstanceId());
    }
    
    /**
     * @author kaushik vira
     * @param configId - configId
     * @param targetedServerVersion - Target Server Version
     * @return Upgrade server instance to targeted server version.
     */
    protected void upgradeConfigurationParametersValuesRev( INetConfigurationInstanceData netConfigurationInstanceData , INetConfigurationParameterData targetConfigurationParameterData , INetConfigurationParameterData currentConfigurationParameterData , String upgradeConfigurationId , List lstnewConfigurationValueData ) throws DataManagerException {
        
        for ( INetConfigurationParameterData child_targetConfigurationParameterData : targetConfigurationParameterData.getNetConfigChildParameters() ) {
            INetConfigurationParameterData child_currentConfigurationParameterData = searchMatchingConfigurationParameter(child_targetConfigurationParameterData, currentConfigurationParameterData.getNetConfigChildParameters());
            
            if (child_currentConfigurationParameterData == null) {
                addConfigurationparameterValueRev(currentConfigurationParameterData, child_targetConfigurationParameterData, lstnewConfigurationValueData,netConfigurationInstanceData);
                continue;
            }
            
            for ( INetConfigurationValuesData netConfigurationValuesData : child_currentConfigurationParameterData.getNetConfigParamValues() ) {
                if (netConfigurationValuesData.getConfigInstanceId()==netConfigurationInstanceData.getConfigInstanceId()) {
                    Logger.logDebug(MODULE, "Updateing Instance : [" + netConfigurationValuesData.getParameterId() + ":" + netConfigurationValuesData.getInstanceId() + ":" + netConfigurationValuesData.getConfigId() + "->" + targetConfigurationParameterData.getParameterId() + ":" + upgradeConfigurationId + "]");
                    INetConfigurationValuesData tempConfigurationValuesData = netConfigurationValuesData.clone();
                    tempConfigurationValuesData.setConfigId(upgradeConfigurationId);
                    lstnewConfigurationValueData.add(tempConfigurationValuesData);
                }
            }
            upgradeConfigurationParametersValuesRev(netConfigurationInstanceData, child_targetConfigurationParameterData, child_currentConfigurationParameterData, upgradeConfigurationId, lstnewConfigurationValueData);
        }
    }
    
    
    
    protected INetConfigurationData getRootParameterConfigurationData( String configId ) throws DataManagerException {
        EliteAssert.notNull(configId, "configId Must be Specified.");
        IDataManagerSession session = null;
        try {
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            NetServerDataManager servermgrDataManager = getNetServerDataManager(session);
            EliteAssert.notNull(servermgrDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getName());
            return servermgrDataManager.getRootParameterConfigurationData(configId);
        }
        catch (DataManagerException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DataManagerException("getRootParameterConfigurationData operartion faild. Reason :"+e.getMessage(), e);
        }
        finally {
            closeSession(session);
        }
    }
    
    
    private INetConfigurationParameterData searchMatchingConfigurationParameter( INetConfigurationParameterData targetConfigurationParameterData , Set<INetConfigurationParameterData> currentConfigurationParameterDataSet ) {
        for ( INetConfigurationParameterData currentConfigurationParameterData : currentConfigurationParameterDataSet ) {
            if (currentConfigurationParameterData.getParameterId().equals(targetConfigurationParameterData.getParameterId())) {
                return currentConfigurationParameterData;
            }
        }
        return null;
    }
    
    /**
     * @author kaushik vira
     * @param configId - configId
     * @param targetedServerVersion - Target Server Version
     * @return Upgrade server instance to targeted server version.
     */
    protected void upgradeNetServerConfiguration( INetConfigurationInstanceData netConfigurationInstanceData , String upgradeConfigurationVersion , IDataManagerSession session ) throws DataManagerException {
        
        EliteAssert.notNull(netConfigurationInstanceData, "netConfigurationInstanceData Must be specified");
        EliteAssert.notNull(upgradeConfigurationVersion, "upgradeConfigurationVersion Must be specified");
        EliteAssert.notNull(session, "session Must be specified");
        NetServerDataManager serverDataManager = getNetServerDataManager(session);
        EliteAssert.notNull(serverDataManager, "Data Manager implementation not found for " + NetServerDataManager.class.getSimpleName());
        
        upgradeNetConfigurationValues(netConfigurationInstanceData,upgradeConfigurationVersion, session);
        
        Logger.logDebug(MODULE, "Step 2: Updating netConfigurationInstanceData");
        
        INetConfigurationInstanceData tempNetConfigurationInstanceData = new NetConfigurationInstanceData();
        tempNetConfigurationInstanceData.setConfigInstanceId(netConfigurationInstanceData.getConfigInstanceId());
        tempNetConfigurationInstanceData.setConfigId(upgradeConfigurationVersion);
        serverDataManager.upadateNetServerConfigurationInstance(tempNetConfigurationInstanceData);
        Logger.logDebug(MODULE, "--END--  Updating Configration. ConfigInstanceId:-" + netConfigurationInstanceData.getConfigInstanceId());
    }
    
    protected void addConfigurationparameterValueRev( INetConfigurationParameterData parentConfigurationParameterData , INetConfigurationParameterData childConfigurationParameterData , List<INetConfigurationValuesData> lstnewConfigurationValueData,INetConfigurationInstanceData netConfigurationInstanceData) {
        int indexCount = 0;
        Set<INetConfigurationValuesData> valuesDataSet = new HashSet<INetConfigurationValuesData>();
        for ( INetConfigurationValuesData parentConfigurationParameterValuesData : parentConfigurationParameterData.getNetConfigParamValues() ) {
            if(parentConfigurationParameterValuesData.getConfigInstanceId()==netConfigurationInstanceData.getConfigInstanceId()) {                
                int instanceIndex = indexCount > 0?indexCount+1:childConfigurationParameterData.getSerialNo();
                String instanceId = parentConfigurationParameterValuesData.getInstanceId() +"."+instanceIndex;
                INetConfigurationValuesData netConfigurationValuesData = new NetConfigurationValuesData();
                /* Setting Values in new Object of netConfigurationValuesData */
                netConfigurationValuesData.setConfigId(childConfigurationParameterData.getConfigId());
                netConfigurationValuesData.setConfigInstanceId(netConfigurationInstanceData.getConfigInstanceId());
                netConfigurationValuesData.setInstanceId(instanceId);
                netConfigurationValuesData.setParameterId(childConfigurationParameterData.getParameterId());
                netConfigurationValuesData.setValue(childConfigurationParameterData.getDefaultValue());
                Logger.logDebug(MODULE, "Adding New Parameter [" + netConfigurationValuesData.getParameterId() + ":" + netConfigurationValuesData.getInstanceId() + ":" + netConfigurationValuesData.getConfigId() + " ]");
                lstnewConfigurationValueData.add(netConfigurationValuesData);
                valuesDataSet.add(netConfigurationValuesData);
            }
        }
        childConfigurationParameterData.setNetConfigParamValues(valuesDataSet);
        for ( INetConfigurationParameterData netConfigurationParameterData : childConfigurationParameterData.getNetConfigChildParameters() ) {
            addConfigurationparameterValueRev(childConfigurationParameterData,netConfigurationParameterData, lstnewConfigurationValueData,netConfigurationInstanceData);
        }
        indexCount++;
    }
    
    
    
    /**
     * @author dhavalraval
     * @author updated By Kaushik Vira
     * @param configParamData
     * @param lstConfigParameterValue
     * @param configInstanceId
     * @param instanceId
     * @purpose Prepare ConfigurationValue Instance for Current node and All the child Nodes.
     * @return List<INetConfigurationValuesData> - INetConfigurationValuesData for CurrentNode and All the Child Nodes.
     */
    protected List<INetConfigurationValuesData> parseInitCreateCurrentNode( INetConfigurationParameterData configParamData , List<INetConfigurationValuesData> lstConfigParameterValue , INetConfigurationInstanceData configurationInstanceData , String instanceId ) {
        INetConfigurationValuesData configValuesData = new NetConfigurationValuesData();
        configValuesData.setConfigInstanceId(configurationInstanceData.getConfigInstanceId());
        configValuesData.setInstanceId(instanceId);
        configValuesData.setParameterId(configParamData.getParameterId());
        configValuesData.setValue(configParamData.getDefaultValue());
        configValuesData.setConfigId(configurationInstanceData.getConfigId());
        lstConfigParameterValue.add(configValuesData);
        int i = 1;
        for ( INetConfigurationParameterData netConfigurationValuesData : configParamData.getNetConfigChildParameters() ) {
            parseInitCreateCurrentNode(netConfigurationValuesData, lstConfigParameterValue, configurationInstanceData, instanceId + "." + i);
            i++;
        }
        return lstConfigParameterValue;
    }
    
    
    protected List<INetConfigurationValuesData> getRecursiveNetConfigurationParameterValues( INetConfigurationParameterData netConfParamData , long configInstanceId , List<INetConfigurationValuesData> lstParameters ) throws DataValidationException {
        EliteAssert.notNull(netConfParamData, "netConfParamData Must Specified");
        EliteAssert.greaterThanZero(configInstanceId, "configInstanceId");
        EliteAssert.notNull(lstParameters, "lstParameters Must Specified");
        
        Set<INetConfigurationValuesData> stConfigInstanceValues = new HashSet<INetConfigurationValuesData>();
        
        Iterator itrConfigParamValues = netConfParamData.getNetConfigParamValues().iterator();
        if (itrConfigParamValues != null && itrConfigParamValues.hasNext()) {
            while (itrConfigParamValues.hasNext()) {
                INetConfigurationValuesData netConfigParamValues = (INetConfigurationValuesData) itrConfigParamValues.next();
                lstParameters.add(netConfigParamValues);
            }
            netConfParamData.setNetConfigParamValues(stConfigInstanceValues);
        }
        
        if (netConfParamData.getNetConfigChildParameters() != null) {
            Iterator itrConfigChildParams = netConfParamData.getNetConfigChildParameters().iterator();
            while (itrConfigChildParams.hasNext()) {
                NetConfigurationParameterData netChildConfParamData = (NetConfigurationParameterData) itrConfigChildParams.next();
                getRecursiveNetConfigurationParameterValues(netChildConfParamData, configInstanceId, lstParameters);
            }
        }
        return lstParameters;
    }
    
    
    
    
    /**
     * @author dhavalraval
     * @param  session
     * @return Returns Data Manager instance for NetServiceDataManager. 
     */
    public NetServiceDataManager getNetServiceDataManager(IDataManagerSession session) {
        return  (NetServiceDataManager) DataManagerFactory.getInstance().getDataManager(NetServiceDataManager.class, session);
        
    }
    
    
    /**
     * @author dhavan
     * @param  session
     * @return Returns Data Manager instance for NetServerDataManager.
     */
    public NetServerDataManager getNetServerDataManager(IDataManagerSession session) {
        return   (NetServerDataManager) DataManagerFactory.getInstance().getDataManager(NetServerDataManager.class, session);
    }
    
    protected byte[] createOutputStream(INetConfigurationData netConfigurationData, long instanceId)throws DataManagerException{
        EliteAssert.notNull(netConfigurationData);
        EliteAssert.notNull(instanceId);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();		
        try{
            Set stRootConfigParamData = netConfigurationData.getNetConfigParameters();
            Iterator itrRootConfigParamData = stRootConfigParamData.iterator();
            if(itrRootConfigParamData.hasNext()){
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();                    
                DocumentBuilder db = dbf.newDocumentBuilder();                    
                Document xmlDoc =  db.newDocument();			
                
                INetConfigurationParameterData netConfigRootData = (INetConfigurationParameterData)itrRootConfigParamData.next();
                Element rootnd = xmlDoc.createElement(netConfigRootData.getAlias());
                xmlDoc.appendChild(rootnd);
                xmlDoc = generateXMLParseNode(netConfigRootData,xmlDoc,rootnd,instanceId,"1");
                format(xmlDoc,outputStream);
                
//                System.setProperty(BaseConstant.TRANSFORMER_FACTORY,BaseConstant.TRANSFORMER_FACTORY_IMPL);
//                TransformerFactory tFactory =TransformerFactory.newInstance();
//                Transformer transformer = tFactory.newTransformer();
//                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//                
//                DOMSource source = new DOMSource(xmlDoc);
//                StreamResult result = new StreamResult(outputStream);
//                transformer.transform(source, result);
            }
        }catch(Throwable e){
            throw new DataManagerException("Update Basic Detail Failed "+e.getMessage(),e);
        }
        return outputStream.toByteArray();
    }
    
    
    protected Document generateXMLParseNode(INetConfigurationParameterData netConfigRootData, Document xmlDoc, Element rootNode, long configInstanceId, String rootInstanceId)throws DataManagerException{
        EliteAssert.notNull(netConfigRootData);
        EliteAssert.notNull(xmlDoc);
        EliteAssert.notNull(rootNode);
        
        EliteAssert.notNull(rootInstanceId);
        try{		
            if(netConfigRootData.getNetConfigParamValues() != null && netConfigRootData.getNetConfigParamValues().size() > 0 && netConfigRootData.getNetConfigChildParameters() != null){
                Iterator itrChildIterator = netConfigRootData.getNetConfigChildParameters().iterator();
                while(itrChildIterator.hasNext()){
                    INetConfigurationParameterData netConfigChildData = (INetConfigurationParameterData)itrChildIterator.next();
                    List sortedList = new ArrayList();
                    sortedList.addAll(netConfigChildData.getNetConfigParamValues());
                    Collections.sort(sortedList);
                    Iterator itrChildValueIterator = sortedList.iterator();
                    
                    while(itrChildValueIterator.hasNext()){
                        
                        INetConfigurationValuesData netChildValueData = (INetConfigurationValuesData)itrChildValueIterator.next();						
                        if(netChildValueData.getConfigInstanceId()==configInstanceId && 
                        netChildValueData.getInstanceId().startsWith(rootInstanceId) && 
                        netChildValueData.getInstanceId().substring(rootInstanceId.length(),netChildValueData.getInstanceId().length()).startsWith(".")								
                        ){
                            
                            String strValue;
                            if(netChildValueData.getValue() == null)
                                strValue = "";
                            else
                                strValue = netChildValueData.getValue();
                            
                            String status = netConfigChildData.getStatus();
                           
                            if(status==null || status.equalsIgnoreCase("Y")){
                            	if(netConfigChildData.getType().equalsIgnoreCase("A")){
                            		rootNode.setAttribute(netConfigChildData.getAlias(),strValue);
                            	}else {
                            		Element childNode = xmlDoc.createElement(netConfigChildData.getAlias());
                            		Node textNode;								
                            		textNode = xmlDoc.createTextNode(strValue);								
                            		if(netConfigChildData.getNetConfigChildParameters() != null && netConfigChildData.getNetConfigChildParameters().size() > 0){
                            			xmlDoc = generateXMLParseNode(netConfigChildData,xmlDoc,childNode,configInstanceId,netChildValueData.getInstanceId());
                            		}

                            		if(netConfigChildData.getType().equalsIgnoreCase("L")){
                            			childNode.appendChild(textNode);
                            		}
                            		rootNode.appendChild(childNode);

                            	}
                            }else{
                            	Logger.logInfo(MODULE, "Parameter [Parameter Id :"+netConfigChildData.getParameterId()+", Alias :"+netConfigChildData.getAlias()+"] considered disabled paramter.");
                            }
                            
                        }
                    }
                }
                
                
            }
        }catch(Throwable e){
            throw new DataManagerException("Update Basic Detail Failed "+e.getMessage(),e);			
        }
        return xmlDoc;
    }
    
	protected INetConfigurationParameterData generateDataObjectParseNode(INetConfigurationParameterData configParamData, Node currentNode, long configInstanceId, String instanceId) throws DataManagerException {
		EliteAssert.notNull(configParamData);
		EliteAssert.notNull(currentNode);
		
		EliteAssert.notNull(instanceId);

		String value = null;
		INetConfigurationValuesData configValuesData = new NetConfigurationValuesData();
		configValuesData.setConfigInstanceId(configInstanceId);
		configValuesData.setParameterId(configParamData.getParameterId());
		configValuesData.setConfigId(configParamData.getConfigId());

		try{
			if(configParamData.getNetConfigChildParameters() == null || configParamData.getNetConfigChildParameters().size() <= 0  || configParamData.getType().equalsIgnoreCase("L")){
				Text result = (Text) currentNode.getFirstChild();
				if(result != null)
					value = result.getNodeValue();
			}
		}catch(ClassCastException e){
			Logger.logTrace("generateDataObjectParseNode",e);
		}
		configValuesData.setValue(value);
		configValuesData.setInstanceId(instanceId);
		configParamData.getNetConfigParamValues().add(configValuesData);


		Iterator itrNetConfigParameters = configParamData.getNetConfigChildParameters().iterator();
		while(itrNetConfigParameters.hasNext()){
			INetConfigurationParameterData netConfigParamChildData = (INetConfigurationParameterData)itrNetConfigParameters.next();
			int count = netConfigParamChildData.getSerialNo();
			for(int i=0;i<currentNode.getAttributes().getLength();i++,count++){
				Node newNode = currentNode.getAttributes().item(i);
				if(newNode.getNodeName().equalsIgnoreCase(netConfigParamChildData.getAlias())){					
					netConfigParamChildData = generateDataObjectParseNode(netConfigParamChildData,newNode,configInstanceId,instanceId+"."+(count));
				}else{
					if(count > 0)
						count--;
				}
			}

			count = netConfigParamChildData.getSerialNo();
			NodeList lstDetailObjectList = currentNode.getChildNodes();
			for(int i=0;i<lstDetailObjectList.getLength();i++,count++ ){
				Node newNode = lstDetailObjectList.item(i);
				if(newNode.getNodeName().equalsIgnoreCase(netConfigParamChildData.getAlias())){
					netConfigParamChildData = generateDataObjectParseNode(netConfigParamChildData,newNode,configInstanceId,instanceId+"."+(count));
				}else{
					if(count > 0)
						count--;
				}
			}
		}
		return configParamData;
	}

	protected INetConfigurationParameterData flushParameterValue(INetConfigurationParameterData configParamData) throws DataManagerException {
		try{
			if(configParamData.getNetConfigParamValues() != null){
				configParamData.setNetConfigParamValues(new HashSet());
			}
			Iterator itrNetConfigParameters = configParamData.getNetConfigChildParameters().iterator();
			while(itrNetConfigParameters.hasNext()){
				INetConfigurationParameterData netConfigParamChildData = (INetConfigurationParameterData)itrNetConfigParameters.next(); 
				netConfigParamChildData = flushParameterValue(netConfigParamChildData);
			}	
		}catch(Exception e){
			Logger.logTrace("flushParameterValue",e);
		}
		return configParamData;
	}

}