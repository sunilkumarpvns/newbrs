package com.elitecore.elitesm.web.dashboard.widget.dataprovider.dynaauthclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.DefaultRadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.RadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.DefaultRadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.RadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient.RadiusDynaAuthClientData;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctclient.RadiusAcctClientData;

public class RadiusDynaAuthClientDataProvider  extends BaseWidgetDataProvider {

	private static final String ID = "RadiusDynaAuthClientDataProvider";
	private RadiusDynaAuthClientDAO radiusDynaAuthClientDAO;


	public RadiusDynaAuthClientDataProvider() {
		this.radiusDynaAuthClientDAO = new DefaultRadiusDynaAuthClientDAO();
	}

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		for(RadiusDynaAuthClientData radiusDynaAuthClientData : radiusDynaAuthClientDAO.getRadiusDynaAuthClientDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusDynaAuthClientData.getRadiusDynAuthClientAddress()));
			for(RadiusDynaAuthClientRowGroupHeaders rowGrpHeaders : RadiusDynaAuthClientRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusDynaAuthClientData.getRadiusDynAuthClientAddress();
				JsonData jsonData = new JsonData();
				jsonData.setId(id);
				jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusDynaAuthClientData)));
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

			List<RadiusDynaAuthClientData> radiusDynaAuthClientDataList = radiusDynaAuthClientDAO.getRadiusDynaAuthClientDataList(2);
			if(radiusDynaAuthClientDataList.isEmpty()){
				System.out.println("No records Found");
				return null;
			}

			List<JsonData> jsonDataList = new ArrayList<JsonData>();

			for(RadiusDynaAuthClientData radiusDynaAuthClientData : radiusDynaAuthClientDataList) {
				for(RadiusDynaAuthClientRowGroupHeaders rowGrpHeaders : RadiusDynaAuthClientRowGroupHeaders.values() ) {
					String id = rowGrpHeaders.getDisplayName() + "_"; 
					id += radiusDynaAuthClientData.getRadiusDynAuthClientAddress();
					JsonData jsonData = new JsonData();
					jsonData.setId(id);
					jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusDynaAuthClientData)));
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
		.addColumnHeader("Clients")
		.addRowGroupHeaders("Counters");

		for(RadiusDynaAuthClientRowGroupHeaders rowGrpHeaders : RadiusDynaAuthClientRowGroupHeaders.values() ) {
			tableSchema.addTableData(new TableData().addTableData(rowGrpHeaders.getDisplayName()));
		}

		return tableSchema;
	}

	@Override
	public String getName() {
		return ID;
	}

}

enum RadiusDynaAuthClientRowGroupHeaders {
	COA_REQUESTS("COA-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServCoARequests();
		}
	},				
	DISCON_REQUEST("DISCON-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServDisconRequests();
		}
	},		
	COA_ACK("COA-Ack") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServCoAAcks();
		}
	},		
	COA_NCK("COA-Nck") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServCoANaks();
		}
	},	
	DISCON_ACK("DISCON-Ack") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServDisconAcks();
		}
	}, 		
	DISCON_NCK("DISCON-Nck") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServDisconNaks();
		}
	}, 			    
	COA_REQUEST_DROPPED("COA-Request Dropped") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServCoAPacketsDropped();
		}
	},	
	DISCONNECT_REQUEST_DROP("DISCON-Request Dropped") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServDisconPacketsDropped();
		}
	},	
	DUPLICATE_COA_REQUEST("Duplicate COA-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServDupCoARequests();
		}
	},
	DUPLICATE_DISCONECT_REQUEST("Duplicate DISCON-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServDupDisconRequests();
		}
	},
	UNKNOWN_TYPE_REQUEST("Unknown Type Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getRadiusDynAuthServUnknownTypes();
		}
	},
	MALFORMED_COA_REQUEST("Malformed COA-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServMalformedCoARequests();
		}
	},
	MALFORMED_DISCON_REQUEST("Malformed DISCON-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServMalformedDisconRequests();
		}
	},
	BAD_AUTHENTICATOR_COA_REQUEST("Bad Authenticator COA-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServCoABadAuthenticators();
		}
	},
	BAD_AUTHENTICATOR_DISCON_REQUEST("Bad Authenticator DISCON-Request") {
		@Override
		public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData) {
			return radiusDynaAuthClientData.getServDisconBadAuthenticators();
		}
	};

	private String displayName;

	RadiusDynaAuthClientRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusDynaAuthClientData radiusDynaAuthClientData);
}
