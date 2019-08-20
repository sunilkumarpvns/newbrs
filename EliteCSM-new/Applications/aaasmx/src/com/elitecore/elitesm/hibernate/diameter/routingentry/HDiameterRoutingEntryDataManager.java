package com.elitecore.elitesm.hibernate.diameter.routingentry;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam;
import com.elitecore.elitesm.datamanager.diameter.routingentry.DiameterRoutingEntryDataManager;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HDiameterRoutingEntryDataManager extends HBaseDataManager implements DiameterRoutingEntryDataManager{

	private static final String MODULE = "HDiameterRoutingEntryDataManager";
	private static final String ROUTING_TABLE_ID = "routingTableId";
	
	@Override
	public String create(Object obj) throws DataManagerException {
		DiameterRoutingConfData diameterRoutingTableEntry =  (DiameterRoutingConfData) obj;
		String diameterRoutingTableEntryName = diameterRoutingTableEntry.getName();
		
		try {
			
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(ROUTING_TABLE_ID, diameterRoutingTableEntry.getRoutingTableId())).setProjection(Projections.max("orderNumber")); 
			List  maxOrderNumber = criteria.list();
			
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
				diameterRoutingTableEntry.setOrderNumber(++orderNumber);
			} else {
				diameterRoutingTableEntry.setOrderNumber(1L);
			}

			diameterRoutingTableEntry.setAuditUId(auditId);
			
			session.save(diameterRoutingTableEntry);
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
				Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
				int orderNumber = 1;
				for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
					diameterPeerGroupData.setRoutingConfigId(diameterRoutingTableEntry.getRoutingConfigId());
					diameterPeerGroupData.setOrderNumber(orderNumber++);
					session.save(diameterPeerGroupData);
					if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
						Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
						for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
							diameterPeerGroupRelData.setPeerGroupId(diameterPeerGroupData.getPeerGroupId());
							session.save(diameterPeerGroupRelData);
						}
					}
				}
			}
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet()) == false) {
				Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParams = diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet();
				int orderNumber = 1;
				for (DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam : diameterRoutingConfigFailureParams) {
					diameterRoutingConfigFailureParam.setDiameterRoutingConfData(diameterRoutingTableEntry);
					diameterRoutingConfigFailureParam.setOrderNumber(orderNumber++);
					session.save(diameterRoutingConfigFailureParam);
				}
			}
			
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableEntryName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableEntryName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableEntryName + REASON + e.getMessage(), e);
		}
		return diameterRoutingTableEntryName;
	}
}
