package com.elitecore.netvertexsm.hibernate.CustomizedMenu;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuDataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HCustomizedMenuDataManager extends HBaseDataManager implements CustomizedMenuDataManager{
	private static final String MODULE=HCustomizedMenuDataManager.class.getSimpleName();
	
	public void create(CustomizedMenuData customizedMenuData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(customizedMenuData);
			session.flush();
		}catch(ConstraintViolationException hExp){
	        throw hExp;
	    }catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public void update(CustomizedMenuData customizedMenuData) throws DataManagerException {
		try{
			
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class);
			criteria.add(Restrictions.eq("customizedMenuId", customizedMenuData.getCustomizedMenuId()));
						
			CustomizedMenuData data = (CustomizedMenuData)criteria.uniqueResult();
			if(customizedMenuData.getIsContainer().equalsIgnoreCase("Yes"))
			{
				data.setURL("");
				data.setParameters("");
			}else{
				data.setURL(customizedMenuData.getURL());
				data.setParameters(customizedMenuData.getParameters());
			}
			data.setOpenMethod(customizedMenuData.getOpenMethod());
			data.setTitle(customizedMenuData.getTitle());
			data.setOpenMethod(customizedMenuData.getOpenMethod());
			data.setParentID(customizedMenuData.getParentID());
			data.setOrder(customizedMenuData.getOrder());
			data.setIsContainer(customizedMenuData.getIsContainer());
			session.update(data);
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public void delete(Long[] customizedMenuIDs,List<CustomizedMenuData> menuList) throws DataManagerException {
		try{
			Session session = getSession();
			boolean isDeletable = true;
			for(Long customizedMenuID:customizedMenuIDs)
			{	
				for(CustomizedMenuData customizedMenuDataBean:menuList){
					if(customizedMenuID == customizedMenuDataBean.getParentID()){
						isDeletable =false;
						throw new DataManagerException("PARENT_IS_NOT_DELETABLE");						
					}
				}
				if(isDeletable){
					List list = session.createCriteria(CustomizedMenuData.class).add(Restrictions.like("customizedMenuId", customizedMenuID)).list();
					deleteObjectList(list,session);
				}
			}
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}
	
	
	@Override
	public PageList search(CustomizedMenuData customizedMenuData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		
	
	try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class).addOrder(Order.asc("title"));
			if(customizedMenuData.getTitle()!= null && customizedMenuData.getTitle().length() > 0){
				criteria.add(Restrictions.ilike("title", customizedMenuData.getTitle(),MatchMode.ANYWHERE));
			}
			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo - 1) * pageSize));
			if(pageSize > 0){
				criteria.setMaxResults(pageSize);
			}
			List customizeMenuList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0){
				totalPages -= 1;
			}
			pageList = new PageList(customizeMenuList, pageNo, totalPages, totalItems);
		}catch(HibernateException hExe){
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
	
	
	public List<CustomizedMenuData> getCustomizeMenuList() throws DataManagerException{
		List<CustomizedMenuData> list=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class).addOrder(Order.asc("parentID"));		
			return criteria.list();
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	public List<CustomizedMenuData> getCustomizedMenuList() throws DataManagerException{
		List<Long> list=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class).addOrder(Order.asc("parentID"));
			return criteria.list();
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	public List<CustomizedMenuData> getCustomizedMenuItem(Long parentId) throws DataManagerException{
		List<CustomizedMenuData> customizedMenuDatas =null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class).add(Restrictions.eq("parentID", parentId));		
			customizedMenuDatas = criteria.list();
			return customizedMenuDatas;
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	
	

	public CustomizedMenuData getCustomizedMenuDetailData(String title) throws DataManagerException {
		System.out.println(" Method : getCustomizedMenuDetailData()");
		CustomizedMenuData customizedMenuData=null;
		try{
			Session session = getSession();			
			List list = session.createCriteria(CustomizedMenuData.class).add(Restrictions.eq("title", title)).list();
			if(list!=null && !list.isEmpty()){
				customizedMenuData = (CustomizedMenuData) list.get(0);
			}
			return customizedMenuData;
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override

	public List<CustomizedMenuData> getCustomizedMenuList(String title ,Long id)
			throws DataManagerException {
		List<Long> list=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CustomizedMenuData.class).add(Restrictions.and(Restrictions.not(Restrictions.eq("title", title)), Restrictions.not(Restrictions.eq("parentID", id))));
			return criteria.list();
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	
	


}
