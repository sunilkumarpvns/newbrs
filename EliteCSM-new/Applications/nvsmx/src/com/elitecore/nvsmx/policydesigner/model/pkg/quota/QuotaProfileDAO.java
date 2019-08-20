package com.elitecore.nvsmx.policydesigner.model.pkg.quota;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;

/**This method contains method related to hibernate operation for {@code QuotaProfile}
 * @author aditya
 *
 */
public class QuotaProfileDAO {

	public static final String MODULE="Quota-Profile-DAO";
	
	/* property constants for criteria */
	private static final String NAME_PROPERTY="name";
	
	@SuppressWarnings("unchecked")
	public static QuotaProfileData getQuotaProfileByName(String name) {
		QuotaProfileData quotaProfile=null;
		
		try {
			Session session=HibernateSessionFactory.getSession();
		    Criteria criteria = session.createCriteria(QuotaProfileData.class)
		    		           .add(Restrictions.eq(NAME_PROPERTY, name));

			List<QuotaProfileData> list =  criteria.list();
			if(Collectionz.isNullOrEmpty(list)==false){
				quotaProfile= list.get(0);
			}
			return quotaProfile;

		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile for name: " + name + " .Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile for name: " + name + " .Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		
	}
	
	public static List<QuotaProfileData> getQuotaProfile(PkgData pkgdata,String sortColumnName, String sortColumnOrder){
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(QuotaProfileData.class).add(Restrictions.eq("pkgData", pkgdata));
			criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
			if(sortColumnOrder != null && sortColumnOrder.equalsIgnoreCase("desc")){
			    criteria.addOrder(Order.desc("name"));
			} else {
			    criteria.addOrder(Order.asc("name"));
			}									    		
			
			List<QuotaProfileData> list =  criteria.list();
			return list;
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static QuotaProfileData getQuotaProfileById(String  quotaProfileId){
		try {
			
			if(Strings.isNullOrEmpty(quotaProfileId) == true){
				return null;
			}
			Session session = HibernateSessionFactory.getSession();
			
			Criteria criteria = session.createCriteria(QuotaProfileData.class).add(Restrictions.eq("id", quotaProfileId));
			List<QuotaProfileData> list =  criteria.list();
			if(Collectionz.isNullOrEmpty(list)==false){
			 return list.get(0);
			}
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		return null;
	}
	
	public static List<QuotaProfileDetailData> getFupLevelQuotaProfileDetails(Class<QuotaProfileDetailData> type,String fupLevel , QuotaProfileData quotaProfile) throws HibernateDataException  {
	    Session session = null;
		try{
		    	session = HibernateSessionFactory.getSession();
		    	Criteria criteria = session.createCriteria(type);
		    	criteria.add(Restrictions.and(Restrictions.eq("quotaProfile", quotaProfile), Restrictions.eq("fupLevel", Integer.valueOf(fupLevel))));
			return criteria.list();
		}catch (HibernateException he) {
		    LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail. Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
		    LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}
	
	public static List<QuotaProfileData> getQuotaProfileOfPkgData(PkgData pkgdata) throws HibernateDataException{
	  try {
	    Session session = HibernateSessionFactory.getSession();
	    Criteria criteria = session.createCriteria(QuotaProfileData.class).add(Restrictions.eq("pkgData", pkgdata));
	    criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
	    List<QuotaProfileData> list =  criteria.list();
	    return list;
	  } catch (HibernateException he) {
	    LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + he.getMessage());
	    LogManager.getLogger().trace(MODULE, he);
	    throw new HibernateDataException(he.getMessage(), he);
	  } catch (Exception e) {
	    LogManager.getLogger().error(MODULE,"Failed to fetch quota Profile Detail.Reason: " + e.getMessage());
	    LogManager.getLogger().trace(MODULE, e);
	    throw new HibernateDataException(e.getMessage(), e);
	  }
	}
	
	
	
	public static boolean isDuplicateName(String mode,String id,String name,String pkgId) throws HibernateDataException{
		try{
		   PkgData pkg = new PkgData();
		   pkg.setId(pkgId);
		
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(QuotaProfileData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(NAME_PROPERTY));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(CRUDOperationUtil.MODE_UPDATE)){
				criteria.add(Restrictions.ne("id", id));
			}
			List list = criteria.add(Restrictions.ilike(NAME_PROPERTY,name))
					                .add(Restrictions.eq("pkgData", pkg))
					                .add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED))
					                .list();
		   if(Collectionz.isNullOrEmpty(list) == true){
				return false;
			}
		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to Quota Profile  for name "+name+".Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to Quota Profile  for name "+name+".Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}	
		return true;
		}
	
}
