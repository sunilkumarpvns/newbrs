package com.elitecore.netvertexsm.hibernate.RoutingTable.mccmncroutingTable;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;

import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.MCCMNCRoutingTableDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HMCCMNCRoutingTableDataManager extends HBaseDataManager implements MCCMNCRoutingTableDataManager {

	@Override
	public PageList search(RoutingTableData routingTableData, int requiredPageNo, Integer pageSize)
			throws DataManagerException {
		PageList pageList = null;
		try{
			if(routingTableData!=null){
				Session session = getSession();
				Criteria criteria = session.createCriteria(RoutingTableData.class).addOrder(Order.asc("orderNumber"));
				if(routingTableData.getName() != null && routingTableData.getName().length() > 0){
					criteria.add(Restrictions.ilike("name", routingTableData.getName(),MatchMode.ANYWHERE));
				}
				int totalItems = criteria.list().size();
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
			}
		}catch(HibernateException hExe){
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;

	}

	@Override
	public void create(RoutingTableData routingTableData) throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoutingTableData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<RoutingTableData> list= criteria.list();
			if(list!=null&&!list.isEmpty()){
				     RoutingTableData routingEntry=list.get(0);
				     routingTableData.setOrderNumber(routingEntry.getOrderNumber()+1);
			}
			session.save(routingTableData);
			session.flush();
			List<RoutingTableGatewayRelData> RoutingTableGatewayRelDataList = routingTableData.getRoutingTableGatewayRelData();
			if(RoutingTableGatewayRelDataList != null && !RoutingTableGatewayRelDataList.isEmpty()) {
				for(RoutingTableGatewayRelData RoutingTableGatewayRelData : RoutingTableGatewayRelDataList) {
					RoutingTableGatewayRelData.setRoutingTableId(routingTableData.getRoutingTableId());
					session.save(RoutingTableGatewayRelData);
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
	public List<RoutingTableData> getRoutingTableDataList() throws DataManagerException {
		List<RoutingTableData> routingEntryList=null;
			try{
				Session session = getSession();
				Criteria criteria = session.createCriteria(RoutingTableData.class).addOrder(Order.asc("orderNumber"));
				routingEntryList = criteria.list();
				return routingEntryList;
			}catch(HibernateException hExp){
		       	hExp.printStackTrace();
		        throw new DataManagerException(hExp.getMessage(), hExp);
		    }catch(Exception exp){
		      	exp.printStackTrace();
		      	throw new DataManagerException(exp.getMessage(),exp);
		    }
	}

	@Override
	public void delete(Long[] routingTableIds) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(RoutingTableData.class).add(Restrictions.in("routingTableId", routingTableIds)).list();
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
	public RoutingTableData getRoutingTableData(long routingTableId) throws DataManagerException {
		RoutingTableData routingData = null;
		try{
			Session session = getSession();
			List routingDatalist = session.createCriteria(RoutingTableData.class)
					.add(Restrictions.eq("routingTableId", routingTableId)).list();
			if(routingDatalist != null && !routingDatalist.isEmpty()){
				routingData = (RoutingTableData) routingDatalist.get(0);
			}

			return routingData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void update(RoutingTableData routingTableData) throws DataManagerException {
		try{
		Session session = getSession();
		Criteria criteria = session.createCriteria(RoutingTableData.class);
		deleteRoutingTableGatewayRelData(routingTableData.getRoutingTableId());
		if(!(routingTableData.getRoutingAction()==0)){
		List<RoutingTableGatewayRelData> routingGatewayRelList = routingTableData.getRoutingTableGatewayRelData();
		if(routingGatewayRelList != null && !routingGatewayRelList.isEmpty()){
			for (RoutingTableGatewayRelData routingGatewayRel : routingGatewayRelList){
				session.save(routingGatewayRel);
			}
		}
		}
		criteria.add(Restrictions.eq("routingTableId",routingTableData.getRoutingTableId()));
		RoutingTableData data = (RoutingTableData) criteria.uniqueResult();
		data.setName(routingTableData.getName());
		data.setRoutingTableId(routingTableData.getRoutingTableId());
		data.setMccmncGroupId(routingTableData.getMccmncGroupId());
		data.setType(routingTableData.getType());
		data.setRoutingAction(routingTableData.getRoutingAction());
		data.setRealmCondition(routingTableData.getRealmCondition());
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
	public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelList(Long routingTableId)
			throws DataManagerException {
		List<RoutingTableGatewayRelData> routingGatewayRelList=null;
		try{
			 Session session =getSession();
			 routingGatewayRelList=session.createCriteria(RoutingTableGatewayRelData.class).add(Restrictions.eq("routingTableId", routingTableId)).list();
	 		  return  routingGatewayRelList;
			}catch(HibernateException hExp){ 
			hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }

	}

	@Override
	public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelList()
			throws DataManagerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRoutingTableGatewayRelData(Long routingTableId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RoutingTableGatewayRelData.class);
			criteria.add(Restrictions.eq("routingTableId", routingTableId));
			List<RoutingTableGatewayRelData> routingGatewayRelDataList = criteria.list();
			if(routingGatewayRelDataList != null && routingGatewayRelDataList.size() > 0){
				for (RoutingTableGatewayRelData mccmncCodeGroupRelData : routingGatewayRelDataList){
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
	public void changeRoutingEntryOrder(String[] routingEntryIds) throws DataManagerException {
		try{
			Session session=getSession();
			if(routingEntryIds!=null&&routingEntryIds.length>0){
				for(int i=0;i<routingEntryIds.length;i++){
					RoutingTableData routingEntry=getRoutingTableData(Integer.parseInt(routingEntryIds[i]));
					routingEntry.setOrderNumber(i+1);
					session.update(routingEntry);
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
	public RoutingTableData getRoutingTableByMCCMNCGroup(long mccmncGroupId)throws DataManagerException{
		RoutingTableData routingData = null;
		try{
			Session session = getSession();
			List routingDatalist = session.createCriteria(RoutingTableData.class).add(Restrictions.eq("mccmncGroupId", mccmncGroupId)).list();
			if(routingDatalist != null && !routingDatalist.isEmpty()){
				routingData = (RoutingTableData) routingDatalist.get(0);
			}
			return routingData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	
}
