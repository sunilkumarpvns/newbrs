package com.elitecore.elitesm.web.dashboard.widget.dataprovider.dynaauthserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.DefaultRadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.RadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver.DefaultRadiusDynaAuthServerDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver.RadiusDynaAuthServerDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient.RadiusDynaAuthClientData;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthserver.RadiusDynaAuthServerData;

public class RadiusDynaAuthServerDataProvider  extends BaseWidgetDataProvider {

	private static final String ID = "RadiusDynaAuthServerDataProvider";
	private RadiusDynaAuthServerDAO radiusDynaAuthServerDAO;


	public RadiusDynaAuthServerDataProvider() {
		this.radiusDynaAuthServerDAO = new DefaultRadiusDynaAuthServerDAO();
	}

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		TableSchema tableSchema = createDefaultSchema();
		for(RadiusDynaAuthServerData radiusDynaAuthClientData : radiusDynaAuthServerDAO.getRadiusDynaAuthServerDataList(serverKey)) {
			tableSchema.addFirstRowData(Arrays.asList(radiusDynaAuthClientData.getDynauthservername()));
			for(RadiusDynaAuthServerRowGroupHeaders rowGrpHeaders : RadiusDynaAuthServerRowGroupHeaders.values() ) {
				String id = rowGrpHeaders.getDisplayName() + "_"; 
				id += radiusDynaAuthClientData.getDynauthservername();
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

			List<RadiusDynaAuthServerData> radiusDynaAuthClientDataList = radiusDynaAuthServerDAO.getRadiusDynaAuthServerDataList(2);
			if(radiusDynaAuthClientDataList.isEmpty()){
				System.out.println("No records Found");
				return null;
			}

			List<JsonData> jsonDataList = new ArrayList<JsonData>();

			for(RadiusDynaAuthServerData radiusDynaAuthServerData : radiusDynaAuthClientDataList) {
				for(RadiusDynaAuthServerRowGroupHeaders rowGrpHeaders : RadiusDynaAuthServerRowGroupHeaders.values() ) {
					String id = rowGrpHeaders.getDisplayName() + "_"; 
					id += radiusDynaAuthServerData.getDynauthservername();
					JsonData jsonData = new JsonData();
					jsonData.setId(id);
					jsonData.setValue(String.valueOf(rowGrpHeaders.getValue(radiusDynaAuthServerData)));
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
		.addColumnHeader("Servers")
		.addRowGroupHeaders("Counters");

		for(RadiusDynaAuthServerRowGroupHeaders rowGrpHeaders : RadiusDynaAuthServerRowGroupHeaders.values() ) {
			tableSchema.addTableData(new TableData().addTableData(rowGrpHeaders.getDisplayName()));
		}

		return tableSchema;
	}

	@Override
	public String getName() {
		return ID;
	}

}

enum RadiusDynaAuthServerRowGroupHeaders {
	ROUND_TRIPTIME("Round Trip Time") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientRoundTripTime();
		}
	},				
	DISCON_REQUEST("Disconnect Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconRequests();
		}
	},		
	DIS_AUTH_ONLY_REQ("Disconnect Auth-Only Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconAuthOnlyRequests();
		}
	},		
	DISCONNECT_RETRANSMISSION_REQ("Disconnect Retransmissions Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconRetransmissions();
		}
	},	
	RADIUS_DYNAUTH_CLIENT_DISCO_ACKS("Disconnect Acks") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientDisconAcks();
		}
	}, 	
	RADIUS_DYNAUTH_CLIENT_DISCO_NACKS("Disconnect Naks") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientDisconNaks();
		}
	}, 
	DISCON_NCK_AUTHONLY_REQ("Disconnect Nak AuthOnly Request") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconNakAuthOnlyRequest();
		}
	}, 			    
	CLIENT_DISCON_NAK_SESSNO_CONTEXT("Disconnect Nak Session Not Exist Request") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconNakSessNoContext();
		}
	},	
	CLIENT_MALFORMED_DISCON_RESPONSES("Malformed Disconnect Responses") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientMalformedDisconResponses();
		}
	},	
	CLIENT_DISCON_BADAUTHENTICATORS("BadAuthenticators Disconnect Responses") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconBadAuthenticators();
		}
	},
	CLIENT_DISCON_PACKETS_DROPPED("Disconnect Packets Dropped") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconPacketsDropped();
		}
	},
	CLIENT_DISCON_PENDING_REQUESTS("Disconnect Pending Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconPendingRequests();
		}
	},
	CLIENT_DISCON_TIMEOUTS("Disconnect Timeouts") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientDisconTimeouts();
		}
	},
	RADIUS_DYNAUTHCLIENT_COAREQUESTS("CoA Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientCoARequests();
		}
	},
	CLIENT_COA_AUTHONLY_REQUEST("CoA Auth-Only Request") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoAAuthOnlyRequest();
		}
	},
	CLIENT_COA_RETRANSMISSIONS("CoA Retransmissions") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoARetransmissions();
		}
	},
	RADIUS_DYNAUTH_CLIENT_COA_ACKS("CoA Acks") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientCoAAcks();
		}
	},
	RADIUS_DYNAUTH_CLIENT_COA_NACKS("CoA Naks") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientCoANaks();
		}
	},
	CLIENT_COA_NAK_AUTHONLY_REQUEST("CoA Nak Auth-Only Request") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoANakAuthOnlyRequest();
		}
	},
	CLIENT_COA_NAK_SESSNO_CONTEXT("CoA Nak Session Not Exist Request") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoANakAuthOnlyRequest();
		}
	},
	CLIENT_MALFORMED_COA_RESPONSES("Malformed CoA Responses") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientMalformedCoAResponses();
		}
	},
	CLIENT_COA_BAD_AUTHENTICATORS("CoA BadAuthenticators") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoABadAuthenticators();
		}
	},
	CLIENT_COA_PENDING_REQUESTS("CoA Pending Requests") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoAPendingRequests();
		}
	},
	RADIUS_DYNAUTH_CLIENT_COA_TIMEOUTS("CoA Timeouts") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getRadiusDynAuthClientCoATimeouts();
		}
	},
	CLIENT_COA_PACKETS_DROPPED("CoA PacketsDropped") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientCoAPacketsDropped();
		}
	},
	CLIENT_UNKNOWN_TYPES("Unknown Types") {
		@Override
		public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData) {
			return radiusDynaAuthServerData.getClientUnknownTypes();
		}
	};

	private String displayName;

	RadiusDynaAuthServerRowGroupHeaders(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	abstract public Long getValue(RadiusDynaAuthServerData radiusDynaAuthServerData);
}
