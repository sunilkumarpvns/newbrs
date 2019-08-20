package com.elitecore.netvertexsm.blmanager.core.system.profilemanagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.ProfileDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleTypeData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ConfigurationProfileData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleTypeData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IConfigurationProfileData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ProfileBISModelRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.exception.InvalidUploadedProfileException;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.SystemParameterDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

public class ProfileBLManager extends BaseBLManager{
    private static final String MODULE = "PROFILE MANAGEMENT";
    
    /**
     * @author dhaval raval
     * @param session
     * @return Returns Data Manager instance for Servermgr data.
     */
    public ProfileDataManager getProfileDataManager(IDataManagerSession session){
        ProfileDataManager profileDataManager = (ProfileDataManager)DataManagerFactory.getInstance().getDataManager(ProfileDataManager.class,session);
        return profileDataManager;
    }
    
    /**
     * @author  dhavalraval
     * @return  List
     * @purpose This method returns the list of ConfigurationProfile.
     */
    public List getConfigurationProfileList() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstConfigurationProfileList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        lstConfigurationProfileList = profileDataManager.getConfigurationProfileList();
        }catch(Exception e){
        	throw new DataManagerException(e);
        }finally{
        	session.close();
        }
        return lstConfigurationProfileList;
    }
    
    /**
     * @author  dhaval raval
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of BusinessModel
     */
    public List getBusinessModelList() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstBusinessList;
		try {
			if(profileDataManager == null)
			    throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			
			lstBusinessList = profileDataManager.getBusinessModelList();
		}finally{
			session.close();
		}
        return lstBusinessList;
    }
    
    public List getBISModelModuleRelList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	List lstBISModelModuleRelList;
    	try{   

    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

    		lstBISModelModuleRelList = profileDataManager.getBISModelModuleRelList();
    	}catch (DataManagerException e) {
    		throw e;
    	}finally{
    		session.close();
    	}

    	return lstBISModelModuleRelList;
    }
    
    /**
     * @author  dhavalraval
     * @return  List
     * @throws  DatatManagerException
     * @purpose This method returns the list of all the records of BusinessModule
     */
    public List getBusinessModuleList() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstBusinessModuleList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        lstBusinessModuleList = profileDataManager.getBusinessModuleList();
        }finally{
    		session.close();
    	}
        return lstBusinessModuleList;
    }
    
    /**
     * @author  dhavalraval
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of the Sub-BusinessModule
     */
    public List getSubBusinessModuleList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);

    	List lstSubBusinessModuleList;
    	try{   
    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

    		lstSubBusinessModuleList = profileDataManager.getSubBusinessModuleList();
    	}finally{
    		session.close();
    	}
        return lstSubBusinessModuleList;
    }
    
    /**
     * @author  dhavalraval
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of the BusinessModuleType
     */
    public List getBusinessModuleTypeList() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstBusinessModuleTypeList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            lstBusinessModuleTypeList = profileDataManager.getBusinessModuleTypeList();
        }finally{
    		session.close();
    	}
        return lstBusinessModuleTypeList;
    }
    
    public List getActionList() throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstActionList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        lstActionList = profileDataManager.getActionList();
        }finally{
    		session.close();
    	}
        return lstActionList;
    }
    
    /**
     * @author  dhavalraval
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of the SubBusinessModuleType
     */
    public List getSubBusinessModuleTypeList() throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);

    	List lstSubBusinessModuleTypeList;
    	try{ 
    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

    		lstSubBusinessModuleTypeList = profileDataManager.getSubBusinessModuleTypeList();
    	}finally{
    		session.close();
    	}	   
    	return lstSubBusinessModuleTypeList;
    }
    
    /**
     * @author  dhavalraval
     * @param   BISModelId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of the BISModelModuleRelList
     */
    public List getBISModelModuleRelList(String BISModelId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	List lstBISModelModelRelList;
    	try{ 
    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager imlpementation not found for "+getClass().getName());
    		lstBISModelModelRelList = profileDataManager.getBISModelModuleRelList(BISModelId);
    	}finally{
    		session.close();
    	} 
    	return lstBISModelModelRelList;
    }
    
    /**
     * @author  dhavalraval
     * @param   businessModuleId
     * @return  IBISModelModuleRelData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModelModuleRelData   
     **/
    public IBISModelModuleRelData getBusinessModelModuleRelList(String businessModuleId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IBISModelModuleRelData bisModelModuleRelData = new BISModelModuleRelData();
    	bisModelModuleRelData.setBusinessModelId(businessModuleId);
    	try{
    		List lstBISModelModuelRelList = profileDataManager.getBusinessModelModuleRelList(bisModelModuleRelData.getBusinessModelId());

    		if(lstBISModelModuelRelList != null && lstBISModelModuelRelList.size() >= 1){
    			bisModelModuleRelData = (IBISModelModuleRelData)lstBISModelModuelRelList.get(0);
    		}else{
    			bisModelModuleRelData = null;
    		}
    	}catch (DataManagerException e) {
    		throw e;
    	}finally{
    		session.close();
    	}
    	return bisModelModuleRelData;
    }
    
    /**
     * @author  dhavalraval
     * @param   configurationProfileId
     * @return  IConfigurationProfileData object
     * @throws  DataManagerException
     * @purpose This method returns the IConfigurationProfileData object on the basis of ConfigurationProfileId.
     */
    public IConfigurationProfileData getConfigurationProfileName(String configurationProfileId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IConfigurationProfileData configurationProfileData = new ConfigurationProfileData();
    	configurationProfileData.setConfigurationProfileId(configurationProfileId);
    	try{
    		List lstConfigurationProfileList = profileDataManager.getConfigurationProfileName(configurationProfileData.getConfigurationProfileId());
    		if(lstConfigurationProfileList != null && lstConfigurationProfileList.size() >=1){
    			configurationProfileData = (IConfigurationProfileData)lstConfigurationProfileList.get(0);
    		}else{
    			configurationProfileData = null;
    		}
    	}finally{
    		session.close();
    	}
    	return configurationProfileData;
    }
    
    /**
     * @author  dhavalraval
     * @param   profileName
     * @return  IConfigurationProfileData Object
     * @throws  DataManagerException
     * @purpose This method returns the object of IConfigurationProfileData
     */
    public IConfigurationProfileData getConfigurationProfileData(String profileName) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IConfigurationProfileData configurationProfileData = new ConfigurationProfileData();
    	configurationProfileData.setAlias(profileName);
    	try{
    		List lstConfigurationProfileList = profileDataManager.getConfigurationProfileData(configurationProfileData.getAlias());

    		if(lstConfigurationProfileList != null && lstConfigurationProfileList.size() >=1){
    			configurationProfileData = (IConfigurationProfileData)lstConfigurationProfileList.get(0);
    		}else{
    			configurationProfileData = null;
    		}
    	}finally{
    		session.close();
    	}
    	return configurationProfileData;
    }
    
    /**
     * @author  dhavalraval
     * @param   subBusinessModuleId
     * @return  IBISModuleSubBISModuleRelData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModuleSubBISModuleRelData object
     */
    public IBISModuleSubBISModuleRelData getBISModuleSubBISModuleRel(String subBusinessModuleId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IBISModuleSubBISModuleRelData bisModuleSubBisModuleRelData = new BISModuleSubBISModuleRelData();
        bisModuleSubBisModuleRelData.setSubBusinessModuleId(subBusinessModuleId);
        try{
        List lstBISModuleSubBisModuleRelList = profileDataManager.getBISModuleSubBISModuleRel(bisModuleSubBisModuleRelData.getSubBusinessModuleId());
        
        if(lstBISModuleSubBisModuleRelList != null && lstBISModuleSubBisModuleRelList.size() >= 1){
            bisModuleSubBisModuleRelData = (IBISModuleSubBISModuleRelData)lstBISModuleSubBisModuleRelList.get(0);
        }else{
            bisModuleSubBisModuleRelData = null;
        }
        }finally{
    		session.close();
    	}
        return bisModuleSubBisModuleRelData;
    }
    
    /**
     * @author  dhavalraval
     * @param   BISModelId
     * @return  IBISModelModuleRelData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModelModuleRelData object
     */
    public IBISModelModuleRelData getBISModelModuleRel(String BISModelId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IBISModelModuleRelData bisModelModuleRelData = new BISModelModuleRelData();
    	bisModelModuleRelData.setBusinessModelId(BISModelId);
    	try{
    		List lstBISModelModuelRelList = profileDataManager.getBISModelModuleRelList(bisModelModuleRelData.getBusinessModelId());

    		if(lstBISModelModuelRelList != null && lstBISModelModuelRelList.size() >= 1){
    			bisModelModuleRelData = (IBISModelModuleRelData)lstBISModelModuelRelList.get(0);
    		}else{
    			bisModelModuleRelData = null;
    		}
    	}finally{
    		session.close();
    	}
    	return bisModelModuleRelData;
    }
    /**
     * @author  dhavalraval
     * @param   BISModuleId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of BISModuleSubBISModuleRelList
     */
    public List getBISModuleSubBISModuleRelList(String BISModuleId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);

    	List lstBISModuleSubBISModuleList; 
    	try{   
    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

    		lstBISModuleSubBISModuleList = profileDataManager.getBISModuleSubBISModuleRelList(BISModuleId);

    	}finally{
    		session.close();
    	}
    	return lstBISModuleSubBISModuleList;
    }
    
    
    /**
     * @author  dhavalraval
     * @param   subBusinessModuleId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of SubBISModuleActionRelList
     */
    public List getSubBISModuleActionRelList(String subBusinessModuleId) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstSubBISModuleActionList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager imlementation not found for "+getClass().getName());
        
        lstSubBISModuleActionList = profileDataManager.getSubBISModuleActionRelList(subBusinessModuleId);
        }finally{
        	session.close();
        }
        return lstSubBISModuleActionList;
    }
    
    /**
     * @author  dhavalraval
     * @param   actionId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of SubBISModuleActionRelList
     */
    public List getActionSubBISModuleRelList(String actionId) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	List lstActionSubBISModulelist;
    	try{   
    		if(profileDataManager == null)
    			throw new DataManagerException("Data Manager imlementation not found for "+getClass().getName());

    		lstActionSubBISModulelist = profileDataManager.getActionSubBISModuleRelList(actionId);

    	}finally{
    		session.close();
    	}

    	return lstActionSubBISModulelist;

    }
    
    /**
     * @author  dhavalraval
     * @param   subBusinessModuleId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of all the records of 
     */
    public List getSubBISModuleBISModuleRelList(String subBusinessModuleId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstSubBISModuleBISModuleRelList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager imlementation not found for "+getClass().getName());
        
        lstSubBISModuleBISModuleRelList = profileDataManager.getSubBISModuleBISModuleRelList(subBusinessModuleId);
        }finally{
         session.close();
        }
        return lstSubBISModuleBISModuleRelList;
    }
    
    
    public List getBISModuleModelRelList(String BISModuleId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        List lstBISModuleModelRelList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager imlementation not found for "+getClass().getName());
        
        lstBISModuleModelRelList = profileDataManager.getBISModuleModelRelList(BISModuleId);
        }finally{
         session.close();
        }
        return lstBISModuleModelRelList;
    }
    
    /**
     * @author  dhavalraval
     * @param   BISModelId
     * @return  IBISModelData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModelData object
     */	
    public IBISModelData getBISModel(String BISModelId ) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IBISModelData bisModelData = new BISModelData();
        bisModelData.setBusinessModelId(BISModelId);
        try{
        List lstBISModel = profileDataManager.getBISModelList(bisModelData);
        
        if(lstBISModel != null && lstBISModel.size() >=1){
            bisModelData = (IBISModelData)lstBISModel.get(0);
        }else{
            bisModelData = null;
        }
        }finally{
        	session.close();
        }
        return bisModelData;
    }
    
    /**
     * @author  dhavalraval
     * @param   BISModuleId
     * @return  IBISModuleData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModuleData object
     */
    public IBISModuleData getBISModule(String BISModuleId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IBISModuleData bisModuleData = new BISModuleData();
        try{
        bisModuleData.setBusinessModuleId(BISModuleId);
        
        List lstBISModule = profileDataManager.getBISModuleList(bisModuleData);
        
        if(lstBISModule != null && lstBISModule.size() >=1){
            bisModuleData = (IBISModuleData)lstBISModule.get(0);
        }else{
            bisModuleData = null;
         }
        }finally{
        	session.close();
        }
        return bisModuleData;
    }
    
    public IBISModuleData getBISModuleAlias(String moduleAlias) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IBISModuleData bisModuleData = new BISModuleData();
        bisModuleData.setAlias(moduleAlias);
        try{
        List lstBISModule = profileDataManager.getBISModuleAlias(bisModuleData);
        
        if(lstBISModule != null && lstBISModule.size() >=1){
            bisModuleData = (IBISModuleData)lstBISModule.get(0);
        }else{
            bisModuleData = null;
        }
        }finally{
         session.close();
        }
        return bisModuleData;
    }
    
    public IBISModelData getBISModelAlias(String modelAlias) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IBISModelData bisModelData = new BISModelData();
    	bisModelData.setAlias(modelAlias);
    	try{
    		List lstBISModel = profileDataManager.getBISModelAlias(bisModelData);

    		if(lstBISModel != null && lstBISModel.size()>=1){
    			bisModelData = (IBISModelData)lstBISModel.get(0);
    		}else{
    			bisModelData = null;
    		}
    	}finally{
    		session.close();
    	}
    	return bisModelData;
    }
    
    /**
     * @author  dhavalraval
     * @param   subBusinessModuleId
     * @return  ISubBISModuleData Object
     * @throws  DataManagerException
     * @purpose This method returns the object of ISubBISModuleData object
     */
    public ISubBISModuleData getSubBISModule(String subBusinessModuleId ) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        ISubBISModuleData subBISModuledata = new SubBISModuleData();
        try{
        subBISModuledata.setSubBusinessModuleId(subBusinessModuleId);
        
        List lstSubBISModule = profileDataManager.getSubBISModuleList(subBISModuledata);
        
        if(lstSubBISModule != null && lstSubBISModule.size() >=1){
            subBISModuledata = (ISubBISModuleData)lstSubBISModule.get(0);
        }else{
            subBISModuledata = null;
        }
        }finally{
        	session.close();
        }
        return subBISModuledata;
    }
    
    public ISubBISModuleData getSubBISModuleAlias(String subModuleAlias) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        ISubBISModuleData subBISModuleData = new SubBISModuleData();
        subBISModuleData.setAlias(subModuleAlias);
        try{
        List lstSubBISModule = profileDataManager.getSubBISModuleAlias(subBISModuleData);
        
        if(lstSubBISModule != null && lstSubBISModule.size() >=1){
            subBISModuleData = (ISubBISModuleData)lstSubBISModule.get(0);
        }else{
            subBISModuleData = null;
        }
        }finally{
        	session.close();
        }
        return subBISModuleData;
    }
    
    public IActionData getActionAlias(String actionAlias) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	ProfileDataManager profileDataManager = getProfileDataManager(session);
    	IActionData actionData = new ActionData();
    	actionData.setAlias(actionAlias);
    	try{
    		List lstActionModule = profileDataManager.getActionAlias(actionData);

    		if(lstActionModule != null && lstActionModule.size() >=1){
    			actionData = (IActionData)lstActionModule.get(0);
    		}else{
    			actionData = null;
    		}
    		}finally{
    			session.close();
    		}
    	return actionData;
    }
    
    /**
     * @author  dhavalraval
     * @param   actionId
     * @return  IActionData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IActionData object.
     */
    public IActionData getActionData(String actionId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IActionData actionData = new ActionData();
        actionData.setActionId(actionId);
        
        List lstAction = profileDataManager.getActionList(actionData);
        try{
        	if(lstAction != null && lstAction.size() >=1){
        		actionData = (IActionData)lstAction.get(0);
        	}else{
        		actionData = null;
        	}
        }finally{
        	session.close();
        }
        return actionData;
    }
    
    /**
     * @author  dhavalraval
     * @param   bisModuleTypeID
     * @return  IBISModuleTypeData object
     * @throws  DataManagerException
     * @purpose This method returns the object of IBISModuleTypeData object
     */
    public IBISModuleTypeData getBISModuleTypeData(String bisModuleTypeID) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IBISModuleTypeData bisModuleTypeData = new BISModuleTypeData();
        bisModuleTypeData.setBisModuleTypeId(bisModuleTypeID);
        try{
        List lstBisModuleTypeList = profileDataManager.getBISModuleTypeList(bisModuleTypeData);
        
        if(lstBisModuleTypeList != null && lstBisModuleTypeList.size() >=1){
            bisModuleTypeData = (IBISModuleTypeData)lstBisModuleTypeList.get(0);
        }else{
            bisModuleTypeData = null;
        }
        }finally{
         session.close();
        }
        return bisModuleTypeData;
    }
    
    /**
     * @author  dhavalraval
     * @param   businssModelId
     * @param   businessModuleTypeId
     * @param   businessModuleName
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of BusinessModel.
     */
    public List search(String businssModelId,String businessModuleTypeId,String businessModuleName) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstBISModuleList;
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        lstBISModuleList = profileDataManager.search(businssModelId,businessModuleTypeId,businessModuleName);
        }finally{
         session.close();
        }
        return lstBISModuleList;
    }
    
    /**
     * @author  dhavalraval
     * @param   businessModelId
     * @param   businessModuleTypeId
     * @param   subBusinessModuleTypeId
     * @param   subBusinessModuleName
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of BusinessModule
     */
    public List search(String businessModelId,String businessModuleId,String subBusinessModuleTypeId,String subBusinessModuleName) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstSubBISModuleList; 
        try{
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for :"+getClass().getName());
        
        lstSubBISModuleList = profileDataManager.search(businessModelId,businessModuleId,subBusinessModuleTypeId,subBusinessModuleName);
        
       }finally{
        session.close();
       }	
        return lstSubBISModuleList;
    }
    
    /**
     * @author  dhavalraval
     * @param   configurationProfileId
     * @return  List
     * @throws  DataManagerException
     * @purpose This method returns the list of ProfileBISModelRelData (returns list of profile,model relation table)
     */
    public List getProfileBISModelRelList(String configurationProfileId) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstProfileBISModelRelList; 
       try{ 
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for :"+getClass().getName());
        
        lstProfileBISModelRelList = profileDataManager.getProfileBISModelRelList(configurationProfileId);
       }finally{
        session.close();
       }
        return lstProfileBISModelRelList;
    }
    
    /**
     * @author  dhavalraval
     * @param   lstBusinessModelIds
     * @param   businessModelProfileStatus
     * @throws  DataManagerException
     * @purpose This method is generate to Update the freezeProfileStatus of BusinessModel
     */
    public void updateModelProfileStatus(List lstBusinessModelIds,String businessModelProfileStatus) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        try {
            session.beginTransaction();
            
            if(lstBusinessModelIds != null){
                for(int i=0;i<lstBusinessModelIds.size();i++){
                    if(lstBusinessModelIds.get(i) != null){
                        profileDataManager.updateModelProfileStatus(lstBusinessModelIds.get(i).toString(),businessModelProfileStatus);
                    }
                }
                session.commit();
            }else{
                throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            }
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   lstBusinessModuleIds
     * @param   businessModuleProfileStatus
     * @throws  DataManagerException
     * @purpose This method is generate to update the freezeProfileStatus of BusinessModule
     */
    public void updateBusinessModuleProfileStatus(List lstBusinessModuleIds,String businessModuleProfileStatus) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        try {
            session.beginTransaction();
            if(lstBusinessModuleIds != null){
                for(int i=0;i<lstBusinessModuleIds.size();i++){
                    if(lstBusinessModuleIds.get(i) != null){
                        profileDataManager.updateBusinessModuleProfileStatus(lstBusinessModuleIds.get(i).toString(),businessModuleProfileStatus);
                    }
                }
                session.commit();
            }else{
                throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            }
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   lstSubBusinessModuleIds
     * @param   subBusinessModuleProfileStatus
     * @throws  DataManagerException
     * @purpose This method is generate to update the freezeProfileStatus of subBusinessModule
     */
    public void updateSubBusinessModuleProfileStatus(List lstSubBusinessModuleIds,String subBusinessModuleProfileStatus) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session); 
        
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        try {
            session.beginTransaction();
            if(lstSubBusinessModuleIds != null){
                for(int i=0;i<lstSubBusinessModuleIds.size();i++){
                    if(lstSubBusinessModuleIds.get(i) != null){
                        profileDataManager.updateSubBusinessModuleProfileStatus(lstSubBusinessModuleIds.get(i).toString(),subBusinessModuleProfileStatus);
                    }
                }
                session.commit();
            }else{
                throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            }
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   lstActionIds
     * @param   actionProfileStatus
     * @throws  DataManagerException
     * @purpose This method is generated to update the freezeProfileStatus of Action
     */
    public void updateActionProfileStatus(List lstActionIds,String actionProfileStatus) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        try {
            session.beginTransaction();
            if(lstActionIds != null){
                for(int i=0;i<lstActionIds.size();i++){
                    if(lstActionIds.get(i) != null)
                        profileDataManager.updateActionProfileStatus(lstActionIds.get(i).toString(),actionProfileStatus);
                }
                session.commit();
            }else{
                throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            }
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   profileAlias
     * @throws  DataManagerException
     * @purpose This method is generated to freeze the profile.
     */
    public void updateFreezeModelProfile(String profileAlias) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        IConfigurationProfileData configurationProfileData = new ConfigurationProfileData();
        
        if(profileDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        
        try {
            session.beginTransaction();
            configurationProfileData = getConfigurationProfileData(profileAlias);
            profileDataManager.updateFreezeModelProfile();
            profileDataManager.updateFreezeModuleProfile();
            profileDataManager.updateFreezeSubModuleProfile(); 
            profileDataManager.updateFreezeActionProfile();
            byte[] xmlData = generateXMLProfile(profileAlias);
            profileDataManager.updateFreezeConfigurationProfile(configurationProfileData.getConfigurationProfileId(),xmlData);
            session.commit();
        } catch (DataManagerException exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   profileName
     * @return  byte[]
     * @purpose This method is generated get byte[] on the basis of profile name.
     */
    public byte[] generateXMLProfile(String profileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();	
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement(ConfigConstant.PROFILE);
            root.setAttribute("name",profileName);
            root.setAttribute("alias",profileName);
            
            IConfigurationProfileData configurationProfileData = getConfigurationProfileData(profileName);
            String configurationProfileId = configurationProfileData.getConfigurationProfileId();
            List profileBISModelRelList = getProfileBISModelRelList(configurationProfileId);
            
            for(int i=0;i<profileBISModelRelList.size();i++){
                String businessModelId = ((ProfileBISModelRelData)profileBISModelRelList.get(i)).getBusinessModelId();
                IBISModelData bisModelData = getBISModel(businessModelId);
                Element childModelElement = document.createElement("MODEL");
                childModelElement.setAttribute("status",bisModelData.getStatus());
                childModelElement.setAttribute("name",bisModelData.getName());
                childModelElement.setAttribute("alias",bisModelData.getAlias());
                
                List businessModelModuleRelList = getBISModelModuleRelList(bisModelData.getBusinessModelId());
                for(int j=0;j<businessModelModuleRelList.size();j++){
                    String businessModuleId = ((BISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId();
                    IBISModuleData bisModuleData = getBISModule(businessModuleId);
                    Element childModuleElement = document.createElement("MODULE");
                    childModuleElement.setAttribute("status",bisModuleData.getStatus());
                    childModuleElement.setAttribute("name",bisModuleData.getName());
                    childModuleElement.setAttribute("alias",bisModuleData.getAlias());
                    
                    List businessModuleSubBISModuleRelList = getBISModuleSubBISModuleRelList(bisModuleData.getBusinessModuleId());
                    for(int k=0;k<businessModuleSubBISModuleRelList.size();k++){
                        String subBusinessModuleId = ((BISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId();
                        ISubBISModuleData subBISModuleData = getSubBISModule(subBusinessModuleId);
                        Element childSubModuleElement = document.createElement("SUBMODULE");
                        childSubModuleElement.setAttribute("status",subBISModuleData.getStatus());
                        childSubModuleElement.setAttribute("name",subBISModuleData.getName());
                        childSubModuleElement.setAttribute("alias",subBISModuleData.getAlias());
                        
                        List subBusinessModuleActionRelList = getSubBISModuleActionRelList(subBISModuleData.getSubBusinessModuleId());
                        for(int l=0;l<subBusinessModuleActionRelList.size();l++){
                            String actionId = ((SubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId();
                            IActionData actionData = getActionData(actionId);
                            Element childActionElement = document.createElement("ACTION");
                            childActionElement.setAttribute("status",actionData.getStatus());
                            childActionElement.setAttribute("name",actionData.getName());
                            childActionElement.setAttribute("alias",actionData.getAlias());
                            
                            childSubModuleElement.appendChild(childActionElement);
                        }
                        childModuleElement.appendChild(childSubModuleElement);
                    }
                    childModelElement.appendChild(childModuleElement);
                }
                root.appendChild(childModelElement);	
            }
            
            document.appendChild(root);
            
            
            System.setProperty(BaseConstant.TRANSFORMER_FACTORY, BaseConstant.TRANSFORMER_FACTORY_IMPL);
            
            
            TransformerFactory tFactory =TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            
        } catch (Exception e) {
            Logger.logTrace(MODULE,e);
        }
        return outputStream.toByteArray();
    }
    
    /**
     * @author  dhavalraval
     * @param   lastProfile
     * @param   lastProfileAlias
     * @param   currentProfile
     * @param   rootNode
     * @throws  DataManagerException
     * @purpose Thism method is generated to verify the last-profile and current profile.
     */
    public void performProfileVerification(String lastProfile, String lastProfileAlias, String  currentProfile, String rootNode) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        try{
            session.beginTransaction();
            
            if(currentProfile != null){
                if(lastProfile.equalsIgnoreCase(currentProfile)){
                    session.rollback();
                    session.close();
                    return;
                }else{
                    try{
                        IConfigurationProfileData configurationProfileData = getConfigurationProfileData(currentProfile);
                        if(configurationProfileData != null){
                            byte[] xmlData = configurationProfileData.getXmlData();
                            if(xmlData != null){
                                updateProfileStatus(xmlData,rootNode,profileDataManager,configurationProfileData);
                            }else{
                                //TODO : Create new XML Document (Byte[]) and save this to current Profile
                                xmlData = generateXMLProfile(currentProfile);
                                configurationProfileData.setXmlData(xmlData);
                                profileDataManager.updateConfigurationProfile(configurationProfileData);
                            }
                            updateLastProfile(profileDataManager, currentProfile, lastProfileAlias);
                            session.commit();
                            
                        }else{
                            session.rollback();
                        }
                    }catch(Exception exp){
                        session.rollback();
                        throw new DataManagerException(exp);
                     }
                }
            }else{
                throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
            }
        }catch (Exception e) {
            session.rollback();
            throw new DataManagerException(e);
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   xmlData
     * @return  Document object
     * @param   byte array
     * @purpose This method returns the document object
     */
    public Document getDocument(byte[] xmlData) {
        Document document = null;
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlData);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(byteArrayInputStream);
            return document;
        }catch(Exception exp){
            exp.printStackTrace();
        }
        return document;
    }
    
    /**
     * @author  dhavalraval
     * @param   document
     * @param   rootNode
     * @return  HashMap
     * @purpose This method is generated to get the NodeList on the basis of roorNode.
     */
    public HashMap getNodeList(Document document,String rootNode){
        HashMap profileMap = new HashMap();
        try {
            NodeList rootProfileNode = document.getElementsByTagName(rootNode);
            
            for(int i=0;i<rootProfileNode.getLength();i++){
                Node node =  rootProfileNode.item(i);
                if(node.getNodeType() == 1){
                    Element nodeElement = (Element)node;
                    profileMap.put("name",nodeElement.getAttribute("name"));
                    profileMap.put("alias",nodeElement.getAttribute("alias"));
                    NodeList profileNodeList = nodeElement.getChildNodes();
                    HashMap modelMap = new HashMap();
                    for(int j=0;j<profileNodeList.getLength();j++){
                        Node modelNode = profileNodeList.item(j);
                        HashMap modelObjectMap = new HashMap();
                        if(modelNode.getNodeType() == 1){
                            Element modelElement = (Element)modelNode;
                            modelObjectMap.put("name",modelElement.getAttribute("name"));
                            modelObjectMap.put("alias",modelElement.getAttribute("alias"));
                            modelObjectMap.put("status",modelElement.getAttribute("status"));
                            NodeList modelNodeList = modelNode.getChildNodes();
                            HashMap moduleMap = new HashMap();
                            for(int k=0;k<modelNodeList.getLength();k++){
                                Node moduleNode = modelNodeList.item(k);
                                HashMap moduleObjectMap = new HashMap();
                                if(moduleNode.getNodeType() == 1){
                                    Element moduleElement = (Element)moduleNode;
                                    moduleObjectMap.put("name",moduleElement.getAttribute("name"));
                                    moduleObjectMap.put("alias",moduleElement.getAttribute("alias"));
                                    moduleObjectMap.put("status",moduleElement.getAttribute("status"));
                                    NodeList moduleNodeList = moduleNode.getChildNodes();
                                    HashMap subModuleMap = new HashMap();
                                    for(int l=0;l<moduleNodeList.getLength();l++){
                                        Node subModuleNode = moduleNodeList.item(l);
                                        HashMap subModuleObjectMap = new HashMap();
                                        if(subModuleNode.getNodeType() == 1){
                                            Element subModuleElement = (Element)subModuleNode;
                                            subModuleObjectMap.put("name",subModuleElement.getAttribute("name"));
                                            subModuleObjectMap.put("alias",subModuleElement.getAttribute("alias"));
                                            subModuleObjectMap.put("status",subModuleElement.getAttribute("status"));
                                            NodeList subModuleNodeList = subModuleNode.getChildNodes();
                                            HashMap actionMap = new HashMap();
                                            for(int m=0;m<subModuleNodeList.getLength();m++){
                                                Node actionNode = subModuleNodeList.item(m);
                                                HashMap actionObjectMap = new HashMap();
                                                if(actionNode.getNodeType() == 1){
                                                    Element actionElement = (Element)actionNode;
                                                    actionObjectMap.put("name",actionElement.getAttribute("name"));
                                                    actionObjectMap.put("alias",actionElement.getAttribute("alias"));
                                                    actionObjectMap.put("status",actionElement.getAttribute("status"));
                                                    actionMap.put(actionElement.getAttribute("alias"),actionObjectMap);
                                                }
                                            }
                                            subModuleObjectMap.put("actionMap",actionMap);
                                            subModuleMap.put(subModuleElement.getAttribute("alias"),subModuleObjectMap);
                                        }
                                    }
                                    moduleObjectMap.put("subModuleMap",subModuleMap);
                                    moduleMap.put(moduleElement.getAttribute("alias"),moduleObjectMap);
                                }
                            }
                            modelObjectMap.put("moduleMap",moduleMap);
                            modelMap.put(modelElement.getAttribute("alias"),modelObjectMap);
                        }
                    }
                    profileMap.put("modelMap",modelMap);
                }
            }
            return profileMap;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return profileMap;
    }
    
    /**
     * @author  dhavalraval
     * @param   xmlData
     * @param   rootNode
     * @param   profileDataManager
     * @param   configurationProfileData
     * @throws  DataManagerException
     * @purpose This method is generated to update the profileStatus used in profileVerification
     */
    private void updateProfileStatus(byte[] xmlData,String rootNode,ProfileDataManager profileDataManager,IConfigurationProfileData configurationProfileData) throws DataManagerException, InvalidUploadedProfileException{
        try{
            Document document = getDocument(xmlData);
            HashMap profileMap = getNodeList(document,rootNode);
            String alias = (String)profileMap.get("alias");
            if(alias == null || alias.trim().equalsIgnoreCase("")){
                alias = (String)profileMap.get("name");
            }
            if(!configurationProfileData.getAlias().equals(alias)){
                throw new InvalidUploadedProfileException();
            }
            
            HashMap modelMap = (HashMap)profileMap.get("modelMap");
            for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
                String modelAlias = (String)w.next();
                HashMap modelObjectMap = (HashMap)modelMap.get(modelAlias);
                String modelStatus = (String)modelObjectMap.get("status");
                String modelName = (String)modelObjectMap.get("name");
                profileDataManager.updateModelStatus(modelAlias,modelStatus);
                HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
                for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext();){
                    String moduleAlias = (String)w1.next();
                    HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleAlias);
                    String moduleStatus = (String)moduleObjectMap.get("status");
                    String moduleName = (String)moduleObjectMap.get("name");
                    profileDataManager.updateModuleStatus(moduleAlias,moduleStatus);
                    HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
                    for(Iterator w2=subModuleMap.keySet().iterator();w2.hasNext();){
                        String subModuleAlias = (String)w2.next();
                        HashMap subModuleObjectMap = (HashMap)subModuleMap.get(subModuleAlias);
                        String subModuleStatus = (String)subModuleObjectMap.get("status");
                        String subModuleName = (String)subModuleObjectMap.get("name");
                        profileDataManager.updateSubModuleStatus(subModuleAlias,subModuleStatus);
                        HashMap actionMap = (HashMap)subModuleObjectMap.get("actionMap");
                        for(Iterator w3=actionMap.keySet().iterator();w3.hasNext();){
                            String actionAlias = (String)w3.next();
                            HashMap actionObjectMap = (HashMap)actionMap.get(actionAlias);
                            String actionStatus = (String)actionObjectMap.get("status");
                            String actionName = (String)actionObjectMap.get("name");
                            profileDataManager.updateActionStatus(actionAlias,actionStatus);
                        }
                    }
                }
            }
            
        }catch(InvalidUploadedProfileException exp){
            throw exp;
        }catch(Exception exp){
            Logger.logTrace(MODULE, exp);
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   xmlData
     * @param   rootNode
     * @param   configurationProfileData
     * @param   currentProfile
     * @throws  DataManagerException
     * @purpose This method is generated to upload the file.
     */
    public void uploadProfileStatus(byte[] xmlData,String rootNode,IConfigurationProfileData configurationProfileData,String currentProfile) throws DataManagerException, InvalidUploadedProfileException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        
        try{
            session.beginTransaction();
            updateProfileStatus(xmlData,rootNode,profileDataManager,configurationProfileData);
            profileDataManager.updateFreezeConfigurationProfile(configurationProfileData.getConfigurationProfileId(),xmlData);
            session.commit();
        }catch(InvalidUploadedProfileException exp){
            session.rollback();
            throw new InvalidUploadedProfileException();	
        }catch(DataManagerException exp){
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());	
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   currentProfileAlias
     * @param   lastProfileAlias
     * @param   selectedProfile
     * @throws  DataManagerException
     * @purpose This method is generated to swap the profile with the value of the selected profile.
     */
    public void swapProfile(String currentProfileAlias,String lastProfileAlias,String selectedProfile) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        SystemParameterBLManager systemParameterBLManager = new SystemParameterBLManager();
        SystemParameterDataManager systemParameterDataManager = systemParameterBLManager.getSystemParameterDataManager(session);
        
        try {
            session.beginTransaction();
            ISystemParameterData spDataCurrent=systemParameterDataManager.getSystemParameter(currentProfileAlias);
            String currentProfile = spDataCurrent.getValue();
            
            profileDataManager.updateParameter(currentProfileAlias,lastProfileAlias);
            profileDataManager.updateParameter(selectedProfile,currentProfileAlias);
            
            updateProfileSpecificData(profileDataManager,currentProfile,selectedProfile);
            session.commit();
        } catch (Exception exp) {
            session.rollback();
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
        }finally{
        	session.close();
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   profileDataManager
     * @param   currentProfile
     * @param   selectedProfile
     * @throws  DataManagerException
     * @purpose This method is generated to update the ProfileSpecificData.
     */
    public void updateProfileSpecificData(ProfileDataManager profileDataManager,String currentProfile,String selectedProfile) throws DataManagerException{
        try {
            IConfigurationProfileData configurationProfileData = new ConfigurationProfileData();
            List lstConfigurationProfileList = profileDataManager.getConfigurationProfileData(currentProfile);
            
            if(lstConfigurationProfileList != null && lstConfigurationProfileList.size() >=1){
                configurationProfileData = (IConfigurationProfileData)lstConfigurationProfileList.get(0);
            }else{
                configurationProfileData = null;
            }
            profileDataManager.executeProfileScripts(configurationProfileData.getConfigurationProfileId(),"0");
            
            List lstConfigurationProfileSelectList = profileDataManager.getConfigurationProfileData(selectedProfile);
            if(lstConfigurationProfileSelectList != null && lstConfigurationProfileSelectList.size() >=1){
                configurationProfileData = (IConfigurationProfileData)lstConfigurationProfileSelectList.get(0);
            }else{
                configurationProfileData = null;
            }
            profileDataManager.executeProfileScripts(configurationProfileData.getConfigurationProfileId(),"1");
            
        } catch (Exception exp) {
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName(),exp);
        }
    }
    
    /**
     * @author  dhavalraval
     * @param   profileDataManager
     * @param   currentProfile
     * @param   lastProfileAlias
     * @throws  DataManagerException
     * @purpose This method is generated to upldate the profile after profileverification
     */
    public void updateLastProfile(ProfileDataManager profileDataManager, String currentProfile, String lastProfileAlias) throws DataManagerException{
        try {
            profileDataManager.updateLastProfile(currentProfile, lastProfileAlias);
        } catch (DataManagerException exp) {
            throw new DataManagerException("Data Manager implementation not found for "+getClass().getName(),exp);
        }
    }
    public List<IActionData> getActionData(String[] actionIds) throws DataManagerException{
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        ProfileDataManager profileDataManager = getProfileDataManager(session);
        List lstAction;
         try{  
           lstAction = profileDataManager.getActionList(actionIds);
         }finally{
        	 session.close();
         }
        return lstAction;
    }
     
}