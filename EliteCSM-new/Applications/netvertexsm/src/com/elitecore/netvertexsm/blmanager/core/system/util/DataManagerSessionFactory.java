package com.elitecore.netvertexsm.blmanager.core.system.util;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.logger.Logger;

public class DataManagerSessionFactory implements ErrorHandler {
    
    private static final String MODULE = "SESSION FACTORY";
    
    private boolean initialized;
    private String sessionClassName;
    
    private static DataManagerSessionFactory sessionBindingInstance;
    
    private DataManagerSessionFactory(){
    }
    
    public static final DataManagerSessionFactory getInstance(){
        if (sessionBindingInstance == null) {
            synchronized (DataManagerFactory.class) {
                if (sessionBindingInstance == null){
                    sessionBindingInstance = new DataManagerSessionFactory();
                    sessionBindingInstance.init();
                }
            }
        }
        
        return sessionBindingInstance;
    }
    
    
    private void init() {
        if (!initialized) {
            try {
                
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                factory.setIgnoringElementContentWhitespace(true);
                factory.setValidating(true);
    
                DocumentBuilder documentBuilder =  factory.newDocumentBuilder();
                documentBuilder.setErrorHandler(this);
                Document document = documentBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/elitecore/netvertexsm/elite-dm-binding.xml"));
                
                NodeList datamanagerBindingList =  document.getElementsByTagName("session-binding");
                
                for (int i=0; i < datamanagerBindingList.getLength(); i++) {
                    Node serviceNode = datamanagerBindingList.item(i);
                    NodeList managerDetails = serviceNode.getChildNodes();
                    
                    for (int j=0; j<managerDetails.getLength(); j++) {
                        Node subNode = managerDetails.item(j);
                        if (subNode.getNodeName().equals("session-class")) {
                            sessionClassName = subNode.getChildNodes().item(0).getNodeValue();
                        }
                    }
                    
                    if (sessionClassName != null ) {
                        Logger.logInfo(MODULE, "Session implementation class configured : " + sessionClassName);
                        initialized = true;
                    }
                }
                
            } catch (Exception e) {
                Logger.logError(MODULE,"Error reading Session Binding configuration, reason : " + e.getMessage() );
                Logger.logTrace(MODULE,e);
            }
        }else {
            Logger.logInfo(MODULE,"Already initialized.");
        }
    }
    
    public final IDataManagerSession getDataManagerSession(){
        
        IDataManagerSession dataManagerSession = null;
        
        //if not initialized, try to initialize it.
        if (!initialized) {
            init();
        }
        
        if (initialized){
            
            try {
                dataManagerSession = (IDataManagerSession)Class.forName(sessionClassName).newInstance();
            } catch (Exception e) {
                Logger.logError(MODULE,"Error creating data manager session , reason : " + e.getMessage());
                Logger.logTrace(MODULE,e);
            }
        }
        
        return dataManagerSession;
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
    
    
    
    public IDataManagerSession createDataManagerSession(){
        return null;
    }

    public void closeDataManagerSession(IDataManagerSession session) {
        
    }
    
}
