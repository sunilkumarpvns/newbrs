package com.elitecore.netvertexsm.blmanager.customizedmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuDataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class CustomizedMenuBLManager extends BaseBLManager{
	
	private CustomizedMenuDataManager getCustomizedMenuDataManager(IDataManagerSession session) {
		CustomizedMenuDataManager customizedMenuDataManager = (CustomizedMenuDataManager)DataManagerFactory.getInstance().getDataManager(CustomizedMenuDataManager.class, session);
        return customizedMenuDataManager;
	}
	
	public void create(CustomizedMenuData customizedMenuData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (customizedMenuDataManager == null || systemAuditDataManager == null)
           throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	customizedMenuDataManager.create(customizedMenuData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Customized Menu Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
    }

	
	public void update(CustomizedMenuData customizedMenuData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if(customizedMenuDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	customizedMenuDataManager.update(customizedMenuData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Customized Menu Detail. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
    }
	
	public void delete(Long[] customizedMenuIDs,List<CustomizedMenuData> menuList,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (customizedMenuDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	customizedMenuDataManager.delete(customizedMenuIDs,menuList);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DataManagerException exp){
        	session.rollback();
        	if(exp.getMessage().equals("PARENT_IS_NOT_DELETABLE")){
        		throw new DataManagerException("PARENT_IS_NOT_DELETABLE");
        	}else{
        		throw new DataManagerException("Action failed : "+exp.getMessage());
        	}
        }finally{
			session.close();
		}
        
    }
	public PageList search(CustomizedMenuData customizedMenuData,int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        PageList pageList = null;
        if(customizedMenuDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	pageList = customizedMenuDataManager.search(customizedMenuData,pageNo,pageSize);  
        	return pageList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	public CustomizedMenuData getCustomizedMenuData(String title,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        CustomizedMenuData customizedMenuData = null;
        if(customizedMenuDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	customizedMenuData = customizedMenuDataManager.getCustomizedMenuDetailData(title);
        	return customizedMenuData;
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	
	public CustomizedMenuData getCustomizedMenuDetailData(String title) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        CustomizedMenuData customizedMenuData = null;
        if (customizedMenuDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	customizedMenuData = customizedMenuDataManager.getCustomizedMenuDetailData(title);
        	return customizedMenuData;
        }catch(DataManagerException exp){
        	session.close();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	public List<CustomizedMenuData> getCustomizeMenuList() throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
        List<CustomizedMenuData> titleDataList = null;
        if(customizedMenuDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	titleDataList = customizedMenuDataManager.getCustomizeMenuList();
        	return titleDataList;
        }catch(DataManagerException exp){
        	session.close();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}
	

	public List<CustomizedMenuData> getCustomizedMenuList() throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
         List<CustomizedMenuData> parentIDDataList = null;
        if(customizedMenuDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	parentIDDataList = customizedMenuDataManager.getCustomizedMenuList();
        	return parentIDDataList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
	}

	public List<CustomizedMenuData> getCustomizedMenuList(String title , Long id) throws DataManagerException {
	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
         List<CustomizedMenuData> parentIDDataList = null;
        if(customizedMenuDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	parentIDDataList = customizedMenuDataManager.getCustomizedMenuList(title ,id);
        	return parentIDDataList;
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
			session.close();
		}
	}
	
	public List<MenuItem> getCustomizedMenuItems() throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		CustomizedMenuDataManager customizedMenuDataManager = getCustomizedMenuDataManager(session);
   
        if(customizedMenuDataManager == null)
           throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{
        	return createMenuItem(0L, customizedMenuDataManager);
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
	}
	private List<MenuItem> createMenuItem(Long parentId , CustomizedMenuDataManager customizedMenuDataManager ) throws DataManagerException{
		List<CustomizedMenuData> customizedMenuItemList = null;
        ContainerMenu containerMenu = null;
        NonContainerMenu nonContainerMenu = null;
        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        String method = null;
        OrderComparator orderComparator = new OrderComparator();
		customizedMenuItemList = customizedMenuDataManager.getCustomizedMenuItem(parentId);
		
    	for(CustomizedMenuData customizedMenuData : customizedMenuItemList){
    		if(customizedMenuData.getIsContainer().equalsIgnoreCase(MenuType.CONTAINER.type)){
    			containerMenu = new ContainerMenu();
    			containerMenu.setId(customizedMenuData.getCustomizedMenuId());
    			containerMenu.setTitle(customizedMenuData.getTitle());
    			containerMenu.setOrder(customizedMenuData.getOrder());
    			containerMenu.setMenuItemsList(createMenuItem(customizedMenuData.getCustomizedMenuId(), customizedMenuDataManager));
    			menuItems.add(containerMenu);
    		}else{
    			nonContainerMenu = new NonContainerMenu();
    			nonContainerMenu.setId(customizedMenuData.getCustomizedMenuId());
    			nonContainerMenu.setTitle(customizedMenuData.getTitle());
    			nonContainerMenu.setUrl(customizedMenuData.getUrl());
    			nonContainerMenu.setUrlParameters(customizedMenuData.getParameters());
    			nonContainerMenu.setOrder(customizedMenuData.getOrder());
    			method = customizedMenuData.getOpenMethod();
    			if(method.equalsIgnoreCase(OpenMethod.SAME_WINDOW.methodType)){
    				nonContainerMenu.setOpenMethod(OpenMethod.SAME_WINDOW);
    			}else{
    				nonContainerMenu.setOpenMethod(OpenMethod.NEW_WINDOW);
    			}
    			menuItems.add(nonContainerMenu);
    		}
    	}
    	Collections.sort(menuItems, orderComparator);
		return menuItems;
		
	}
}