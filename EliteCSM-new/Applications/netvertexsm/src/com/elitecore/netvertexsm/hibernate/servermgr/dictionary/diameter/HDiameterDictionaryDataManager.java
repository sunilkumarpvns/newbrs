/**
 * 
 */
package com.elitecore.netvertexsm.hibernate.servermgr.dictionary.diameter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.DataTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.DiameterDictionaryDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterGroupedAttributeData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception.AttributeNotFoundException;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

/**
 * @author pratik.chauhan
 *
 */
public class HDiameterDictionaryDataManager extends HBaseDataManager implements DiameterDictionaryDataManager {
	
	public List getDatatype() throws DataManagerException
    {
        List dataTypeList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(DataTypeData.class);
            criteria.add(Restrictions.eq("dictionaryType","D"));
            dataTypeList = criteria.list();
        }
        catch (HibernateException hExp) {
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            throw new DataManagerException(exp.getMessage(), exp);
        }
         return dataTypeList;
    }

	public void create(DiameterDictionaryData dictionaryData)	throws DataManagerException {
		DiameterDictionaryParamDetailData diameterdicParamDetailData=null;
		try{
			
		    Session session =getSession();
		    session.save(dictionaryData);
		    session.flush();
		    
		    Long dictionaryId=dictionaryData.getDictionaryId();
		    
		    List<DiameterDictionaryParamDetailData> lstDiametrdicParamDetail=dictionaryData.getDiameterDictionaryParamDetailList();
		    for (Iterator iterator = lstDiametrdicParamDetail.iterator(); iterator.hasNext();) {
				
		    	diameterdicParamDetailData = (DiameterDictionaryParamDetailData) iterator.next();
				diameterdicParamDetailData.setDictionaryId(dictionaryId);
				String attributeId=diameterdicParamDetailData.getVendorId()+":"+diameterdicParamDetailData.getVendorParameterId();
				diameterdicParamDetailData.setAttributeId(attributeId);
				session.save(diameterdicParamDetailData);
				session.flush();
				
			}
		    
		   for(int i=0;i<lstDiametrdicParamDetail.size();i++){
			   diameterdicParamDetailData =lstDiametrdicParamDetail.get(i);
			   
			   if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){
					  
					  Long paramdetailId=diameterdicParamDetailData.getDictionaryParameterDetailId();
					  Long diameterdicId=dictionaryData.getDictionaryId();
					   if(diameterdicParamDetailData.getFixedGroupedAttribute() != null){
						     
						     Map<String, AvpRule> fixedGroupedAttribute=diameterdicParamDetailData.getFixedGroupedAttribute();
						     Iterator<String> itr=fixedGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=fixedGroupedAttribute.get(itr.next());
						           Long attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
						           diameterGroupedData.setVendorId(data.getVendorId());
						           diameterGroupedData.setAttributeId(data.getAttrId());
						           diameterGroupedData.setName(data.getName());
						           diameterGroupedData.setMaximum(data.getMaximum());
						           diameterGroupedData.setMinimum(data.getMinimum());
						           diameterGroupedData.setParameterDetailId(paramdetailId);
						           diameterGroupedData.setAttributeRuleId(attributeRuleId);
						           diameterGroupedData.setGroupedAttributeType("F");
						           diameterGroupedData.setDiameterdicId(diameterdicId);
						           session.save(diameterGroupedData);
						           session.flush();  	  
						      }
						     
						   
					   }
					   if(diameterdicParamDetailData.getRequiredGroupedAttribute() != null){
						   
						   Map<String, AvpRule> requiredGroupedAttribute=diameterdicParamDetailData.getRequiredGroupedAttribute();
						     Iterator<String> itr=requiredGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=requiredGroupedAttribute.get(itr.next());
						           Long attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
						           diameterGroupedData.setVendorId(data.getVendorId());
						           diameterGroupedData.setAttributeId(data.getAttrId());
						           diameterGroupedData.setName(data.getName());
						           diameterGroupedData.setMaximum(data.getMaximum());
						           diameterGroupedData.setMinimum(data.getMinimum());
						           diameterGroupedData.setParameterDetailId(paramdetailId);
						           diameterGroupedData.setAttributeRuleId(attributeRuleId);
						           diameterGroupedData.setGroupedAttributeType("R");
						           diameterGroupedData.setDiameterdicId(diameterdicId);
						           session.save(diameterGroupedData);
						           session.flush();  	  
						      }
						   
						   
					   }
					   if(diameterdicParamDetailData.getOptionalGroupedAttribute() != null){
						   
						   Map<String, AvpRule> optionalGroupedAttribute=diameterdicParamDetailData.getOptionalGroupedAttribute();
						     Iterator<String> itr=optionalGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=optionalGroupedAttribute.get(itr.next());
						           Long attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
						           diameterGroupedData.setVendorId(data.getVendorId());
						           diameterGroupedData.setAttributeId(data.getAttrId());
						           diameterGroupedData.setName(data.getName());
						           diameterGroupedData.setMaximum(data.getMaximum());
						           diameterGroupedData.setMinimum(data.getMinimum());
						           diameterGroupedData.setParameterDetailId(paramdetailId);
						           diameterGroupedData.setAttributeRuleId(attributeRuleId);
						           diameterGroupedData.setGroupedAttributeType("O");
						           diameterGroupedData.setDiameterdicId(diameterdicId);
						           session.save(diameterGroupedData);
						           session.flush();  	  
						      }
						    
					   }
					
				}
		   }
		    
		    
		    
		    
		} catch (ConstraintViolationException hExp){
			if(hExp.getConstraintName()!=null && hExp.getConstraintName().contains("UK1_MDIAMETERDICPARAMDETAIL")){
	    		   throw new ConstraintViolationException("Unique Key Constraint violated.Attribute Name: '"+diameterdicParamDetailData.getName()+"' must be unique.", null,hExp.getConstraintName());
				}else {
					throw new DataManagerException("Attribute Name: '"+diameterdicParamDetailData.getName()+"' or Attribute Id: '"+diameterdicParamDetailData.getAttributeId()+"'", hExp);
				}
			} catch (HibernateException hExp){
	    		throw new DataManagerException(hExp.getMessage(), hExp);
			} catch(Exception exp){
				throw new DataManagerException(exp.getMessage(), exp);
			}
		
	}

	private Long getAttributeRuleId(String name, Session session) throws DataManagerException,AttributeNotFoundException {
         
		Long attributeRuleId=0L;
		try{
			 /*
			  *  for support of '*' in attributeRule set attributeRule id = -1[ vendor-id="*" and id="*" ]
			  */
			  if("attribute".equals(name)){
				   return null;
			  }else{
				  Criteria criteria=session.createCriteria(DiameterDictionaryParamDetailData.class);
				  criteria.add(Restrictions.eq("name",name));
				  DiameterDictionaryParamDetailData data=(DiameterDictionaryParamDetailData) criteria.uniqueResult();
				  if(data == null){
					  throw new AttributeNotFoundException("Attribute '"+name+"' Not found in Dictionary");
				  }
				  attributeRuleId=data.getDictionaryParameterDetailId();
			  }
			  
		 }catch(HibernateException hExp){
			 throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
			 throw new DataManagerException(exp.getMessage(), exp);
		 }
	 	
		return attributeRuleId;
	}

	public List<DiameterDictionaryData> getAllList(DiameterDictionaryData data) throws DataManagerException {
		List<DiameterDictionaryData> dictionaryList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(DiameterDictionaryData.class);
            
            if(data != null){
            	
            		
            	if(data.getDictionaryId()!= null && data.getDictionaryId()>0){
            		criteria.add(Restrictions.eq("dictionaryId",data.getDictionaryId()));
            	}
            	
            	if(data.getVendorName() != null){
            		criteria.add(Restrictions.eq("vendorName",data.getVendorName()));
            	}

            	if(data.getDictionaryNumber() != null && data.getDictionaryNumber() != -1){
            		criteria.add(Restrictions.eq("dictionaryNumber",new Long(data.getDictionaryNumber()) ));
            	}

            	if(data.getVendorId() != null && data.getVendorId() != -1){
            		criteria.add(Restrictions.eq("vendorId",new Long(data.getVendorId()) ));
            	}

            	if(data.getCommonStatusId() != null){
            		criteria.add(Restrictions.eq("commonStatusId",data.getCommonStatusId()));
            	}

            	if(data.getCreatedByStaffId() != null){
            		criteria.add(Restrictions.eq("createdByStaffId",data.getCreatedByStaffId()));
            	}
            	
            	if(data.getApplicationId() != null && data.getApplicationId() != -1){
            		criteria.add(Restrictions.eq("applicationId",new Long(data.getApplicationId()) ));
            	}
            	if(data.getApplicationName() != null){
            		criteria.add(Restrictions.eq("applicationName",data.getApplicationName()));
            	}
            	

            }
            
            dictionaryList = criteria.list();
         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	exp.printStackTrace();
        	throw new DataManagerException(exp.getMessage(), exp);
        }
        return dictionaryList;
	}

	public List<DiameterDictionaryParamDetailData> getDictionaryParameterDetailList(Long dictionaryId) throws DataManagerException {
		List<DiameterDictionaryParamDetailData> list=null;
		List<DiameterDictionaryParamDetailData> resultList=new ArrayList<DiameterDictionaryParamDetailData>();
		try{
			 Session session = getSession();
	         Criteria criteria = session.createCriteria(DiameterDictionaryParamDetailData.class);
	         if(dictionaryId != 0){
	              criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).addOrder(Order.asc("vendorParameterId"));
	         }
	         list=criteria.list();
	         
	         for (Iterator iterator = list.iterator(); iterator.hasNext();) {
	        	 
				DiameterDictionaryParamDetailData diameterdicParamDetailData = (DiameterDictionaryParamDetailData) iterator.next();
				
				 if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){
					 
					 Map<String, AvpRule> fixedGroupedAttribute = new HashMap<String, AvpRule>();
					 Map<String, AvpRule> requiredGroupedAttribute = new HashMap<String, AvpRule>();
					 Map<String, AvpRule> optionalGroupedAttribute = new HashMap<String, AvpRule>();
					 
					 Criteria attrRuleCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
					 attrRuleCriteria.add(Restrictions.eq("parameterDetailId",diameterdicParamDetailData.getDictionaryParameterDetailId()));
					 List<DiameterGroupedAttributeData> groupedAttributeList=attrRuleCriteria.list();
					 for (Iterator iterator2 = groupedAttributeList.iterator(); iterator2.hasNext();) {
						
						 DiameterGroupedAttributeData diameterGroupedAttributeData = (DiameterGroupedAttributeData) iterator2.next();
						 String groupAttributeType=diameterGroupedAttributeData.getGroupedAttributeType();
						 AvpRule avpRule = new AvpRule();
						 
						 if("R".equalsIgnoreCase(groupAttributeType)){
							 avpRule=getAvpRule(diameterGroupedAttributeData);
							 requiredGroupedAttribute.put(avpRule.getName(),avpRule);
						 }else if("O".equalsIgnoreCase(groupAttributeType)){
							 avpRule=getAvpRule(diameterGroupedAttributeData);
							 optionalGroupedAttribute.put(avpRule.getName(),avpRule);
						 }else if("F".equalsIgnoreCase(groupAttributeType)){
							 avpRule=getAvpRule(diameterGroupedAttributeData);
							 fixedGroupedAttribute.put(avpRule.getName(),avpRule);
						 }
						
						
					}
					 diameterdicParamDetailData.setRequiredGroupedAttribute(requiredGroupedAttribute);
					 diameterdicParamDetailData.setOptionalGroupedAttribute(optionalGroupedAttribute);
					 diameterdicParamDetailData.setFixedGroupedAttribute(fixedGroupedAttribute);
			 	 }
				 
				 resultList.add(diameterdicParamDetailData);
				
			}
			
		}catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }
		return resultList;
	}


	private AvpRule getAvpRule(DiameterGroupedAttributeData diameterGroupedAttributeData) {
		 AvpRule avpRule = new AvpRule();
		 
		 avpRule.setName(diameterGroupedAttributeData.getName());
		 avpRule.setMaximum(diameterGroupedAttributeData.getMaximum());
		 avpRule.setMinimum(diameterGroupedAttributeData.getMinimum());
         avpRule.setAttrId(diameterGroupedAttributeData.getAttributeId());
         avpRule.setVendorId(diameterGroupedAttributeData.getVendorId());
		return avpRule;
	}
	
	

	public void updateDictionary(DiameterDictionaryData serverData) throws DataManagerException {
	   
		try{
			
			Map<String,List<Long>> groupAttrMap = new HashMap<String, List<Long>>();
			Long dictionaryId=serverData.getDictionaryId();
			Session session=getSession();
			Criteria criteria=session.createCriteria(DiameterDictionaryData.class);
			criteria.add(Restrictions.eq("dictionaryId",serverData.getDictionaryId()));
			DiameterDictionaryData data=(DiameterDictionaryData) criteria.uniqueResult();
			
			serverData.setApplicationId(data.getApplicationId());
			serverData.setApplicationName(data.getApplicationName());
			serverData.setCommonStatusId(data.getCommonStatusId());
			serverData.setDescription(data.getDescription());
	        serverData.setDictionaryNumber(data.getDictionaryNumber());
	        serverData.setEditable(data.getEditable());
	        serverData.setLastModifiedByStaffId(data.getLastModifiedByStaffId());
	        serverData.setLastModifiedDate(data.getLastModifiedDate());
	        serverData.setModalNumber(data.getModalNumber());
	        serverData.setStatusChangedDate(data.getStatusChangedDate());
	        serverData.setSystemGenerated(data.getSystemGenerated());
	        serverData.setVendorId(data.getVendorId());
	        serverData.setVendorName(data.getVendorName());
	        
	        Set<DiameterDictionaryParamDetailData> diameterdicParamSet=serverData.getDiameterDictionaryParamDetailSet();
	       
	        /*
	         * delete grouped attribute list of selected dictionary from db 
	         */
	        
	        Criteria groupCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
		    groupCriteria.add(Restrictions.eq("diameterdicId",serverData.getDictionaryId()));
		    List<DiameterGroupedAttributeData> oldGroupAttributeList=groupCriteria.list();
		    deleteObjectList(oldGroupAttributeList, session);
		    
		    /*
		     * delete from diameterdicparamdetail data list
		     */
	        
          Set<DiameterDictionaryParamDetailData> oldDiameterdicParamSet=data.getDiameterDictionaryParamDetailSet();
	        
	        for (Iterator iterator = oldDiameterdicParamSet.iterator(); iterator.hasNext();) {
	        	
	        	DiameterDictionaryParamDetailData diameterdicParamDetailData = (DiameterDictionaryParamDetailData) iterator.next();
	            /*
	             * Build groupAttrMap that contain attrRule detail of other dictionary attribute
	             */
	        	String attrRuleName=diameterdicParamDetailData.getName();
	        	
	        	Criteria groupeAttrCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
	        	groupeAttrCriteria.add(Restrictions.eq("name",attrRuleName));
	        	/*
	        	 * set null to attributeRuleId and update it 
	        	 */
	        	List<DiameterGroupedAttributeData> groupedAttrList=groupeAttrCriteria.list();
	        	List<Long> grpInstanceIdList = new ArrayList<Long>();
	        	for (Iterator iterator2 = groupedAttrList.iterator(); iterator2.hasNext();) {
					
	        		DiameterGroupedAttributeData diameterGroupedAttributeData = (DiameterGroupedAttributeData) iterator2.next();
	        		Long groupedAttrId=diameterGroupedAttributeData.getGroupedAttrId();
	        		grpInstanceIdList.add(groupedAttrId);
	        		
	        		Criteria updateCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
					updateCriteria.add(Restrictions.eq("groupedAttrId",groupedAttrId));
					
					DiameterGroupedAttributeData grpAttrData=(DiameterGroupedAttributeData) updateCriteria.uniqueResult();
					grpAttrData.setAttributeRuleId(null);
					session.update(grpAttrData);
					session.flush();
					
				}
	        	if(grpInstanceIdList.size()>0 && !grpInstanceIdList.isEmpty())
	        	groupAttrMap.put(attrRuleName, grpInstanceIdList);
	        	
	        	session.delete(diameterdicParamDetailData);
	        	session.flush();
	        	
		    
	        }   
	        
	        /*
	         * 1.Add new data to diameterdicparamdetail data
	         * 2.Iterate Map and find diameterdicparamdata ,fetch dictionaryparamid and set it as attributeruleId of Groupattribute
	         * 
	         */
	        
	        List<DiameterDictionaryParamDetailData> lstDiametrdicParamDetail=new ArrayList<DiameterDictionaryParamDetailData>(diameterdicParamSet);
		    for (Iterator iterator = lstDiametrdicParamDetail.iterator(); iterator.hasNext();) {
				
		    	DiameterDictionaryParamDetailData diameterdicParamDetailData = (DiameterDictionaryParamDetailData) iterator.next();
				diameterdicParamDetailData.setDictionaryId(dictionaryId);
				String attributeId=diameterdicParamDetailData.getVendorId()+":"+diameterdicParamDetailData.getVendorParameterId();
				diameterdicParamDetailData.setAttributeId(attributeId);
				session.save(diameterdicParamDetailData);
				session.flush();
				
			}

		    Iterator<String> itr=groupAttrMap.keySet().iterator();
		    
		    while(itr.hasNext()){
		          	
		    	String attrRuleName=itr.next();
		    	List<Long> grpAttrIds=groupAttrMap.get(attrRuleName);
		    	Criteria diameterdicCriteria=session.createCriteria(DiameterDictionaryParamDetailData.class);
		    	diameterdicCriteria.add(Restrictions.eq("name",attrRuleName));
		    	DiameterDictionaryParamDetailData diameterDicParamData=(DiameterDictionaryParamDetailData) diameterdicCriteria.uniqueResult();
   		    	if(diameterDicParamData==null)
   		    		throw new AttributeNotFoundException("Attribute "+attrRuleName+" not found in Dictionary");  
		    	
   		    	Long diameterdicId=diameterDicParamData.getDictionaryParameterDetailId();
   		    	for(int i=0;i<grpAttrIds.size();i++){
   		    		
   		    		Long grpAttrid=grpAttrIds.get(i);
   		    		Criteria diamerCrt=session.createCriteria(DiameterGroupedAttributeData.class);
   		    		diamerCrt.add(Restrictions.eq("groupedAttrId",grpAttrid));
   		    		
   		    		DiameterGroupedAttributeData grpAttrData=(DiameterGroupedAttributeData) diamerCrt.uniqueResult();
   		    		grpAttrData.setAttributeRuleId(diameterdicId);
   		    		session.update(grpAttrData);
   		    		session.flush();
   		    		
   		    	}
   		    	
		    	
		    }
		    
		    
		    /*
		     * update groupedattrList
		     */
		    
		    for(int i=0;i<lstDiametrdicParamDetail.size();i++){
				   DiameterDictionaryParamDetailData diameterdicParamDetailData =lstDiametrdicParamDetail.get(i);
				   
				   if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){
						  
						  Long paramdetailId=diameterdicParamDetailData.getDictionaryParameterDetailId();
						  Long diameterdicId=serverData.getDictionaryId();
						   if(diameterdicParamDetailData.getFixedGroupedAttribute() != null){
							     
							     Map<String, AvpRule> fixedGroupedAttribute=diameterdicParamDetailData.getFixedGroupedAttribute();
							     Iterator<String> itr1=fixedGroupedAttribute.keySet().iterator();
							     
							      while(itr1.hasNext()){
							    	   
							    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
							           AvpRule data1=fixedGroupedAttribute.get(itr1.next());
							           Long attributeRuleId = getAttributeRuleId(data1.getName(),session);
							           
							           diameterGroupedData.setName(data1.getName());
							           diameterGroupedData.setVendorId(data1.getVendorId());
							           diameterGroupedData.setAttributeId(data1.getAttrId());
							           diameterGroupedData.setMaximum(data1.getMaximum());
							           diameterGroupedData.setMinimum(data1.getMinimum());
							           diameterGroupedData.setParameterDetailId(paramdetailId);
							           diameterGroupedData.setAttributeRuleId(attributeRuleId);
							           diameterGroupedData.setGroupedAttributeType("F");
							           diameterGroupedData.setDiameterdicId(diameterdicId);
							           session.save(diameterGroupedData);
							           session.flush();  	  
							      }
							     
							   
						   }
						   if(diameterdicParamDetailData.getRequiredGroupedAttribute() != null){
							   
							   Map<String, AvpRule> requiredGroupedAttribute=diameterdicParamDetailData.getRequiredGroupedAttribute();
							     Iterator<String> itr2=requiredGroupedAttribute.keySet().iterator();
							     
							      while(itr2.hasNext()){
							    	   
							    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
							           AvpRule data2=requiredGroupedAttribute.get(itr2.next());
							           Long attributeRuleId = getAttributeRuleId(data2.getName(),session);
							           
							           diameterGroupedData.setName(data2.getName());
							           diameterGroupedData.setVendorId(data2.getVendorId());
							           diameterGroupedData.setAttributeId(data2.getAttrId());
							           diameterGroupedData.setMaximum(data2.getMaximum());
							           diameterGroupedData.setMinimum(data2.getMinimum());
							           diameterGroupedData.setParameterDetailId(paramdetailId);
							           diameterGroupedData.setAttributeRuleId(attributeRuleId);
							           diameterGroupedData.setGroupedAttributeType("R");
							           diameterGroupedData.setDiameterdicId(diameterdicId);
							           session.save(diameterGroupedData);
							           session.flush();  	  
							      }
							   
							   
						   }
						   if(diameterdicParamDetailData.getOptionalGroupedAttribute() != null){
							   
							   Map<String, AvpRule> optionalGroupedAttribute=diameterdicParamDetailData.getOptionalGroupedAttribute();
							     Iterator<String> itr2=optionalGroupedAttribute.keySet().iterator();
							     
							      while(itr2.hasNext()){
							    	   
							    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
							           AvpRule data2=optionalGroupedAttribute.get(itr2.next());
							           Long attributeRuleId = getAttributeRuleId(data2.getName(),session);
							           
							           diameterGroupedData.setName(data2.getName());
							           diameterGroupedData.setVendorId(data2.getVendorId());
							           diameterGroupedData.setAttributeId(data2.getAttrId());
							           diameterGroupedData.setMaximum(data2.getMaximum());
							           diameterGroupedData.setMinimum(data2.getMinimum());
							           diameterGroupedData.setParameterDetailId(paramdetailId);
							           diameterGroupedData.setAttributeRuleId(attributeRuleId);
							           diameterGroupedData.setGroupedAttributeType("O");
							           diameterGroupedData.setDiameterdicId(diameterdicId);
							           session.save(diameterGroupedData);
							           session.flush();  	  
							      }
							    
						   }
						
					}
			   }
		    
		    
		    
		    
	        
	       
	        
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }
		
		
	}

	
	public void updateStatus(long dictionaryId, String commonStatusId,Timestamp statusChangeDate) throws DataManagerException 
	{
		Session session = getSession();
		DiameterDictionaryData dictionaryData = null;
			
		try{
				Criteria criteria = session.createCriteria(DiameterDictionaryData.class);
				dictionaryData = (DiameterDictionaryData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).uniqueResult();

				dictionaryData.setCommonStatusId(commonStatusId);
				dictionaryData.setStatusChangedDate(statusChangeDate);
				dictionaryData.setLastModifiedDate(statusChangeDate);
				session.update(dictionaryData);
				session.flush();
	
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		
		
	}

	public void delete(long dictionaryId) throws DataManagerException {
		
		Session session =getSession();
		try{
             /*
              * delete grouped attribute     
              */
			   
			 Criteria grpAttrCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
			 grpAttrCriteria.add(Restrictions.eq("diameterdicId",dictionaryId));
			 List<DiameterGroupedAttributeData> grpAttrList=grpAttrCriteria.list();
			 deleteObjectList(grpAttrList, session);
			 
			/*
			 * delete diameterdicparam detail 
			 */
			 
			 Criteria paramCriteria=session.createCriteria(DiameterDictionaryParamDetailData.class);
			 paramCriteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			 List<DiameterDictionaryParamDetailData> attrList=paramCriteria.list();
			 deleteObjectList(attrList, session);
			 /*
			  * delete diameterdictionary
			  */
			 
			 Criteria dicCriteria=session.createCriteria(DiameterDictionaryData.class);
			 dicCriteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			 DiameterDictionaryData data=(DiameterDictionaryData) dicCriteria.uniqueResult();
             session.delete(data);
             session.flush();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	public List<DiameterDictionaryData> getOnlyDiameterDictionaryDataList() throws DataManagerException {
		List dictionaryParamList = null;
		List<DiameterDictionaryData> tmpDictionaryParamList;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterDictionaryData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryId"));
			proList.add(Projections.property("vendorName"));
			proList.add(Projections.property("commonStatusId"));
			proList.add(Projections.property("vendorId"));
			proList.add(Projections.property("applicationId"));
			
			criteria.setProjection(proList);
			dictionaryParamList = criteria.list();
			DiameterDictionaryData dictionaryData;
			tmpDictionaryParamList = new ArrayList<DiameterDictionaryData>();

			if(dictionaryParamList != null && dictionaryParamList.size() > 0) {
				Iterator dictionaryParamListItr = dictionaryParamList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryData = new DiameterDictionaryData();
					dictionaryData.setDictionaryId((Long)tmpDictionaryData[0]);
					dictionaryData.setVendorName((String)tmpDictionaryData[1]);
					dictionaryData.setCommonStatusId((String)tmpDictionaryData[2]);
					dictionaryData.setVendorId((Long)tmpDictionaryData[3]);
					dictionaryData.setApplicationId((Long)tmpDictionaryData[4]);
					tmpDictionaryParamList.add(dictionaryData);
				}
			}

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}
		return tmpDictionaryParamList;	
	}

	public List<DiameterDictionaryParamDetailData> getOnlyDiameterDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException {
		List<DiameterDictionaryParamDetailData> dictionaryParamDetailList = null;
		List tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(DiameterDictionaryParamDetailData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("diameterdciParamDetailId"));
			proList.add(Projections.property("name"));
			proList.add(Projections.property("vendorId"));
			proList.add(Projections.property("attributeId"));
			proList.add(Projections.property("dataTypeId"));

			criteria.setProjection(proList);
			criteria.add( Restrictions.disjunction()
					.add( Restrictions.ilike("name","%"+searchNameOrAttributeId+"%" ) )
					.add( Restrictions.like("attributeId",searchNameOrAttributeId+"%"))
			);

			tmpDictionaryParamObjectList = criteria.list();	
			DiameterDictionaryParamDetailData dictionaryParameterDetailData;
			dictionaryParamDetailList = new ArrayList<DiameterDictionaryParamDetailData>();

			if(tmpDictionaryParamObjectList != null && tmpDictionaryParamObjectList.size() > 0) {
				Iterator dictionaryParamListItr = tmpDictionaryParamObjectList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryParameterDetailData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryParameterDetailData = new DiameterDictionaryParamDetailData();
					dictionaryParameterDetailData.setDictionaryParameterDetailId((Long)tmpDictionaryParameterDetailData[0]);
					dictionaryParameterDetailData.setName((String)tmpDictionaryParameterDetailData[1]);
					dictionaryParameterDetailData.setVendorId((Long)tmpDictionaryParameterDetailData[2]);
					dictionaryParameterDetailData.setAttributeId((String)tmpDictionaryParameterDetailData[3]);
					dictionaryParameterDetailData.setDataTypeId((String)tmpDictionaryParameterDetailData[4]);
					dictionaryParamDetailList.add(dictionaryParameterDetailData);
				}
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} 
		return dictionaryParamDetailList;
	}

}


