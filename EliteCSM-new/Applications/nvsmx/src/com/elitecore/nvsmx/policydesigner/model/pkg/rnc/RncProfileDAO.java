package com.elitecore.nvsmx.policydesigner.model.pkg.rnc;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * To fetch information from database for Rating And Charging Profile
 * Created by dhyani on 4/8/17.
 */
public class RncProfileDAO {

	public static final String MODULE = "RncProfileDAO";
	public static final String STATUS_PROPERTY = "status";

	public static List<RncProfileDetailData> getFupLevelQuotaProfileDetails(Class<RncProfileDetailData> type,String fupLevel , RncProfileData rncProfileData) throws HibernateDataException  {
	    Session session = HibernateSessionFactory.getSession();
		Criteria criteria = session.createCriteria(type);
		criteria.add(Restrictions.and(Restrictions.eq("rncProfileData", rncProfileData), Restrictions.eq("fupLevel", Integer.valueOf(fupLevel))));
		return criteria.list();
	}

	public static List<RncProfileData> getRncProfileDetails(PkgData pkgData){
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(RncProfileData.class);
		criteria.add(Restrictions.eq("pkgData", pkgData));
		criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
		return criteria.list();
	}
	
}
