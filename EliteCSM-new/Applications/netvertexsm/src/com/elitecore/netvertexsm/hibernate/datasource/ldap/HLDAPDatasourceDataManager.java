package com.elitecore.netvertexsm.hibernate.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ReferenceFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.LDAPDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HLDAPDatasourceDataManager extends HBaseDataManager implements LDAPDSDataManager{

	public void create(ILDAPDatasourceData ldapDatasourceData) throws DataManagerException,DuplicateParameterFoundExcpetion {
		
		List searchList = getLDAPDatasourceDataByName(ldapDatasourceData.getName());
		
		if(searchList != null && searchList.size() > 0){
			throw new DuplicateParameterFoundExcpetion("Duplicate Name used");
			
		}		
		Session session = getSession();		
		session.save(ldapDatasourceData);
		session.flush();
		
		List baseDnDetailList = ldapDatasourceData.getSearchBaseDnList();
		
		if(baseDnDetailList != null && baseDnDetailList.size() >0){
			for(int i=0;i<baseDnDetailList.size();i++){
				try{
					ILDAPBaseDnDetailData ldapBaseDnDetail = (ILDAPBaseDnDetailData)baseDnDetailList.get(i);
					ldapBaseDnDetail.setLdapDsId(ldapDatasourceData.getLdapDsId());
					session.save(ldapBaseDnDetail);
				}catch(HibernateException he){
					throw new DataManagerException(he.getMessage(), he);
				}catch(Exception e){
					throw new DataManagerException(e.getMessage(), e);
				}
			}
		}else{
			throw new DataManagerException("No Search BaseDn List found.");
		}
	}

	public List getLDAPDatasourceDataByName(String name) {
		List LDAPDatasourceSearcList = new ArrayList();
		Session session = getSession();
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		LDAPDatasourceSearcList = (List<ILDAPDatasourceData>)criteria.add(Restrictions.eq("name",name)).list();
		return LDAPDatasourceSearcList;
		
	}

	public List search(ILDAPDatasourceData ldapDatasourceData) {
		
		List<ILDAPDatasourceData> listOfSearchData = new ArrayList<ILDAPDatasourceData>();
		Session session = getSession();
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		
		if(Strings.isNullOrBlank(ldapDatasourceData.getName())){
			listOfSearchData = getLDAPDSList();
		}else{
			listOfSearchData = criteria.add(Restrictions.ilike("name", ldapDatasourceData.getName().trim(), MatchMode.ANYWHERE)).list();				
		}
		return listOfSearchData;
	}
	
	public List getLDAPDSList(){
		
		List<ILDAPDatasourceData> listOfLDAP = new ArrayList<ILDAPDatasourceData>();
		
		Session session = getSession();
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		listOfLDAP = criteria.list();	
		
		return listOfLDAP;
		
	}

	public LDAPDatasourceData getLdapData(long ldapDsId) throws DataManagerException {		
		Session session = getSession();	
		try{
				Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
				List<LDAPDatasourceData> tempList = criteria.add(Restrictions.eq("ldapDsId", ldapDsId)).list();		   
				LDAPDatasourceData tempLdapDatasourceData = (LDAPDatasourceData)tempList.get(0);
				return tempLdapDatasourceData;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
			
		
	
	}

	public List getLdapBaseDnDetailDataByLdapId(long ldapDsId) throws DataManagerException {
		
		try{
			Session session = getSession();		
			Criteria criteria = session.createCriteria(LDAPBaseDnDetailData.class);
			List<LDAPBaseDnDetailData>tempList = criteria.add(Restrictions.eq("ldapDsId", ldapDsId)).list();			
			return tempList;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		
	}

	public List getLDAPDetailsById(long LdapDsId){
		
		Session session = getSession();		
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		List ldapDetailsList = criteria.add(Restrictions.eq("ldapDsId", LdapDsId)).list();
		
		return ldapDetailsList;
		
	}
	public void update(ILDAPDatasourceData ldapDatasourceData , long ldapDsId) throws DataManagerException {
		
		Session session = getSession();		
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		LDAPDatasourceData tempLDAPDatasourceData = (LDAPDatasourceData)criteria.add(Restrictions.eq("ldapDsId", ldapDsId)).uniqueResult();
	
		try{
			
			String name = ldapDatasourceData.getName();			
			criteria = session.createCriteria(LDAPDatasourceData.class);
			List tempList = criteria.add(Restrictions.eq("name",name)).list();
			
			if(tempList != null && tempList.size() >0){
				for(int i=0;i<tempList.size();i++){
					if(ldapDsId != ((LDAPDatasourceData)tempList.get(i)).getLdapDsId()){
						throw new DuplicateParameterFoundExcpetion();
					}
				}
			}
			
			tempLDAPDatasourceData.setName(ldapDatasourceData.getName());
			tempLDAPDatasourceData.setAddress(ldapDatasourceData.getAddress());			
		
			tempLDAPDatasourceData.setTimeout(ldapDatasourceData.getTimeout());
			tempLDAPDatasourceData.setStatusCheckDuration(ldapDatasourceData.getStatusCheckDuration());
			tempLDAPDatasourceData.setLdapSizeLimit(ldapDatasourceData.getLdapSizeLimit());
			tempLDAPDatasourceData.setAdministrator(ldapDatasourceData.getAdministrator());
			tempLDAPDatasourceData.setPassword(ldapDatasourceData.getPassword());
		
			tempLDAPDatasourceData.setUserDNPrefix(ldapDatasourceData.getUserDNPrefix());
			tempLDAPDatasourceData.setMinimumPool(ldapDatasourceData.getMinimumPool());
			tempLDAPDatasourceData.setMaximumPool(ldapDatasourceData.getMaximumPool());
					
			session.update(tempLDAPDatasourceData);
			setUpdateAuditDetail(tempLDAPDatasourceData);
			session.flush();
			
			List<LDAPBaseDnDetailData> baseDnDetailList = ldapDatasourceData.getSearchBaseDnList();///users updated list ...
			
			criteria = session.createCriteria(LDAPBaseDnDetailData.class);
			List<LDAPBaseDnDetailData> actualBaseDnDetailList = criteria.add(Restrictions.eq("ldapDsId", ldapDsId)).list();
			if(actualBaseDnDetailList != null && actualBaseDnDetailList.size() > 0){
				for(int i=0;i<actualBaseDnDetailList.size();i++){
					LDAPBaseDnDetailData tempBaseDnDetailData = actualBaseDnDetailList.get(i);			
					session.delete(tempBaseDnDetailData);
				}			
			}

			if(baseDnDetailList != null && baseDnDetailList.size() >0){
				for(int i=0;i<baseDnDetailList.size();i++){
					
					ILDAPBaseDnDetailData ldapBaseDnDetail = (ILDAPBaseDnDetailData)baseDnDetailList.get(i);
					ldapBaseDnDetail.setLdapDsId(ldapDsId);				
					session.save(ldapBaseDnDetail);
					session.flush();
				}
			}
			
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}catch(DuplicateParameterFoundExcpetion dpe){
			throw new DuplicateParameterFoundExcpetion(dpe.getMessage(),dpe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		
		
	}

	public void delete(List ldapDsIds) throws DataManagerException {
		
		Session session = getSession();
		Criteria criteria ;
		long id=0;
		
		try{
			if(ldapDsIds !=null && ldapDsIds.size() >0){				
				for(int i=0;i<ldapDsIds.size();i++){
					id = Long.parseLong((ldapDsIds.get(i)).toString().trim());	
					
					criteria = session.createCriteria(LDAPSPInterfaceData.class);
					LDAPSPInterfaceData data = (LDAPSPInterfaceData)criteria.add(Restrictions.eq("ldapDsId",id)).uniqueResult();
					LDAPDatasourceData dsData = (LDAPDatasourceData)session.createCriteria(LDAPDatasourceData.class).add(Restrictions.eq("ldapDsId",id)).uniqueResult();
					if(data != null){
						throw new ReferenceFoundException("The " + dsData.getName() + " LAP datasource is already configured with  driver.",dsData.getName());
					}
					
					criteria = session.createCriteria(LDAPBaseDnDetailData.class);
					
					List<LDAPBaseDnDetailData> baseDetailList = criteria.add(Restrictions.eq("ldapDsId", id)).list();
					
					if(baseDetailList != null && baseDetailList.size() > 0) {
						
						for(int j=0 ; j<baseDetailList.size();j++){
							LDAPBaseDnDetailData baseDetailData = baseDetailList.get(j);
							session.delete(baseDetailData);
						}
					}
					
					List<LDAPDatasourceData> ldapDatasourceList = getLDAPDetailsById(id);
					session.delete(ldapDatasourceList.get(0));
					session.flush();
				}
				
			}			
		}catch(ReferenceFoundException rf){
			throw rf;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}				
	}
}
