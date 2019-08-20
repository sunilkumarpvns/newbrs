package com.elitecore.elitesm.web.dashboard.widget.dao.esi;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.web.dashboard.widget.model.esi.ESIWidgetData;

public class ESIWidgetDAOImpl implements ESIWidgetDAO {

	@Override
	public List<ESIWidgetData> getESIWidgetDataList() {
		Session session = HibernateSessionFactory.createSession();
		/*Criteria criteria = session.createCriteria(ESIWidgetData.class)
									.setProjection(Projections.projectionList()
										.add(Projections.groupProperty("esi"))
										.add(Projections.count("accessAccept"))
										.add(Projections.count("accessReject"))
										.add(Projections.count("accessChallange"))
										.add(Projections.count("requestDrop"))
									);  
		List list = criteria.list();*/
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty("esi"), "esi");
		projectionList.add(Projections.max("accessAccept"), "accessAccept");
		projectionList.add(Projections.max("accessReject"), "accessReject");
		projectionList.add(Projections.max("accessChallange"), "accessChallange");
		projectionList.add(Projections.max("requestDrop"), "requestDrop");
		
		Criteria criteria = session.createCriteria(ESIWidgetData.class)
									.setProjection(projectionList)
									.setResultTransformer(Transformers.aliasToBean(ESIWidgetData.class))
									;
		
		
		List<ESIWidgetData> list = criteria.list();
		System.out.println("List : " + list);
		HibernateSessionFactory.closeSession(session);
		return list;
	}

	@Override
	public List<ESIWidgetData> getESIWidgetDataList(int interval) {
		Session session = HibernateSessionFactory.createSession();
		Criteria criteria = session.createCriteria(ESIWidgetData.class);
		long time = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5);
		Timestamp timestamp = new Timestamp(time);
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty("esi"), "esi");
		projectionList.add(Projections.max("accessAccept"), "accessAccept");
		projectionList.add(Projections.max("accessReject"), "accessReject");
		projectionList.add(Projections.max("accessChallange"), "accessChallange");
		projectionList.add(Projections.max("requestDrop"), "requestDrop");
		
		criteria.add(Restrictions.ge("timestamp", timestamp))
				.setProjection(projectionList)
				.setResultTransformer(Transformers.aliasToBean(ESIWidgetData.class));
		/*criteria.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("esi"))
                .add(Projections.count("accessAccept"))
                .add(Projections.count("accessReject"))
                .add(Projections.count("accessChallange"))
                .add(Projections.count("requestDrop"))
                ); */ 
		List<ESIWidgetData> list = criteria.list();
		HibernateSessionFactory.closeSession(session);
		return list;
	}

}
