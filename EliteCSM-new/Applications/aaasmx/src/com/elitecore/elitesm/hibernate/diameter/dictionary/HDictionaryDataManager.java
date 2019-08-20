/**
 * 
 */
package com.elitecore.elitesm.hibernate.diameter.dictionary;

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

import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.dictionary.DictionaryDataManager;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterGroupedAttributeData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.exception.AttributeNotFoundException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * @author pratik.chauhan
 *
 */
public class HDictionaryDataManager extends HBaseDataManager implements DictionaryDataManager {
	private static final String MODULE="HDictionaryDataManager";
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

	public void create(DiameterdicData dictionaryData)	throws DataManagerException {
		DiameterdicParamDetailData diameterdicParamDetailData=null;
		try{
			
		    Session session =getSession();
		    session.save(dictionaryData);
		    session.flush();
		    
		    String dictionaryId=dictionaryData.getDictionaryId();
		    
		    List<DiameterdicParamDetailData> lstDiametrdicParamDetail=dictionaryData.getDiameterdicParamDetailList();
		    for (Iterator iterator = lstDiametrdicParamDetail.iterator(); iterator.hasNext();) {
				
		    	diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
				diameterdicParamDetailData.setDictionaryId(dictionaryId);
				String attributeId=diameterdicParamDetailData.getVendorId()+":"+diameterdicParamDetailData.getVendorParameterId();
				diameterdicParamDetailData.setAttributeId(attributeId);
				session.save(diameterdicParamDetailData);
				session.flush();
				Logger.logDebug(MODULE, diameterdicParamDetailData);
				
			}
		    
		   for(int i=0;i<lstDiametrdicParamDetail.size();i++){
			   DiameterdicParamDetailData diameterdicParamData =lstDiametrdicParamDetail.get(i);
			   
			   if("DTT12".equalsIgnoreCase(diameterdicParamData.getDataTypeId())){
					  
					  String paramdetailId=diameterdicParamData.getDiameterdciParamDetailId();
					  String diameterdicId=dictionaryData.getDictionaryId();
					   if(diameterdicParamData.getFixedGroupedAttribute() != null){
						     
						     Map<String, AvpRule> fixedGroupedAttribute=diameterdicParamData.getFixedGroupedAttribute();
						     Iterator<String> itr=fixedGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=fixedGroupedAttribute.get(itr.next());
						           String attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
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
					   if(diameterdicParamData.getRequiredGroupedAttribute() != null){
						   
						   Map<String, AvpRule> requiredGroupedAttribute=diameterdicParamData.getRequiredGroupedAttribute();
						     Iterator<String> itr=requiredGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=requiredGroupedAttribute.get(itr.next());
						           String attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
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
					   if(diameterdicParamData.getOptionalGroupedAttribute() != null){
						   
						   Map<String, AvpRule> optionalGroupedAttribute=diameterdicParamData.getOptionalGroupedAttribute();
						     Iterator<String> itr=optionalGroupedAttribute.keySet().iterator();
						     
						      while(itr.hasNext()){
						    	   
						    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
						           AvpRule data=optionalGroupedAttribute.get(itr.next());
						           String attributeRuleId = getAttributeRuleId(data.getName(),session);
						           
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
		    
		    
		    
		    
		}catch (ConstraintViolationException hExp){
			if(hExp.getConstraintName().contains("UK_MDIAMETERDICPARAMETERDETAIL")){
    		   throw new ConstraintViolationException("Unique Key Constraint violated.Attribute Name "+diameterdicParamDetailData.getName()+" must be unique.", null,hExp.getConstraintName());
			}else {
				throw new ConstraintViolationException("ConstraintViolationException Exception :"+hExp.getMessage(), null,hExp.getConstraintName());
			}
		} catch (HibernateException hExp){
    		throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	private String getAttributeRuleId(String name, Session session) throws DataManagerException,AttributeNotFoundException {
         
		String attributeRuleId="0";
		try{
			 /*
			  *  for support of '*' in attributeRule set attributeRule id = -1[ vendor-id="*" and id="*" ]
			  */
			  if("attribute".equals(name)){
				   return null;
			  }else{
				  Criteria criteria=session.createCriteria(DiameterdicParamDetailData.class);
				  criteria.add(Restrictions.eq("name",name));
				  DiameterdicParamDetailData data=(DiameterdicParamDetailData) criteria.uniqueResult();
				  if(data == null){
					  throw new AttributeNotFoundException("Attribute "+name+" not found in Dictionary");
				  }
				  attributeRuleId=data.getDiameterdciParamDetailId();
			  }
			  
		 }catch(HibernateException hExp){
			 throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
			 throw new DataManagerException(exp.getMessage(), exp);
		 }
	 	
		return attributeRuleId;
	}

	public List<DiameterdicData> getAllList(DiameterdicData data) throws DataManagerException {
		List<DiameterdicData> dictionaryList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(DiameterdicData.class);
            
            if(data != null){
            	
            		
				if (Strings.isNullOrEmpty(data.getDictionaryId()) == false) {
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

	public List<DiameterdicParamDetailData> getDictionaryParameterDetailList(String dictionaryId) throws DataManagerException {
		List<DiameterdicParamDetailData> list=null;
		List<DiameterdicParamDetailData> resultList=new ArrayList<DiameterdicParamDetailData>();
		try{
			 Session session = getSession();
	         Criteria criteria = session.createCriteria(DiameterdicParamDetailData.class);
	         if(Strings.isNullOrEmpty(dictionaryId) == false){
	              criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).addOrder(Order.asc("vendorParameterId"));
	         }
	         list=criteria.list();
	         
	         for (Iterator iterator = list.iterator(); iterator.hasNext();) {
	        	 
				DiameterdicParamDetailData diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
				
				 if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){
					 
					 Map<String, AvpRule> fixedGroupedAttribute = new HashMap<String, AvpRule>();
					 Map<String, AvpRule> requiredGroupedAttribute = new HashMap<String, AvpRule>();
					 Map<String, AvpRule> optionalGroupedAttribute = new HashMap<String, AvpRule>();
					 
					 Criteria attrRuleCriteria=session.createCriteria(DiameterGroupedAttributeData.class);
					 attrRuleCriteria.add(Restrictions.eq("parameterDetailId",diameterdicParamDetailData.getDiameterdciParamDetailId()));
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
	
	

	public void updateDictionary(DiameterdicData serverData) throws DataManagerException {
	   
		try{
			
			Map<String,List<String>> groupAttrMap = new HashMap<String, List<String>>();
			String dictionaryId=serverData.getDictionaryId();
			Session session=getSession();
			Criteria criteria=session.createCriteria(DiameterdicData.class);
			criteria.add(Restrictions.eq("dictionaryId",serverData.getDictionaryId()));
			DiameterdicData data=(DiameterdicData) criteria.uniqueResult();
			/*
			 * update Diameter Dictionary Data Detail
			 */
			data.setDescription(serverData.getDescription());
			data.setApplicationName(serverData.getApplicationName());
			
		    session.update(data);
	        
	        Set<DiameterdicParamDetailData> diameterdicParamSet=serverData.getDiameterdicParamDetailSet();
	       
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
	        
          Set<DiameterdicParamDetailData> oldDiameterdicParamSet=data.getDiameterdicParamDetailSet();
	        
	        for (Iterator iterator = oldDiameterdicParamSet.iterator(); iterator.hasNext();) {
	        	
	        	DiameterdicParamDetailData diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
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
	        	List<String> grpInstanceIdList = new ArrayList<String>();
	        	for (Iterator iterator2 = groupedAttrList.iterator(); iterator2.hasNext();) {
					
	        		DiameterGroupedAttributeData diameterGroupedAttributeData = (DiameterGroupedAttributeData) iterator2.next();
	        		String groupedAttrId=diameterGroupedAttributeData.getGroupedAttrId();
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
	        
	        List<DiameterdicParamDetailData> lstDiametrdicParamDetail=new ArrayList<DiameterdicParamDetailData>(diameterdicParamSet);
		    for (Iterator iterator = lstDiametrdicParamDetail.iterator(); iterator.hasNext();) {
				
		    	DiameterdicParamDetailData diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
				diameterdicParamDetailData.setDictionaryId(dictionaryId);
				String attributeId=diameterdicParamDetailData.getVendorId()+":"+diameterdicParamDetailData.getVendorParameterId();
				diameterdicParamDetailData.setAttributeId(attributeId);
				session.save(diameterdicParamDetailData);
				session.flush();
				
			}

		    Iterator<String> itr=groupAttrMap.keySet().iterator();
		    
		    while(itr.hasNext()){
		          	
		    	String attrRuleName=itr.next();
		    	List<String> grpAttrIds=groupAttrMap.get(attrRuleName);
		    	Criteria diameterdicCriteria=session.createCriteria(DiameterdicParamDetailData.class);
		    	diameterdicCriteria.add(Restrictions.eq("name",attrRuleName));
		    	DiameterdicParamDetailData diameterDicParamData=(DiameterdicParamDetailData) diameterdicCriteria.uniqueResult();
   		    	if(diameterDicParamData==null)
   		    		throw new AttributeNotFoundException("Attribute "+attrRuleName+" not found in Dictionary");  
		    	
   		    	String diameterdicId=diameterDicParamData.getDiameterdciParamDetailId();
   		    	for(int i=0;i<grpAttrIds.size();i++){
   		    		
   		    		String grpAttrid=grpAttrIds.get(i);
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
				   DiameterdicParamDetailData diameterdicParamDetailData =lstDiametrdicParamDetail.get(i);
				   
				   if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){
						  
						  String paramdetailId=diameterdicParamDetailData.getDiameterdciParamDetailId();
						  String diameterdicId=serverData.getDictionaryId();
						   if(diameterdicParamDetailData.getFixedGroupedAttribute() != null){
							     
							     Map<String, AvpRule> fixedGroupedAttribute=diameterdicParamDetailData.getFixedGroupedAttribute();
							     Iterator<String> itr1=fixedGroupedAttribute.keySet().iterator();
							     
							      while(itr1.hasNext()){
							    	   
							    	   DiameterGroupedAttributeData diameterGroupedData= new DiameterGroupedAttributeData();
							           AvpRule data1=fixedGroupedAttribute.get(itr1.next());
							           String attributeRuleId = getAttributeRuleId(data1.getName(),session);
							           
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
							           String attributeRuleId = getAttributeRuleId(data2.getName(),session);
							           
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
							           String attributeRuleId = getAttributeRuleId(data2.getName(),session);
							           
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

	
	public void updateStatus(String dictionaryId, String commonStatusId,Timestamp statusChangeDate) throws DataManagerException 
	{
		Session session = getSession();
		DiameterdicData dictionaryData = null;
			
		try{
				Criteria criteria = session.createCriteria(DiameterdicData.class);
				dictionaryData = (DiameterdicData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).uniqueResult();

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

	public void delete(String dictionaryId) throws DataManagerException {
		
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
			 
			 Criteria paramCriteria=session.createCriteria(DiameterdicParamDetailData.class);
			 paramCriteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			 List<DiameterdicParamDetailData> attrList=paramCriteria.list();
			 deleteObjectList(attrList, session);
			 /*
			  * delete diameterdictionary
			  */
			 
			 Criteria dicCriteria=session.createCriteria(DiameterdicData.class);
			 dicCriteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			 DiameterdicData data=(DiameterdicData) dicCriteria.uniqueResult();
             session.delete(data);
             session.flush();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	public List<DiameterdicData> getOnlyDiameterDictionaryDataList() throws DataManagerException {
		List dictionaryParamList = null;
		List<DiameterdicData> tmpDictionaryParamList;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterdicData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryId"));
			proList.add(Projections.property("vendorName"));
			proList.add(Projections.property("commonStatusId"));
			proList.add(Projections.property("vendorId"));
			proList.add(Projections.property("applicationId"));
			
			criteria.setProjection(proList);
			dictionaryParamList = criteria.list();
			DiameterdicData dictionaryData;
			tmpDictionaryParamList = new ArrayList<DiameterdicData>();

			if(dictionaryParamList != null && dictionaryParamList.size() > 0) {
				Iterator dictionaryParamListItr = dictionaryParamList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryData = new DiameterdicData();
					dictionaryData.setDictionaryId((String)tmpDictionaryData[0]);
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

	public List<DiameterdicParamDetailData> getOnlyDiameterDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException {
		List<DiameterdicParamDetailData> dictionaryParamDetailList = null;
		List tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(DiameterdicParamDetailData.class);
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
			DiameterdicParamDetailData dictionaryParameterDetailData;
			dictionaryParamDetailList = new ArrayList<DiameterdicParamDetailData>();

			if(tmpDictionaryParamObjectList != null && tmpDictionaryParamObjectList.size() > 0) {
				Iterator dictionaryParamListItr = tmpDictionaryParamObjectList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryParameterDetailData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryParameterDetailData = new DiameterdicParamDetailData();
					dictionaryParameterDetailData.setDiameterdciParamDetailId((String)tmpDictionaryParameterDetailData[0]);
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


