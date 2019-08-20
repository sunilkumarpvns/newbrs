package com.elitecore.elitesm.hibernate.inmemorydatagrid;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.InMemoryDataGridDataManager;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.ConfigConstant;

import net.sf.json.JSONArray;

public class HInMemoryDataGridDataManager extends HBaseDataManager implements InMemoryDataGridDataManager{

	@Override
	public void update(InMemoryDataGridData inMemoryDataGridData, IStaffData staffData) throws DataManagerException {
		EliteAssert.notNull(inMemoryDataGridData,"In Memory Data Grid must not be null.");
		try{
			Session session = getSession();
			
			InMemoryDataGridData inMemoryDataGridDataObj = null;
			
			if( Strings.isNullOrBlank(inMemoryDataGridData.getImdgID()) == false ){
				Criteria criteria = session.createCriteria(InMemoryDataGridData.class).add(Restrictions.eq("imdgID", inMemoryDataGridData.getImdgID()));
				inMemoryDataGridDataObj = (InMemoryDataGridData)criteria.uniqueResult();
			}
			
			if( inMemoryDataGridDataObj == null ){
				
				inMemoryDataGridDataObj = new InMemoryDataGridData();
				inMemoryDataGridDataObj.setImdgXml(inMemoryDataGridData.getImdgXml());
				inMemoryDataGridDataObj.setAuditUId(UUIDGenerator.generate());
				
				session.save(inMemoryDataGridData);
				session.flush();
	
			}else{
				
				JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				
				String xmlDatas = new String(inMemoryDataGridData.getImdgXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());
				ImdgConfigData newIMDGConfigData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);
				
				String xmlDatasOld = new String(inMemoryDataGridDataObj.getImdgXml());
				StringReader stringReaderOld =new StringReader(xmlDatasOld.trim());
				ImdgConfigData oldIMDGConfigData = (ImdgConfigData) unmarshaller.unmarshal(stringReaderOld);
				
				JSONArray jsonArray = ObjectDiffer.diff(oldIMDGConfigData, newIMDGConfigData);
				
				inMemoryDataGridDataObj.setImdgXml(inMemoryDataGridData.getImdgXml());
				
				if (Strings.isNullOrBlank(inMemoryDataGridData.getAuditUId())) {
					inMemoryDataGridDataObj.setAuditUId(UUIDGenerator.generate());
				} else {
					inMemoryDataGridDataObj.setAuditUId(inMemoryDataGridDataObj.getAuditUId());
				}
				
				session.update(inMemoryDataGridDataObj);
				session.flush();
				
				staffData.setAuditId(inMemoryDataGridData.getAuditUId());
				staffData.setAuditName("In-Memory Data Grid");
				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_ACCESS_GROUP_ACTION);
				
			}
			
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to update In Memory Data Grid, Reason:" + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to update In Memory Data Grid, Reason: " + exp.getMessage(),exp);
		}
	}

	@Override
	public InMemoryDataGridData search() throws DataManagerException {
		InMemoryDataGridData inMemoryDataGridData = new InMemoryDataGridData();
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(InMemoryDataGridData.class);
			inMemoryDataGridData = (InMemoryDataGridData) criteria.uniqueResult();

		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to retrive In Memory Data Grid Data, Reason : " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to retrive In Memory Data Grid Data, Reason : " + exp.getMessage(), exp);
		}
		return inMemoryDataGridData;
	}

	@Override
	public InMemoryDataGridData getInMemoryDataGridConfiguration() throws DataManagerException {

		InMemoryDataGridData inMemoryDataGridData = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(InMemoryDataGridData.class);
			inMemoryDataGridData = (InMemoryDataGridData) criteria.uniqueResult();

		} catch (HibernateException hExp) {
			throw new DataManagerException("Failed to retrive In Memory Data Grid configuration, Reason : " + hExp.getMessage(), hExp);
		}
		return inMemoryDataGridData;
	}

}