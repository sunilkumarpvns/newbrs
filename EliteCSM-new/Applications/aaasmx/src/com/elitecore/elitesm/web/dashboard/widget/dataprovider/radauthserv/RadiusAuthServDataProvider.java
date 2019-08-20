package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthserv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv.DefaultRadiusAuthServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv.RadiusAuthServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthserv.RadiusAuthServData;

public class RadiusAuthServDataProvider extends BaseWidgetDataProvider {
	
	private final static String ID = "RadAuthServ";
	private RadiusAuthServDAO radiusAuthServDAO;
	
	public RadiusAuthServDataProvider() {
		this.radiusAuthServDAO = new DefaultRadiusAuthServDAO();
	}

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		for(RadiusAuthServData radiusAuthServData : radiusAuthServDAO.getAuthServDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusAuthServData.getRadiusAuthServIdent()));
			for(RadiusAuthClientRowGroupHeaders rowGrpHeaders : RadiusAuthClientRowGroupHeaders.values() ) {
				System.out.println("GROUP HEADER : " +rowGrpHeaders);
				 String id = rowGrpHeaders.getDisplayName() + "_"; 
				 id += radiusAuthServData.getRadiusAuthServIdent();
				 JsonData jsonData = new JsonData();
				 jsonData.setId(id);
				 jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAuthServData)));
				 jsonDataList.add(jsonData);
			}
		}
		List<WidgetJSON> widgetJSONList = new ArrayList<WidgetJSON>(2);
			
		Header header = new Header();
		header.setType("tableschema");
		WidgetJSON widgetJSON = new WidgetJSON(header, JSONObject.fromObject(tableSchema).toString());
		widgetJSONList.add(widgetJSON);
		
		if (!jsonDataList.isEmpty()) {
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
		List<RadiusAuthServData> radiusAuthServDataList = radiusAuthServDAO.getAuthServDataList(2);
		if(radiusAuthServDataList.isEmpty()){
			System.out.println("No records Found");
			return null;
		}

		List<JsonData> jsonDataList = new ArrayList<JsonData>();

		for(RadiusAuthServData radiusAuthServData : radiusAuthServDataList) {
			for(RadiusAuthClientRowGroupHeaders rowGrpHeaders : RadiusAuthClientRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusAuthServData.getRadiusAuthServIdent();
				JsonData jsonData = new JsonData();
				jsonData.setId(id);
				jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAuthServData)));
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

		for(RadiusAuthClientRowGroupHeaders rowGrpHeaders : RadiusAuthClientRowGroupHeaders.values() ) {
			tableSchema.addTableData(new TableData().addTableData(rowGrpHeaders.getDisplayName()));
		}
		
		return tableSchema;
	}

	@Override
	public String getName() {
		return ID;
	}

}

enum RadiusAuthClientRowGroupHeaders {
	ACCESS_REQUESTS("Access Requests") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalAccessRequests();
		}
	},	
	DUP_ACCESS_REQUESTS("Duplicate Access Request") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalDupAccessRequests();
		}
	},				
	ACCESS_ACCEPTS("Access Accept") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalAccessAccepts();
		}
	},		
	ACCESS_REJECTS("Access Rejects") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalAccessRejects();
		}
	},		
	ACCESS_CHALLENGES("Access Challenges") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalAccessChallenges();
		}
	},	
	MALFORMED_ACCESS_REQUESTS("Malformed Access Requests") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalMalformedAccessRequests();
		}
	}, 		
	BAD_AUTHENTICATORS("Bad Authenticators") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalBadAuthenticators();
		}
	}, 			    
	PACKETS_DROPPED("Packets Dropped") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalPacketsDropped();
		}
	},	
	UNKNOWNTYPES("UnknownTypes") {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalUnknownTypes();
		}
	},
	INVALID_REQUEST("Invalid Request" ) {
		@Override
		public Long getValue(RadiusAuthServData radiusAuthServData) {
			return radiusAuthServData.getTotalInvalidRequests();
		}
	}
	;
	
	private String displayName;
	
	RadiusAuthClientRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusAuthServData radiusAuthServData);
}

