package com.elitecore.elitesm.hibernate.diameter.prioritytable;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.PriorityTableConfDataManager;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HPriorityTableConfDataManager extends HBaseDataManager implements PriorityTableConfDataManager{

	private static final String ORDER_NUMBER = "orderNumber";


	@Override
	public List<PriorityTableData> getPriorityTableList() throws DataManagerException {
		
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(PriorityTableData.class).addOrder(Order.asc(ORDER_NUMBER));
			List<PriorityTableData> priorityTables = criteria.list();
			
			if(Collectionz.isNullOrEmpty(priorityTables) == false){
				return priorityTables;
			}
			
			return Collections.emptyList();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Priority Table, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Priority Table, Reason: " + e.getMessage(), e);
		}
	}

	
	@Override
	public void updatePriorityTable(List<PriorityTableData> priorityTables, IStaffData staffData) throws DataManagerException {
		
		try{
			Session session = getSession();
			
			List<PriorityTableData> priorityTableDatas =  getPriorityTableList();
	        
			JSONObject newJsonObject = listOfPriorityMappingEntriesToJSONObject(priorityTables);
			JSONObject oldJsonObject = listOfPriorityMappingEntriesToJSONObject(priorityTableDatas);
			
			JSONArray jsonArray = ObjectDiffer.diff(oldJsonObject, newJsonObject);
			
			if(Collectionz.isNullOrEmpty(priorityTableDatas) == false){
				for(PriorityTableData priorityTableData : priorityTableDatas){
					session.delete(priorityTableData);
				}
			}
			
			session.flush();
			
			if(Collectionz.isNullOrEmpty(priorityTables) == false){
				int orderNumber = 1;
				for(PriorityTableData priorityTable : priorityTables){
					priorityTable.setOrderNumber(orderNumber++);
					session.save(priorityTable);
				}
			}
			
			String auditId = getAuditId(ConfigConstant.PRIORITY_TABLE);
			staffData.setAuditId(auditId);
			staffData.setAuditName(ConfigConstant.HYPHEN);
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_PRIORITY_TABLE);
			session.flush();
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create/update Priority Table, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to create/update Priority Table, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to create/update Priority Table, Reason: " + e.getMessage(), e);
		}
	}

	private JSONObject listOfPriorityMappingEntriesToJSONObject(List<PriorityTableData> priorityTableDatas) {
		JSONObject jsonObject = new JSONObject();
		for(PriorityTableData data : priorityTableDatas) {
			jsonObject.put(data.getApplicationId(), data.toJson());
		}
		return jsonObject;
	}
}
