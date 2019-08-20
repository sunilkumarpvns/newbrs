package com.elitecore.netvertexsm.hibernate.RoutingTable.mccmncgroup;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.MCCMNCGroupDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HMCCMNCGroupDataManager  extends HBaseDataManager implements MCCMNCGroupDataManager{
	
	
	@Override
	public PageList search(MCCMNCGroupData mccmncGroupData, int requiredPageNo, Integer pageSize)
			throws DataManagerException {
		PageList pageList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCGroupData.class).addOrder(Order.asc("name"));
			if(mccmncGroupData.getName() != null && mccmncGroupData.getName().length() > 0){
				criteria.add(Restrictions.ilike("name", mccmncGroupData.getName(),MatchMode.ANYWHERE));
			}
			if(mccmncGroupData.getBrandId()>0){
				criteria.add(Restrictions.eq("brandId", mccmncGroupData.getBrandId()));
			}int totalItems = criteria.list().size();
			criteria.setFirstResult(((requiredPageNo - 1) * pageSize));
			if(pageSize > 0){
				criteria.setMaxResults(pageSize);
			}
			List groupDataList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0){
				totalPages -= 1;
			}
			pageList = new PageList(groupDataList, requiredPageNo, totalPages, totalItems);
		}catch(HibernateException hExe){
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void create(MCCMNCGroupData mccmncGroupData)
			throws DataManagerException {
			try{
				Session session = getSession();
				session.save(mccmncGroupData);
				session.flush();
				List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = mccmncGroupData.getMccmncCodeGroupRelDataList();
				if(mccmncCodeGroupRelDataList != null && !mccmncCodeGroupRelDataList.isEmpty()) {
					for(MCCMNCCodeGroupRelData mccmncCodeGroupRelData : mccmncCodeGroupRelDataList) {
						mccmncCodeGroupRelData.setMccmncGroupId(mccmncGroupData.getMccmncGroupId());
						session.save(mccmncCodeGroupRelData);
					}
				}
			}catch(ConstraintViolationException hExp){
		        throw hExp;
		    }catch(HibernateException hExp){
		        throw new DataManagerException(hExp.getMessage(), hExp);
		    }catch(Exception exp){
		      	throw new DataManagerException(exp.getMessage(),exp);
		    }
		}

	@Override
	public List<MCCMNCGroupData> getmccmncGroupDataList() throws DataManagerException{
	List<MCCMNCGroupData> mccmncGroupDataList=null;
	try{
		Session session = getSession();
		Criteria criteria = session.createCriteria(MCCMNCGroupData.class);		
		mccmncGroupDataList = criteria.list();
	}catch(HibernateException hExp){
		throw new DataManagerException(hExp.getMessage(), hExp);
	}catch(Exception exp){
		throw new DataManagerException(exp.getMessage(), exp);
	}
	return mccmncGroupDataList;
}

	@Override
	public void delete(Long[] mccmncGroupIDS) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(MCCMNCGroupData.class).add(Restrictions.in("mccmncGroupId", mccmncGroupIDS)).list();
			deleteObjectList(list,session);
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public MCCMNCGroupData getMCCMNCGroupData(long mccmncGroupId) throws DataManagerException {
		MCCMNCGroupData mccmncGroupData = null;
		try{
			Session session = getSession();
			List list = session.createCriteria(MCCMNCGroupData.class).add(Restrictions.eq("mccmncGroupId", mccmncGroupId)).list();
			if(list != null && !list.isEmpty()){
				mccmncGroupData = (MCCMNCGroupData) list.get(0);
			}
			return mccmncGroupData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void update(MCCMNCGroupData mccmncGroupData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCGroupData.class);
			deleteMCCMNCCodeGroupOnRelData(mccmncGroupData.getMccmncGroupId());
			List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = mccmncGroupData.getMccmncCodeGroupRelDataList();
			if(mccmncCodeGroupRelDataList != null && !mccmncCodeGroupRelDataList.isEmpty()){
				for (MCCMNCCodeGroupRelData mccmncCodeGroupRelData : mccmncCodeGroupRelDataList){
					session.save(mccmncCodeGroupRelData);
				}
			}
			criteria.add(Restrictions.eq("mccmncGroupId", mccmncGroupData.getMccmncGroupId()));
			MCCMNCGroupData data = (MCCMNCGroupData) criteria.uniqueResult();
			data.setMccmncGroupId(mccmncGroupData.getMccmncGroupId());
			data.setName(mccmncGroupData.getName());
			data.setBrandId(mccmncGroupData.getBrandId());
			data.setDescription(mccmncGroupData.getDescription());
			setUpdateAuditDetail(data);
			session.update(data);
		}catch(ConstraintViolationException hExp){
			throw hExp;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<MCCMNCCodeGroupRelData> getMCCMNCodeGroupRelDataList(Long mccmncGroupID)
			throws DataManagerException {
		try{
			List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCCodeGroupRelData.class);
			mccmncCodeGroupRelDataList = criteria.add(Restrictions.eq("mccmncGroupId", mccmncGroupID)).list();
			return mccmncCodeGroupRelDataList;

		}catch(ConstraintViolationException hExp){
			throw hExp;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public void deleteMCCMNCCodeGroupOnRelData(Long mccmncGroupId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCCodeGroupRelData.class);
			criteria.add(Restrictions.eq("mccmncGroupId", mccmncGroupId));
			List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = criteria.list();

			if(mccmncCodeGroupRelDataList != null && mccmncCodeGroupRelDataList.size() > 0){
				for (MCCMNCCodeGroupRelData mccmncCodeGroupRelData : mccmncCodeGroupRelDataList){
					session.delete(mccmncCodeGroupRelData);
					session.flush();
				}
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<MCCMNCCodeGroupRelData> getMCCMNCodeGroupRelDataList() throws DataManagerException {
		List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCCodeGroupRelData.class);
			mccmncCodeGroupRelDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return mccmncCodeGroupRelDataList;
	}

	@Override
	public List<MCCMNCGroupData> getmccmncGroupDataList(String restrictionSql)
			throws DataManagerException {
		List<MCCMNCGroupData> mccmncGroupDataList=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MCCMNCGroupData.class).add(Restrictions.sqlRestriction(restrictionSql));		
			mccmncGroupDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return mccmncGroupDataList;
	}
	
	@Override
	public MCCMNCGroupData getMCCMNCGroupByNetwork(MCCMNCCodeGroupRelData mccmncCodeGroupRelData) throws DataManagerException{
		MCCMNCGroupData mccmncGroup=null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(MCCMNCCodeGroupRelData.class).add(Restrictions.eq("mccMNCID", mccmncCodeGroupRelData.getMccMNCID()));
			MCCMNCCodeGroupRelData  tempmccmncCodeGroupRelData=(MCCMNCCodeGroupRelData)criteria.uniqueResult();
			if(tempmccmncCodeGroupRelData!=null){
				mccmncGroup=getMCCMNCGroupData(tempmccmncCodeGroupRelData.getMccmncGroupId());
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		
		return mccmncGroup;
	}
}
	


