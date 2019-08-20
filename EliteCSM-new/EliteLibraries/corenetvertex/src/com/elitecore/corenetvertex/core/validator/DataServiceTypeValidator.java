package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

public class DataServiceTypeValidator implements Validator<DataServiceTypeData,Object,ResourceData> {

	private static final String MODULE = "DATA-SERVICE-TYPE-VALIDATOR";
	@Override
	public List<String> validate(DataServiceTypeData dataServiceTypeImported, Object parentObject, ResourceData pkgData, String action, SessionProvider session) {
		List<String> subReasons = new ArrayList<String>();
		try {
			if(Strings.isNullOrBlank(dataServiceTypeImported.getId()) && Strings.isNullOrBlank(dataServiceTypeImported.getName())){
				subReasons.add("Data Service type id or Data Service type name is mandatory");
				return subReasons;
			}
			String id = dataServiceTypeImported.getId();
			if (Strings.isNullOrBlank(id) == false) {
				isDataServiceTypeIdExists(dataServiceTypeImported, session, subReasons, id);
			} else {
				isDataServiceTypeNameExists(dataServiceTypeImported, session, subReasons);

			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to validate data service type with " + BasicValidations.printIdAndName(dataServiceTypeImported.getId(), dataServiceTypeImported.getName()));
			LogManager.getLogger().trace(MODULE, e);
			subReasons.add("Failed to validate data service type with " + BasicValidations.printIdAndName(dataServiceTypeImported.getId(), dataServiceTypeImported.getName()));
		}
		return subReasons;
	}


	private void isDataServiceTypeIdExists(DataServiceTypeData dataServiceTypeImported, SessionProvider session, List<String> subReasons, String id) throws Exception {
		if (Strings.isNullOrBlank(id) == false) {
			DataServiceTypeData existingDataServiceType = ImportExportCRUDOperationUtil.get(DataServiceTypeData.class, id, session);
			if (existingDataServiceType == null) {
				subReasons.add("Data Service Type with id: " + id + " does not exist");
			} else if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingDataServiceType.getStatus())) {
				subReasons.add("Data Service Type with id:" + id + " does not exist");
			} else {
				if (Strings.isNullOrBlank(dataServiceTypeImported.getName()) == false && dataServiceTypeImported.getName().equals(existingDataServiceType.getName()) == false) {
					subReasons.add("Data Service Type name: " + dataServiceTypeImported.getName() + " and data service type id:" + id + " are not related");
				}

			}
		}
	}
	private void isDataServiceTypeNameExists(DataServiceTypeData dataServiceTypeImported, SessionProvider session, List<String> subReasons) throws Exception {
		List<DataServiceTypeData> existingDataServiceType = ImportExportCRUDOperationUtil.getByName(DataServiceTypeData.class, dataServiceTypeImported.getName(),session);
		if (Collectionz.isNullOrEmpty(existingDataServiceType)) {
			subReasons.add("Data Service Type with name: " + dataServiceTypeImported.getName() + " does not exist");
		}else if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingDataServiceType.get(0).getStatus())){
			subReasons.add("Data Service Type with name:" + existingDataServiceType.get(0).getName() + " does not exist");
		}else{
			dataServiceTypeImported.setId(existingDataServiceType.get(0).getId());
		}

	}

}
