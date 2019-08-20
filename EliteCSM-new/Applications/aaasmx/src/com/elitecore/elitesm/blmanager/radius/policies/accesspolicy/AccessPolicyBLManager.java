package com.elitecore.elitesm.blmanager.radius.policies.accesspolicy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.AccessPolicyDataManager;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IWeekDayData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.exception.DuplicateAccessPolicyNameException;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class AccessPolicyBLManager extends BaseBLManager {
    
    private static final String EMPTY = "";
	private static final String MODULE = "Access Policy";
   
    public List getWeekDay( IWeekDayData weekDayData ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        List lstWeekDayList;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
        	 lstWeekDayList = accessPolicyDataManager.getWeekDay(weekDayData);
             return lstWeekDayList;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
    public void updateStatus( List<String> lstAccessPolicyIds , String commonStatusId) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        
        Date currentDate = new Date();
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
            updateStatusValidate(lstAccessPolicyIds, commonStatusId);
            session.beginTransaction();
            
            if (lstAccessPolicyIds != null) {
                for ( int i = 0; i < lstAccessPolicyIds.size(); i++ ) {
                    if (lstAccessPolicyIds.get(i) != null) {
                    	String accessPolicyId = lstAccessPolicyIds.get(i);
                    	accessPolicyDataManager.updateStatus(accessPolicyId, commonStatusId, new Timestamp(currentDate.getTime()));
                    }
                }
                commit(session);
                
                
            } else {
            	rollbackSession(session);
                throw new DataManagerException("Data Manager List of Ids are null" + getClass().getName());
            }
            
        } catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}    
    }
    
    public List getAccessPolicyList( ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        List lstAccessPolicyList;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
        	 lstAccessPolicyList = accessPolicyDataManager.getList();
             return lstAccessPolicyList;
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
    
    public List getList( IAccessPolicyData accessPolicyData ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        List lstList;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
            lstList = accessPolicyDataManager.getList(accessPolicyData);
            
        } catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}  
        return lstList;
    }
    
    public List getStartWeekDayList( ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        List lstParameterValue;
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
            lstParameterValue = accessPolicyDataManager.getStartWeekDayList();
            return lstParameterValue;
        } catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
    }
    
    /**
     * @return Returns Data Manager instance for dictionary data.
     */
    public AccessPolicyDataManager getAccessPolicyDataManager( IDataManagerSession session ) {
        AccessPolicyDataManager accessPolicyDataManager = (AccessPolicyDataManager) DataManagerFactory.getInstance().getDataManager(AccessPolicyDataManager.class, session);
        return accessPolicyDataManager;
    }
    
    public void updateStatusValidate( List lstAccessPolicyIds , String commonStatusId ) throws DataValidationException {
        // CommonStatusId
        if (EliteGenericValidator.isBlankOrNull(commonStatusId)) {
            throw (new DataValidationException("Invalid AccessPolicy commonStatusId", (MODULE + " " + "commonStatusId")));
        }
        
    }
    
    public void verifyRadiusPolicyName( String policyName ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        boolean isPolicyName = false;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
            isPolicyName = accessPolicyDataManager.verifyAccessPolicyName(policyName);
        } catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        if (isPolicyName) {
            throw new DuplicateAccessPolicyNameException("Duplicate Access Policy Name found");
        }
    }
    
    public void verifyRadiusPolicyName( String accessPolicyId , String policyName ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        boolean isPolicyName = false;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try { 
            isPolicyName = accessPolicyDataManager.verifyAccessPolicyName(accessPolicyId, policyName);
            
        } catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}  
        
        if (isPolicyName) {
            throw new DuplicateAccessPolicyNameException("Duplicate Access Policy Name found");
        }
    }
    public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }

    
	/**
	 * @param accessPolicyData
	 * @param staffData
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws DataManagerException
	 */
    
	public PageList search(IAccessPolicyData accessPolicyData, IStaffData staffData, int pageNo, int pageSize) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);
        
        PageList lstAccessPolicyList;
        
        if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        
        try {
        	
        	session.beginTransaction();
            
        	lstAccessPolicyList = accessPolicyDataManager.search(accessPolicyData, pageNo, pageSize);
        	
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_ACCESS_POLICY_ACTION);
            
        	commit(session);
            
            return lstAccessPolicyList;
            
        } catch (DataManagerException dme) {
        	rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
        
    }
    
    
    public IAccessPolicyData getAccessPolicyById(String accessPolicyId) throws DataManagerException {
		return getAccessPolicy(accessPolicyId, BY_ID, false);
	}
    
	public IAccessPolicyData getAccessPolicyByName(String accessPolicyName, boolean caseSensitivity) throws DataManagerException {
		return getAccessPolicy(accessPolicyName.trim(), BY_NAME, caseSensitivity);
	}
	
	private IAccessPolicyData getAccessPolicy(Object accessPolicyIdOrName, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);

		if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {

			IAccessPolicyData accessPolicy = null;

			if (isIdOrName) {
				accessPolicy = accessPolicyDataManager.getAccessPolicyById((String) accessPolicyIdOrName);
			} else {
				if(caseSensitivity){
					accessPolicy = (AccessPolicyData) verifyNameWithIgnoreCase(AccessPolicyData.class, (String) accessPolicyIdOrName, true);
				} else {
					accessPolicy = accessPolicyDataManager.getAccessPolicyByName((String) accessPolicyIdOrName);
				}
			}

			return accessPolicy;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
	/**
	 * @param accessPolicy
	 * @param staffData
	 * @throws DataManagerException
	 */
	
	public void createAccessPolicy(AccessPolicyData accessPolicy, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
	
		List<AccessPolicyData> accessPolicies = new ArrayList<AccessPolicyData>();
		accessPolicies.add(accessPolicy);
		createAccessPolicy(accessPolicies, staffData, EMPTY,caseSensitivity);
		
	}
	
	public Map<String, List<Status>> createAccessPolicy(List<AccessPolicyData> accessPolicies, IStaffData staffData, String partialSuccess, boolean caseSensitivity) throws DataManagerException {

		if (Collectionz.isNullOrEmpty(accessPolicies) == false) {

			for (IAccessPolicyData accessPolicy : accessPolicies) {
				
				if(caseSensitivity){
					verifyNameWithIgnoreCase(AccessPolicyData.class, accessPolicy.getName(), false);
				}

				accessPolicy.setAssigned("N");
				accessPolicy.setLastUpdated(new Timestamp(new Date().getTime()));
				accessPolicy.setSystemGenerated("N");
				accessPolicy.setStatusChangeDate(null);

				if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {

					List<AccessPolicyDetailData> accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();

					String accessStatus;

					if(accessPolicy.getAccessStatus().equals(AccessPolicyConstant.ALLOWED_VALUE)){
						accessStatus = AccessPolicyConstant.DENIED_VALUE;
					}else{
						accessStatus = AccessPolicyConstant.ALLOWED_VALUE;
					}
					int size = accessPolicyDetails.size();
					for ( int i = 0; i < size; i++ ) {

						AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) accessPolicyDetails.get(i);

						accessPolicyDetailData.setSerialNumber(i + 1);

						accessPolicyDetailData.setAccessStatus(accessStatus);
					}
				}
			}
		}
		return insertRecords(AccessPolicyDataManager.class, accessPolicies, staffData, ConfigConstant.CREATE_ACCESS_POLICY_ACTION, partialSuccess);
	}
	
	
	public void updateAccessPolicyById(IAccessPolicyData accessPolicy, IStaffData staffData) throws DataManagerException {
		
		updateAccessPolicyByName(accessPolicy, staffData, null, false);
		
	}
	
	public void updateAccessPolicyByName(IAccessPolicyData accessPolicy, IStaffData staffData, String accessPolicyName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);

		if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			accessPolicy.setAssigned("N");
            accessPolicy.setLastUpdated(new Timestamp(new Date().getTime()));
            accessPolicy.setSystemGenerated("N");
            accessPolicy.setStatusChangeDate(null);
			
			if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
				
				List<AccessPolicyDetailData> accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();

				String accessStatus;
				
				if(accessPolicy.getAccessStatus().equals(AccessPolicyConstant.ALLOWED_VALUE)){
                	
					accessStatus = AccessPolicyConstant.DENIED_VALUE;
                	
                }else{
                	
					accessStatus = AccessPolicyConstant.ALLOWED_VALUE;
                	
                }
				
				int size = accessPolicyDetails.size();
				for ( int i = 0; i < size; i++ ) {
					
                    AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) accessPolicyDetails.get(i);
                    
                    accessPolicyDetailData.setSerialNumber(i + 1);

                    accessPolicyDetailData.setAccessStatus(accessStatus);
                    
                }
				
			}

			if (accessPolicyName == null) {
				accessPolicyDataManager.updateAccessPolicyById(accessPolicy, accessPolicy.getAccessPolicyId());
			} else {
				if(caseSensitivity){
					AccessPolicyData accessPolicyDataIgnoreCaseData = (AccessPolicyData) verifyNameWithIgnoreCase(AccessPolicyData.class, (String) accessPolicyName, true);
					accessPolicyDataManager.updateAccessPolicyById(accessPolicy, accessPolicyDataIgnoreCaseData.getAccessPolicyId());
				}else {
					accessPolicyDataManager.updateAccessPolicyByName(accessPolicy, accessPolicyName.trim());
				}
			}

			staffData.setAuditName(accessPolicy.getName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_ACCESS_POLICY_ACTION);
			
			commit(session);
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
	public void deleteAccessPolicyById(List<String> accessPolicyIds, IStaffData staffData) throws DataManagerException {
		
		deleteAccessPolicy(accessPolicyIds, staffData, BY_ID, false);
		
	}
	
	public void deleteAccessPolicyByName(List<String> accessPolicyNames, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		
		deleteAccessPolicy(accessPolicyNames, staffData, BY_NAME, caseSensitivity);
		
	}

	private void deleteAccessPolicy(List<String> accessPolicyIdOrNames, IStaffData staffData, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AccessPolicyDataManager accessPolicyDataManager = getAccessPolicyDataManager(session);

		if (accessPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(accessPolicyIdOrNames) == false) {

				int size = accessPolicyIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(accessPolicyIdOrNames.get(i)) == false) {

						String accessPolicyIdOrName = accessPolicyIdOrNames.get(i).trim();
						
						String accessPolicyName = null;

						if (isIdOrName) {
							accessPolicyName = accessPolicyDataManager.deleteAccessPolicyById(accessPolicyIdOrName);
						} else {
							if(caseSensitivity){
								AccessPolicyData accessPolicyDataIgnoreCaseData = (AccessPolicyData) verifyNameWithIgnoreCase(AccessPolicyData.class, (String) accessPolicyIdOrName, true);
								accessPolicyName  = accessPolicyDataManager.deleteAccessPolicyById(accessPolicyDataIgnoreCaseData.getAccessPolicyId());
							} else {
								accessPolicyName = accessPolicyDataManager.deleteAccessPolicyByName(accessPolicyIdOrName);
							}
						}
						staffData.setAuditName(accessPolicyName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_ACCESS_POLICY_ACTION);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
    
    
}
