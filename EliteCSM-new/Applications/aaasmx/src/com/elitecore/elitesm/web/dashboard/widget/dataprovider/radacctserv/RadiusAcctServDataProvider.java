package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctserv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv.DefaultRadiusAcctServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv.RadiusAcctServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctserv.RadiusAcctServData;

public class RadiusAcctServDataProvider  extends BaseWidgetDataProvider {
	
	private static final String ID = "RadiusAcctServDataProvider";
	private RadiusAcctServDAO radiusAcctServDAO;
	
	public RadiusAcctServDataProvider() {
		radiusAcctServDAO = new DefaultRadiusAcctServDAO();
	}

	@Override
	public List<WidgetJSON>  getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		for(RadiusAcctServData radiusAcctServData : radiusAcctServDAO.getAuthServDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusAcctServData.getRadiusAccServIdent()));
			for(RadiusAcctServRowGroupHeaders rowGrpHeaders : RadiusAcctServRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusAcctServData.getRadiusAccServIdent();
				JsonData jsonData = new JsonData();
				jsonData.setId(id);
				jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAcctServData)));
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
			
		List<RadiusAcctServData> radiusAcctServDataList = radiusAcctServDAO.getAuthServDataList(2);
		if(radiusAcctServDataList.isEmpty()){
			System.out.println("No records Found");
			return null;
		}

		List<JsonData> jsonDataList = new ArrayList<JsonData>();

		for(RadiusAcctServData radiusAcctServData : radiusAcctServDataList) {
			for(RadiusAcctServRowGroupHeaders rowGrpHeaders : RadiusAcctServRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusAcctServData.getRadiusAccServIdent();
				JsonData jsonData = new JsonData();
				jsonData.setId(id);
				jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAcctServData)));
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

	@Override
	public String getName() {
		return ID;
	}
	
	private TableSchema createDefaultSchema() {
		TableSchema tableSchema = new TableSchema()
		.addColumnHeader("EliteAAA Server Instances")
		.addRowGroupHeaders("Counters");

		for(RadiusAcctServRowGroupHeaders rowGrpHeaders : RadiusAcctServRowGroupHeaders.values() ) {
			tableSchema.addTableData(new TableData().addTableData(rowGrpHeaders.getDisplayName()));
		}

		return tableSchema;
	}

}
enum RadiusAcctServRowGroupHeaders {
	DUP_REQUESTS("Duplicate Request") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getRadiusAccServTotalDupRequests();
		}
	},				
	SERVER_RESPONSE("Server Response") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getRadiusAccServTotalResponses();
		}
	},		
	SERVER_REQUEST("Server Request") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getRadiusAccServTotalRequests();
		}
	},		
	No_RECORDS("No Records") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getRadiusAccServTotalNoRecords();
		}
	},	
	MALFORMED_ACCESS_REQUESTS("Malformed Access Requests") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getTotalMalformedRequests();
		}
	}, 		
	BAD_AUTHENTICATORS("Bad Authenticators") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getTotalBadAuthenticators();
		}
	}, 			    
	PACKETS_DROPPED("Packets Dropped") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getTotalPacketsDropped();
		}
	},	
	UNKNOWNTYPES("UnknownTypes") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getRadiusAccServTotalUnknownTypes();
		}
	},
	INVALID_REQUEST("Invalid Request") {
		@Override
		public Long getValue(RadiusAcctServData radiusAcctServData) {
			return radiusAcctServData.getTotalInvalidRequests();
		}
	},
	;

	private String displayName;

	RadiusAcctServRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusAcctServData radiusAcctServData);
}
