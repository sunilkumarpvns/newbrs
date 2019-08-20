package com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;

/**
 * Perform IMSPkgService related database operation
 * @author Dhyani.Raval
 *
 */
public class IMSPkgServiceDAO {

	private static final String MODULE="IMS-PKG-SRVC-DAO";
	public static final String STATUS_PROPERTY = "status";
	private static final String NAME_PROPERTY="name";

	@SuppressWarnings("unchecked")
	public static  List<IMSPkgServiceData> getIMSServiceByIMSPkg(String imsPkgId) throws HibernateDataException{
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(IMSPkgServiceData.class);
			IMSPkgData imsPkgData = new IMSPkgData();
			imsPkgData.setId(imsPkgId);
			criteria.add(Restrictions.eq("imsPkgData", imsPkgData));
			criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
			return criteria.list();

		}catch (HibernateException he) {
			getLogger().error(MODULE, "Failed to get list of ims package services. Reason: "+he.getMessage());
			getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			getLogger().error(MODULE, "Failed to get list of ims package services. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}

	public static boolean isDuplicateName(String mode,String id,String name,String imsPkgId) throws HibernateDataException{
		try{
			IMSPkgData imsPkgData = new IMSPkgData();
			imsPkgData.setId(imsPkgId);

			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(IMSPkgServiceData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(NAME_PROPERTY));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(CRUDOperationUtil.MODE_UPDATE)){
				criteria.add(Restrictions.ne("id", id));
			}
			List list = criteria.add(Restrictions.ilike(NAME_PROPERTY,name))
					.add(Restrictions.eq("imsPkgData", imsPkgData))
					.add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED))
					.list();
			if(Collectionz.isNullOrEmpty(list) == true){
				return false;
			}
		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to check Ims service data  for name "+name+". Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to check Ims service data  for name "+name+". Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		return true;
	}


}
