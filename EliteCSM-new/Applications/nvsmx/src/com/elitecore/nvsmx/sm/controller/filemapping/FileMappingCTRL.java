package com.elitecore.nvsmx.sm.controller.filemapping;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.HttpHeaders;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingData;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingDetail;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Manage File Mapping related information. Created by Krishna.Seekarla on
 * 14/12/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/filemapping")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "file-mapping" }),

})
public class FileMappingCTRL extends RestGenericCTRL<FileMappingData> {

	private static final long serialVersionUID = -1575872926479112500L;

	private static final Predicate<FileMappingDetail> PREDICATEFILEMAPPINGDETAIL = fileMappingDetail -> {
		if (fileMappingDetail != null) {
			return !Strings.isNullOrBlank(fileMappingDetail.getSourceKey());
		} else {
			return false;
		}
	};

	@Override
	public ACLModules getModule() {
		return ACLModules.FILEMAPPING;
	}

	@Override
	public FileMappingData createModel() {
		return new FileMappingData();
	}

	@Override
	public HttpHeaders create() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called create()");
		}
		FileMappingData fileMappingData = (FileMappingData) getModel();
		setFileMappingDataToFileMappingDetails(fileMappingData);
		return super.create();
	}

	@Override
	public HttpHeaders update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called update()");
		}
		FileMappingData fileMappingData = (FileMappingData) getModel();
		setFileMappingDataToFileMappingDetails(fileMappingData);
		return super.update();
	}

	private void setFileMappingDataToFileMappingDetails(FileMappingData fileMappingData) {
		Collectionz.filter(fileMappingData.getFileMappingDetail(), PREDICATEFILEMAPPINGDETAIL);
		fileMappingData.getFileMappingDetail()
				.forEach(fileMappingDetail -> fileMappingDetail.setFileMappingData(fileMappingData));
	}

	public String getFileMappingDetailDataAsJson() {
		Gson gson = GsonFactory.defaultInstance();
		FileMappingData fileMappingData = (FileMappingData) getModel();
		return gson.toJsonTree(fileMappingData.getFileMappingDetail(), new TypeToken<List<FileMappingDetail>>() {
		}.getType()).getAsJsonArray().toString();
	}

	@Override
	public void validate() {
		FileMappingData fileMappingData = (FileMappingData) getModel();
		if (Collectionz.isNullOrEmpty(fileMappingData.getFileMappingDetail())) {
			getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel()
					+ " information.Reason: At-least one File Mapping must be specified.");
			addActionError(getText("error.filemapping.filemappingdetail"));
		}
		super.validate();
	}
}
