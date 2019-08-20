package com.elitecore.nvsmx.pd.controller.calender;

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
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.calender.CalenderDetails;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@ParentPackage(value = "pd")
@Namespace("/pd/calender")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "calender" }),

})
public class CalenderCTRL extends RestGenericCTRL<CalenderData> {

	private static final long serialVersionUID = 1026714750593896214L;
	private static final Predicate<CalenderDetails> PREDICATECALENDARDETAIL = calendarDetail -> {
		if (calendarDetail != null) {
			return !Strings.isNullOrBlank(calendarDetail.getCalenderName());
		} else {
			return false;
		}
	};

	@Override
	public ACLModules getModule() {
		return ACLModules.CALENDER;
	}

	@Override
	public CalenderData createModel() {
		return new CalenderData();
	}

	@Override
	public HttpHeaders create() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called create()");
		}
		CalenderData calenderData = (CalenderData) getModel();
		setCalendarDataToCalenderDetails(calenderData);
		return super.create();
	}

	@Override
	public HttpHeaders update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called update()");
		}
		CalenderData calendarData = (CalenderData) getModel();
		setCalendarDataToCalenderDetails(calendarData);
		return super.update();
	}

	private void setCalendarDataToCalenderDetails(CalenderData holidayData) {
		Collectionz.filter(holidayData.getCalenderDetails(), PREDICATECALENDARDETAIL);
		holidayData.getCalenderDetails().forEach(holidayDetails -> holidayDetails.setCalenderData(holidayData));
	}

	public String getCalenderDetailsDataAsJson() {
		Gson gson = GsonFactory.defaultInstance();
		CalenderData holidayData = (CalenderData) getModel();
		return gson.toJsonTree(holidayData.getCalenderDetails(), new TypeToken<List<CalenderDetails>>() {
		}.getType()).getAsJsonArray().toString();
	}
	

	@Override
	public void validate() {
		CalenderData calenderData = (CalenderData) getModel();
		boolean isAlreadyExist = isDuplicateEntity("calenderList", calenderData.getResourceName(), getMethodName());
		if (isAlreadyExist) {
			addFieldError("calenderList", getText("error.duplicate.calendarList"));
		} else {
			if (Collectionz.isNullOrEmpty(calenderData.getCalenderDetails())) {
				getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel()
						+ " information.Reason: At-least one File Mapping must be specified.");
				addActionError(getText("error.calender.calendardetail"));
			}
		}
	}
}
