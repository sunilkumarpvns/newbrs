package com.elitecore.nvsmx.policydesigner.model.pkg.syquota;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;

/**
 * Perform SyQuotaProfile related database operation 
 * @author Dhyani.Raval
 *
 */
public class SyQuotaProfileDAO {
	public static final String MODULE = SyQuotaProfileDAO.class.getSimpleName();
	public static final String STATUS_PROPERTY = "status";
	private static final String NAME_PROPERTY="name";
	
	@SuppressWarnings("unchecked")
	public static List<SyQuotaProfileData> getSyQuotaProfileDetails(String pkgId){
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(SyQuotaProfileData.class);
			PkgData pkgData = new PkgData();
			pkgData.setId(pkgId);
			criteria.add(Restrictions.eq("pkgData", pkgData));
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			return criteria.list();
		}catch (HibernateException hex) {
			getLogger().error(MODULE,"Failed to fetch Sy Quota Profile. Reason: " + hex.getMessage());
			getLogger().trace(MODULE, hex);
			throw new HibernateDataException(hex.getMessage(), hex);
		} catch (Exception e) {
			getLogger().error(MODULE,"Failed to fetch Sy Quota Profile. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		
	}
	
	public static boolean isDuplicateName(String mode,String id,String name,String pkgId) throws HibernateDataException{
		try{
		   PkgData pkg = new PkgData();
		   pkg.setId(pkgId);
		
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(SyQuotaProfileData.class);
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
			LogManager.getLogger().error(MODULE, "Failed to check Sy quota profile  for name "+name+". Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to check Sy quota profile  for name "+name+". Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}	
		return true;
		}

}
