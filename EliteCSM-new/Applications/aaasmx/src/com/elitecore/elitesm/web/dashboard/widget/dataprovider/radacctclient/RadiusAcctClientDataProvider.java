package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.DefaultRadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.RadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctclient.RadiusAcctClientData;

public class RadiusAcctClientDataProvider  extends BaseWidgetDataProvider {

	private static final String ID = "RadiusAcctClientDataProvider";
	private RadiusAcctClientDAO radiusAcctClientDAO;


	public RadiusAcctClientDataProvider() {
		this.radiusAcctClientDAO = new DefaultRadiusAcctClientDAO();
	}

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		for(RadiusAcctClientData radiusAcctClientData : radiusAcctClientDAO.getRadiusAcctClientDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusAcctClientData.getRadiusAccClientAddress()));
			for(RadiusAcctClientRowGroupHeaders rowGrpHeaders : RadiusAcctClientRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusAcctClientData.getRadiusAccClientAddress();
				JsonData jsonData = new JsonData();
				jsonData.setId(id);
				jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAcctClientData)));
				jsonDataList.add(jsonData);
			}
		}
		List<WidgetJSON> widgetJSONList = new ArrayList<WidgetJSON>(2);

		Header header = new Header();
		header.setType("tableschema");
		WidgetJSON widgetJSON = new WidgetJSON(header, JSONObject.fromObject(tableSchema).toString());
		widgetJSONList.add(widgetJSON);

		if(!jsonDataList.isEmpty()) {
			String jsonStr = JSONArray.fromObject(jsonDataList).toString();
			header = new Header();
			header.setType("tabledata");
			widgetJSON = new WidgetJSON(header, jsonStr);
			widgetJSONList.add(widgetJSON);
		}


		return widgetJSONList;
	}

	@Override
	public List<WidgetJSON> getProvideData() throws Exception {

			List<RadiusAcctClientData> radiusAcctClientDataList = radiusAcctClientDAO.getRadiusAcctClientDataList(2);
			if(radiusAcctClientDataList.isEmpty()){
				System.out.println("No records Found");
				return null;
			}

			List<JsonData> jsonDataList = new ArrayList<JsonData>();

			for(RadiusAcctClientData radiusAcctClientData : radiusAcctClientDataList) {
				for(RadiusAcctClientRowGroupHeaders rowGrpHeaders : RadiusAcctClientRowGroupHeaders.values() ) {
					String id = rowGrpHeaders.getDisplayName() + "_"; 
					id += radiusAcctClientData.getRadiusAccClientAddress();
					JsonData jsonData = new JsonData();
					jsonData.setId(id);
					jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAcctClientData)));
					jsonDataList.add(jsonData);
				}
			}

			String jsonStr = JSONArray.fromObject(jsonDataList).toString();
			System.out.println("JSON STR : " + jsonStr);

			Header header = new Header();
			header.setType("tabledata");
			WidgetJSON widgetJSON = new WidgetJSON(header, jsonStr);

			List<WidgetJSON> widgetJSONList = new ArrayList<WidgetJSON>(1);
			widgetJSONList.add(widgetJSON);

			return widgetJSONList;
	}

	private TableSchema createDefaultSchema() {
		TableSchema tableSchema = new TableSchema()
		.addColumnHeader("EliteAAA Server Instances")
		.addRowGroupHeaders("Counters");

		for(RadiusAcctClientRowGroupHeaders rowGrpHeaders : RadiusAcctClientRowGroupHeaders.values() ) {
			tableSchema.addTableData(new TableData().addTableData(rowGrpHeaders.getDisplayName()));
		}

		return tableSchema;
	}

	@Override
	public String getName() {
		return ID;
	}

}

enum RadiusAcctClientRowGroupHeaders {
	DUP_REQUESTS("Duplicate Access Request") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServDupRequests();
		}
	},				
	SERVER_RESPONSE("Server Response") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServResponses();
		}
	},		
	SERVER_REQUEST("Server Request") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServRequests();
		}
	},		
	No_RECORDS("No Records") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServNoRecords();
		}
	},	
	MALFORMED_ACCESS_REQUESTS("Malformed Access Requests") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServMalformedRequests();
		}
	}, 		
	BAD_AUTHENTICATORS("Bad Authenticators") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServBadAuthenticators();
		}
	}, 			    
	PACKETS_DROPPED("Packets Dropped") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServPacketsDropped();
		}
	},	
	UNKNOWNTYPES("UnknownTypes") {
		@Override
		public Long getValue(RadiusAcctClientData radiusAcctClientData) {
			return radiusAcctClientData.getRadiusAccServUnknownTypes();
		}
	},
	;

	private String displayName;

	RadiusAcctClientRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusAcctClientData radiusAcctClientData);
}
