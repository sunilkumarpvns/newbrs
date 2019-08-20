package com.elitecore.netvertexsm.hibernate.gateway.attrmapping;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.PacketMapDataManager;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HPacketMapDataManager extends HBaseDataManager implements PacketMapDataManager{

	@Override
	public PageList search(PacketMappingData packetMappingData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PacketMappingData.class).addOrder(Order.asc("name"));		
			
			if(packetMappingData.getName() != null && packetMappingData.getName().length() > 0) 
				criteria.add(Restrictions.ilike("name", packetMappingData.getName(), MatchMode.ANYWHERE));
			if(packetMappingData.getCommProtocol() != null && packetMappingData.getCommProtocol().length() > 0 && !(packetMappingData.getCommProtocol().equalsIgnoreCase("ALL")))
				criteria.add(Restrictions.ilike("commProtocol", packetMappingData.getCommProtocol()));
			if(packetMappingData.getType() != null && packetMappingData.getType().length() > 0 && !(packetMappingData.getType().equalsIgnoreCase("ALL")))
				criteria.add(Restrictions.ilike("type", packetMappingData.getType()));
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			if(pageSize > 0) 	criteria.setMaxResults(pageSize);
			long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0) 	totalPages-=1;
			
			List<?> packetMapList = criteria.list();
	        pageList = new PageList(packetMapList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void create(PacketMappingData packetMappingData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(packetMappingData);
			session.flush();
			for(AttributeMappingData mappingData : packetMappingData.getAttributeMappings()) {
				mappingData.setPacketMapId(packetMappingData.getPacketMapId());
				session.save(mappingData);
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
	public void update(PacketMappingData packetMappingData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(PacketMappingData.class).add(Restrictions.eq("packetMapId",packetMappingData.getPacketMapId()));
			PacketMappingData packetMapData = (PacketMappingData)criteria.uniqueResult();
			
			deleteAttrMap(packetMappingData);			
			
			for(AttributeMappingData mappingData : packetMappingData.getAttributeMappings()) {
				mappingData.setPacketMapId(packetMappingData.getPacketMapId());
				session.save(mappingData);
			}
			
			packetMapData.setPacketMapId(packetMappingData.getPacketMapId());
			packetMapData.setName(packetMappingData.getName());
			packetMapData.setDescription(packetMappingData.getDescription());
			/*packetMapData.setCommProtocol(packetMappingData.getCommProtocol());
			packetMapData.setPacketType(packetMappingData.getPacketType());
			packetMapData.setType(packetMappingData.getType());*/
			setUpdateAuditDetail(packetMapData);
			session.update(packetMapData);
			session.flush();
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	private void deleteAttrMap(PacketMappingData packetMappingData) {
		Session session = getSession();
		Criteria criteria = session.createCriteria(AttributeMappingData.class).add(Restrictions.eq("packetMapId", packetMappingData.getPacketMapId()));
		List<AttributeMappingData> attributeMappingList = criteria.list();
		
		for(AttributeMappingData mappingData : attributeMappingList) {
			AttributeMappingData data = (AttributeMappingData) session.load(AttributeMappingData.class, mappingData.getAttributeMapId());
			session.delete(data);
			session.flush();
		}
	}
	
	public void changeMappingOrder(Long[] order, long profileId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			List<GatewayProfilePacketMapData> policyList = criteria.add(Restrictions.eq("profileId", profileId)).list();
			if(order != null){
				for(int i=0;i<order.length;i++){
					Long packetMapId = order[i];
					for(int j=0;j<policyList.size();j++){ 
						GatewayProfilePacketMapData profileMap = policyList.get(j);
						if(profileMap.getPacketMapId().equals(packetMapId)){
							profileMap.setOrderNumber(i+1);
							session.update(profileMap);
							break;
						}
					}
				}
			}
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List searchPacketMap(long profileId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("profileId", profileId)).list();
			
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public void delete(List<Long> ids) throws DataManagerException {
		try{
			Session session = getSession();			
			for(Long packetMapId : ids){
				PacketMappingData packetMapData = (PacketMappingData) session.load(PacketMappingData.class, packetMapId);
				session.delete(packetMapData);
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
	public List<PacketMappingData> getPacketMappingList(String commProtocol) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(PacketMappingData.class);
		criteria.add(Restrictions.eq("commProtocol", commProtocol)).addOrder(Order.asc("name"));
		return criteria.list();
	}

	@Override
	public PacketMappingData getPacketMappingData(PacketMappingData packetMappingData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(PacketMappingData.class);
		if(packetMappingData.getPacketMapId() > 0) 
			criteria.add(Restrictions.eq("packetMapId", packetMappingData.getPacketMapId()));
		packetMappingData = (PacketMappingData) criteria.uniqueResult();
		
		Criteria criteriaAttr = session.createCriteria(AttributeMappingData.class);
		if(packetMappingData.getPacketMapId() > 0) 
			criteriaAttr.add(Restrictions.eq("packetMapId", packetMappingData.getPacketMapId())).addOrder(Order.asc("orderNumber"));
		packetMappingData.setAttributeMappings(criteriaAttr.list());
		
		return packetMappingData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PacketMappingData> getDiameterToPCRFPacketMappingList() throws DataManagerException
	{
		List<PacketMappingData> diameterToPCRFPacketMappingList=null;
		try{
			Session session = getSession();
			Criteria diameterToPCRFCriteria = session.createCriteria(PacketMappingData.class);
			
			Conjunction c1=Restrictions.conjunction();
						c1.add(Restrictions.eq("type",ConversionType.GATEWAY_TO_PCRF.getConversionType()));
			                        
			Disjunction disjunction=Restrictions.disjunction();
			disjunction.add(c1);
			
			diameterToPCRFCriteria.add(Restrictions.eq("commProtocol","DIAMETER"));
			diameterToPCRFCriteria.add(disjunction);
			          
			diameterToPCRFPacketMappingList=diameterToPCRFCriteria.list();
		
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    return diameterToPCRFPacketMappingList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PacketMappingData> getPCRFToDiameterPacketMappingList() throws DataManagerException {
		List<PacketMappingData> pcrfToDiameterPacketMappingList=null;
		try{
			Session session = getSession();
			Criteria pcrfToDiameterCriteria = session.createCriteria(PacketMappingData.class);
			
			Conjunction c1=Restrictions.conjunction();
						c1.add(Restrictions.eq("type",ConversionType.PCRF_TO_GATEWAY.getConversionType()));
			            
			Disjunction disjunction=Restrictions.disjunction();
			disjunction.add(c1);
			
			pcrfToDiameterCriteria.add(Restrictions.eq("commProtocol","DIAMETER"));
			pcrfToDiameterCriteria.add(disjunction);
			          
			pcrfToDiameterPacketMappingList=pcrfToDiameterCriteria.list();
		
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    return pcrfToDiameterPacketMappingList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<PacketMappingData> getRadiusToPCRFPacketMappingList() throws DataManagerException
	{
		List<PacketMappingData> radiusToPCRFPacketMappingList=null;
		try{
			Session session = getSession();
			Criteria radiusToPCRFCriteria = session.createCriteria(PacketMappingData.class);
			
			Conjunction c1=Restrictions.conjunction();
						c1.add(Restrictions.eq("type",ConversionType.GATEWAY_TO_PCRF.getConversionType()));
			                        
			Disjunction disjunction=Restrictions.disjunction();
			disjunction.add(c1);
			
			radiusToPCRFCriteria.add(Restrictions.eq("commProtocol","RADIUS"));
			radiusToPCRFCriteria.add(disjunction);
			          
			radiusToPCRFPacketMappingList=radiusToPCRFCriteria.list();
		
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    return radiusToPCRFPacketMappingList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<PacketMappingData> getPCRFToRadiusPacketMappingList() throws DataManagerException {
		List<PacketMappingData> pcrfToRadiusPacketMappingList=null;
		try{
			Session session = getSession();
			Criteria pcrfToRadiusCriteria = session.createCriteria(PacketMappingData.class);
			
			Conjunction c1=Restrictions.conjunction();
						c1.add(Restrictions.eq("type",ConversionType.PCRF_TO_GATEWAY.getConversionType()));			            
			            
			Disjunction disjunction=Restrictions.disjunction();
			disjunction.add(c1);
			
			pcrfToRadiusCriteria.add(Restrictions.eq("commProtocol","RADIUS"));
			pcrfToRadiusCriteria.add(disjunction);
			          
			pcrfToRadiusPacketMappingList=pcrfToRadiusCriteria.list();
		
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    return pcrfToRadiusPacketMappingList;
	}
	
	public boolean isPacketMappingConfiguredWithGatewayProfile(Long[] strPacketMapIds)  throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);			
		criteria.add(Restrictions.in("packetMapId",strPacketMapIds));
		if(criteria.list().size()>0){			
			return true;
}
		return false;
	}
	public List<GatewayProfileData> getPacketMapConfiguredProfileList(long packetMapId)  throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);			
		criteria.add(Restrictions.like("packetMapId",packetMapId));
		List<GatewayProfilePacketMapData> gatewayProfilePacketMapDataList = criteria.list();		
		List<GatewayProfileData> gatewayProfileDataList = new ArrayList<GatewayProfileData>(); 
		for(GatewayProfilePacketMapData obj : gatewayProfilePacketMapDataList){
			Long profileId = obj.getProfileId();
			GatewayProfileData gatewayProfileData = getGatewayProfileList(profileId);
			gatewayProfileDataList.add(gatewayProfileData);
		}
		return gatewayProfileDataList;
	}
	public GatewayProfileData getGatewayProfileList(long profileId)  throws DataManagerException {
		Session session = getSession();
		GatewayProfileData gatewayProfileData = (GatewayProfileData) session.get(GatewayProfileData.class, new Long(profileId));								
		return gatewayProfileData;						
	}
 	
}
