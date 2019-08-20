package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient.DefaultRadiusAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient.RadiusAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthclient.RadiusAuthClientData;

public class RadiusAuthClientDataProvider extends BaseWidgetDataProvider {
	
	private final static String ID = "RadiusAuthClient";
	private RadiusAuthClientDAO radiusAuthClientDAO;
	
	public RadiusAuthClientDataProvider() {
		this.radiusAuthClientDAO = new DefaultRadiusAuthClientDAO();
	}

	

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		
		for(RadiusAuthClientData radiusAuthClientData : radiusAuthClientDAO.getRadiusAuthClientDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusAuthClientData.getRadiusAuthClientAddress()));
			for(RadiusAuthClientRowGroupHeaders rowGrpHeaders : RadiusAuthClientRowGroupHeaders.values() ) {
				 String id = rowGrpHeaders.getDisplayName() + "_"; 
				 id += radiusAuthClientData.getRadiusAuthClientAddress();
				 JsonData jsonData = new JsonData();
				 jsonData.setId(id);
				 jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAuthClientData)));
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
			 
		List<RadiusAuthClientData> radiusAuthClientDataList = radiusAuthClientDAO.getRadiusAuthClientDataList(2);
		if(radiusAuthClientDataList.isEmpty()){
			System.out.println("No records Found");
			return null;
		} 
			
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
			
		for(RadiusAuthClientData radiusAuthClientData : radiusAuthClientDataList) {
			for(RadiusAuthClientRowGroupHeaders rowGrpHeaders : RadiusAuthClientRowGroupHeaders.values() ) {
				 String id = rowGrpHeaders.getDisplayName() + "_"; 
				 id += radiusAuthClientData.getRadiusAuthClientAddress();
				 JsonData jsonData = new JsonData();
				 jsonData.setId(id);
				 jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusAuthClientData)));
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
			
		System.out.println("JSON :"+widgetJSONList);
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
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServAccessRequests();
		}
	},	
	DUP_ACCESS_REQUESTS("Duplicate Access Request") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getDupAccessRequests();
		}
	},				
	ACCESS_ACCEPTS("Access Accept") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServAccessAccepts();
		}
	},		
	ACCESS_REJECTS("Access Rejects") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServAccessRejects();
		}
	},		
	ACCESS_CHALLENGES("Access Challenges") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServAccessChallenges();
		}
	},	
	MALFORMED_ACCESS_REQUESTS("Malformed Access Requests") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getMalformedAccessRequests();
		}
	}, 		
	BAD_AUTHENTICATORS("Bad Authenticators") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getBadAuthenticators();
		}
	}, 			    
	PACKETS_DROPPED("Packets Dropped") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServPacketsDropped();
		}
	},	
	UNKNOWNTYPES("UnknownTypes") {
		@Override
		public Long getValue(RadiusAuthClientData radiusAuthClientData) {
			return radiusAuthClientData.getRadiusAuthServUnknownTypes();
		}
	},
	;
	
	private String displayName;
	
	RadiusAuthClientRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusAuthClientData radiusAuthClientData);
}
