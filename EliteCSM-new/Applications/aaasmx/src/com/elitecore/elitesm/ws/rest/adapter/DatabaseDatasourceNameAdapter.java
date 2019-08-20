package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

public class DatabaseDatasourceNameAdapter extends XmlAdapter<String, String>{

		@Override
		public String unmarshal(String databaseName) {

			String databaseId = null;
			if(Strings.isNullOrEmpty(databaseName) ==false){
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();

				try {
					if(Strings.isNullOrBlank(databaseName) == false){
						databaseId = databaseDSBLManager.getDatabaseIdFromName(databaseName);
					}
				}
				catch(Exception e){
					databaseId = RestValidationMessages.INVALID ;
				}
			}
			return databaseId;
		}
		
		@Override
		public String marshal(String databaseId){
			String databaseName = null;
			try {
				databaseName = EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseId);
			} catch(Exception e){
				return null;
			}
			return databaseName;
		}
	}