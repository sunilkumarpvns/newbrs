package com.elitecore.netvertexsm.blmanager.core.system.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.logger.Logger;


public final class DataManagerFactory implements ErrorHandler{
    
    private static final String MODULE = "DATA MANAGER FACTORY";
    
    private boolean initialized;
    private Map datamanagerMap;
    
    private static DataManagerFactory managerBindingInstance;
    
    private DataManagerFactory(){
        datamanagerMap = new HashMap();
    }
    
    
    public static final DataManagerFactory getInstance(){
        if (managerBindingInstance == null) {
            synchronized (DataManagerFactory.class) {
                if (managerBindingInstance == null){
                    managerBindingInstance = new DataManagerFactory();
                    managerBindingInstance.init();
                }
            }
        }
        
        return managerBindingInstance;
    }
    
    
    private void init() {
        if (!initialized) {
            try {
                Logger.logInfo(MODULE,"init() Called.");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                factory.setIgnoringElementContentWhitespace(true);
                factory.setValidating(true);
                
                DocumentBuilder documentBuilder =  factory.newDocumentBuilder();
                documentBuilder.setErrorHandler(this);
                Document document = documentBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/elitecore/netvertexsm/elite-dm-binding.xml"));
                
                NodeList datamanagerBindingList =  document.getElementsByTagName("datamanager-binding");
                Node dataManagerBindingNode = datamanagerBindingList.item(0);
                NodeList dataManagerList = dataManagerBindingNode.getChildNodes();
                
                Logger.logDebug(MODULE,"Adding data manager");
                Logger.logDebug(MODULE,"**************************************************************");
                for (int i=0; i < dataManagerList.getLength(); i++) {
                    Node dataManagerNode = dataManagerList.item(i);
                    NodeList dataManager = dataManagerNode.getChildNodes();
                    
                    String managerName = null;
                    String managerClass = null;
                    
                    for (int j=0; j<dataManager.getLength(); j++) {
                        
                        Node subNode = dataManager.item(j);
                        if (subNode.getNodeName().equals("manager-name")) {
                            managerName = subNode.getChildNodes().item(0).getNodeValue();
                        }
                        if (subNode.getNodeName().equals("manager-class")) {
                            managerClass = subNode.getChildNodes().item(0).getNodeValue();
                        }
                    }
                    if (managerName != null  && managerName.trim().length() != 0  &&    managerClass != null  && managerClass.trim().length() != 0 ) {
                        datamanagerMap.put(managerName, managerClass);
                        Logger.logDebug(MODULE,(i+1) +" : "+ managerClass);
                     }
                }
                Logger.logDebug(MODULE,"**************************************************************");
                initialized = true;
                Logger.logInfo(MODULE,"Data manager Successfully initialized.");
            } catch (Exception e) {
                Logger.logError(MODULE,"Error reading Data Manager Binding configuration, reason : " + e.getMessage() );
                Logger.logTrace(MODULE,e);
                initialized = false;
                datamanagerMap = new HashMap();
            }
        }else {
            Logger.logInfo(MODULE,"Already initialized.");
        }
    }
    
    public final DataManager getDataManager(Class cls, IDataManagerSession session){
        
        DataManager dataManager = null;
        
        //if not initialized, try to initialize it.
        if (!initialized) {
            init();
        }
        
        if (initialized){
            String managerClass = String.valueOf(datamanagerMap.get(cls.getName()));
            try {
                dataManager = (DataManager)Class.forName(managerClass).newInstance();
                dataManager.setDataManagerSession(session);
            } catch (Throwable e) {
                Logger.logError(MODULE,"Error creating data manager for " + cls.getSimpleName()+ " , Reason : " + e.getMessage());
                Logger.logWarn(MODULE,"Please Verify elite-dm-binding.xml.");
                Logger.logTrace(MODULE,e);
            }
        }
        
        return dataManager;
    }
    
    
    public void warning(SAXParseException exception) throws SAXException {
        // TODO Auto-generated method stub
        
    }
    
    public void error(SAXParseException exception) throws SAXException {
        // TODO Auto-generated method stub
        
    }
    
    public void fatalError(SAXParseException exception) throws SAXException {
        // TODO Auto-generated method stub
        
    }
    
    
}
