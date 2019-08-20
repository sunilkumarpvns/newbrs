package com.elitecore.nvsmx.policydesigner.controller.subscriber;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriptionInformation;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.JsonDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/*
 * This controller will retrieve data according to the criteria passed for the Subscriber Module.
 * This data will be rendered by dataTable on the search page. 
 * @author aneri.chavda
 */
public class SubscriberSearchCTRL extends ActionSupport implements
		ServletRequestAware, SessionAware {

	private static final String MODULE = "SUBS-SEARCH-CTRL";
	private static final long serialVersionUID = 1L;
	
	
	private HttpServletRequest request;
	private Map<String, Object> session;
	private List dataList;
	private List<SubscriptionInformation> subscriptionInformations;
	private int pages;

	private List<SPRInfo> deletedSubscriberList;
	private SubscriptionInformation subscriptionInformation;
	
	private int draw;
	private String recordsTotal;
	private String recordsFiltered;
	private String beanType;
	private int startIndex;
	private String data;
	private String rowData;

	// /TODO ANERI: Replace it later with a proper message
	private String errorMessage;

	public List<SPRInfo> getDeletedSubscriberList() {
		return deletedSubscriberList;
	}

	public void setDeletedSubscriberList(List<SPRInfo> deletedSubscriberList) {
		this.deletedSubscriberList = deletedSubscriberList;
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@SkipValidation
	public String searchSubscriber() {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE,
					"Method called searchSubscriber()");
		}

		loadParams();
		String criteria = request.getParameter("criteriaVal");

		try {
			 
			Gson gson = GsonFactory.defaultInstance();
			dataList = Collections.emptyList();
			if (Strings.isNullOrBlank(criteria) == false) {
				Map<String, String> criteriaMap = gson.fromJson(criteria, new TypeToken<Map<String, String>>() {}.getType());
				String subscriberIdentity = criteriaMap.get(Attributes.SUBSCRIBER_IDENTITY);
				String alternateIdField = criteriaMap.get(Attributes.ALTERNATE_ID_FIELD);
				if(Strings.isNullOrBlank(subscriberIdentity) && Strings.isNullOrBlank(alternateIdField)){
					return Results.SUBSCRIBER_SEARCH_SUCCESS.getValue();
				}
				try {
					List<SPRInfo> subscribers = Collectionz.newArrayList();
					if(Strings.isNullOrBlank(subscriberIdentity) == false){
						subscribers = SubscriberDAO.getInstance().searchSubscriber(subscriberIdentity, getStaffData());
					} else {
						if (Strings.isNullOrBlank(alternateIdField) == false) {
							subscribers =SubscriberDAO.getInstance().searchSubscriberByAlternateIdField(alternateIdField, getStaffData());
						}
					}
					dataList = subscribers;
				} catch (OperationFailedException e) {
					setErrorMessage("Database connection not found");
					getLogger().error(MODULE, "Error in searching subscriber profile. Reason: " + e.getMessage());
					if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
			}
			}

			int listSize = dataList.size();

			int totalFileterRecord = getStartIndex() + listSize;
			recordsFiltered = String.valueOf(totalFileterRecord);
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in searching subscriber profile. Reason: "	+ e.getMessage());
			request.setAttribute(Attributes.MESSAGE, e.getMessage());
			setErrorMessage(e.getMessage());
			addActionError(e.getMessage());
			dataList = Collections.emptyList();
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error in searching subscriber profile. Reason: "	+ ex.getMessage());
			getLogger().trace(MODULE, ex);

			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText("note.refer.logs"));
			return Results.ERROR.getValue();
		}

		return Results.SUBSCRIBER_SEARCH_SUCCESS.getValue();
	}
				
	public String subscriptionDetail(){
						if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called subscriptionDetail()");
						}
				
		try{
			String tableId = request.getParameter("tableId");
			String rowData = request.getParameter("rowData"+tableId);
			subscriptionInformation = new GsonBuilder().registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).registerTypeAdapter(Timestamp.class, new JsonDateDeserializer()).create().fromJson(rowData, SubscriptionInformation.class);
		}catch(Exception e){
			subscriptionInformation = null; 
			setErrorMessage("Error in getting Usage Information. Reason: " + e.getMessage());
			getLogger().error(MODULE, "Error in getting subscription information. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}
		return Results.SUBTABLE_SUCCESS.getValue();
	}

	public static String convertBytesToSuitableUnit(long bytes) {
		String bytesToSuitableUnit = bytes + " B";
		final double BYTE = 1, KB = BYTE * 1024, MB = KB * 1024, GB = MB * 1024;
		DecimalFormat threeDecimalForm = new DecimalFormat("#.###");

		if (bytes >= GB) {
			double tempBytes = bytes / GB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " GB";
			return bytesToSuitableUnit;
		}
		if (bytes >= MB) {
			double tempBytes = bytes / MB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " MB";
			return bytesToSuitableUnit;
		}
		if (bytes >= KB) {
			double tempBytes = bytes / KB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " KB";
			return bytesToSuitableUnit;
		}
		return bytesToSuitableUnit;
	}

	@SkipValidation
	public String searchDeletedSubscribers(){

		return Results.SEARCH_DELETED_SUBCRIBER.getValue();
	}
	
	public String searchAddOns() {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE,
					"Method called searchSubscriberAddOn()");
		}
		loadParams();

		try {
			Class beanType = Class.forName(getBeanType());
			dataList = Collections.emptyList();
			SPRInfo subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			if (subscriber != null) {
				dataList = SubscriberDAO.getInstance().getSubscribedAddOns(subscriber, getStaffData());
			}
			int listSize = dataList.size();
			int totalFileterRecord = getStartIndex() + listSize;
			recordsFiltered = String.valueOf(totalFileterRecord);
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in reading addOns for subscriber. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			request.setAttribute(Attributes.MESSAGE, e.getMessage());
			addActionError(e.getMessage());
			dataList=Collections.emptyList();
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error in reading addOns for subscriber. Reason: "	+ ex.getMessage());
			getLogger().trace(MODULE, ex);

			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText("note.refer.logs"));
			return Results.ERROR.getValue();
		}

		return Results.SEARCH_ADDON_SUCCESS.getValue();
				
	}
	
	private void loadParams() {

		String drawParam = request.getParameter("draw");
		String startParam = request.getParameter("start");
		String lengthParam = request.getParameter("length");

		String sortColumnOrder = request.getParameter("sortColumnOrder");
		String sortColumnName = request.getParameter("sortColumnName");
		String cachedPages = request.getParameter("pages");
		if(Strings.isNullOrBlank(cachedPages)){
			pages = -1;
		}else{
			pages = Integer.parseInt(cachedPages);
		}

		int rows = ConfigurationProvider.getInstance().getPageRowSize();

		int drawTemp = Numbers.parseInt(drawParam, 0);
		int maxRecords = Numbers.parseInt(lengthParam, rows * pages);
		startIndex = Numbers.parseInt(startParam, 0);
		draw = drawTemp;
		recordsTotal = String.valueOf(maxRecords);

	}

	private int getStartIndex() {
		return startIndex;
	}

	private void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(String recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public String getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(String recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public String getBeanType() {
		return beanType;
	}

	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getRowData() {
		return rowData;
	}

	public void setRowData(String rowData) {
		this.rowData = rowData;
	}
	
	private StaffData getStaffData() {
		return (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
	}

	public List<SubscriptionInformation> getSubscriptionInformations() {
		return subscriptionInformations;
	}

	public void setSubscriptionInformations(List<SubscriptionInformation> subscriptionInformations) {
		this.subscriptionInformations = subscriptionInformations;
	}

	public SubscriptionInformation getSubscriptionInformation() {
		return subscriptionInformation;
	}

	public void setSubscriptionInformation(SubscriptionInformation subscriptionInformation) {
		this.subscriptionInformation = subscriptionInformation;
	}

	@SkipValidation
	public String searchDeletedSubscriber(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called searchDeletedSubscriber()");
		}
		
		loadParams();
		try {
			String criteria = request.getParameter(Attributes.CRITERIA_VAL);
			Gson gson = GsonFactory.defaultInstance();
			Map<String, String> criteriaMap = gson.fromJson(criteria, new TypeToken<Map<String, String>>() {}.getType());

			if(Maps.isNullOrEmpty(criteriaMap) || Strings.isNullOrBlank(criteriaMap.get(Attributes.SUBSCRIBER_IDENTITY))) {
				dataList = SubscriberDAO.getInstance().getDeleteMarkedProfiles(getStaffData());

			}else {
				dataList = new ArrayList<SPRInfo>();
				SPRInfo deletedSubscriber = SubscriberDAO.getInstance().getDeleteMarkedProfile(criteriaMap.get(Attributes.SUBSCRIBER_IDENTITY), getStaffData());
				if(deletedSubscriber != null)
					dataList.add(deletedSubscriber);
			}

			int listSize = dataList.size();

			int totalFileterRecord = getStartIndex() + listSize;
			recordsFiltered = String.valueOf(totalFileterRecord);

		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in searching deleted subscribers. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			request.setAttribute(Attributes.MESSAGE, e.getMessage());
			addActionError(e.getMessage());
			dataList = Collections.emptyList();
		}catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error in searching deleted subscribers. Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			addActionError(e.getMessage());
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in searching deleted subscribers. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			addActionError(getText("note.refer.logs"));
			return Results.LIST.getValue();
		}

		return Results.SUBSCRIBER_SEARCH_SUCCESS.getValue();
	}
	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}

