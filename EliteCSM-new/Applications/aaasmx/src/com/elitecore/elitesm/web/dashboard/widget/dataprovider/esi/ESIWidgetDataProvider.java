package com.elitecore.elitesm.web.dashboard.widget.dataprovider.esi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.dao.esi.ESIWidgetDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.esi.ESIWidgetDAOImpl;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.esi.ESIWidgetData;

public class ESIWidgetDataProvider  {
	
	private TableSchema tableSchema = createDefaultTableSchema();
	private final static String [] tableDataColumns = {"Access Reject", "Access Challange", "Access Accept", "Request Drop"}; 
	private static final String ID = "ESI";
	private ESIWidgetDAO esiWidgetDAO;
	
	public ESIWidgetDataProvider() {
		this.esiWidgetDAO = new ESIWidgetDAOImpl();
		this.tableSchema = new TableSchema();
	}

	
	public String provideInitialData(String widgetId) {
		if(widgetId == null) {
			widgetId = ID;
		}
		JSONArray jsonArray = new JSONArray();
		Header header = new Header();
		header.setId(widgetId);
		header.setType("tableschema");
		
		tableSchema = new TableSchema()
		   				.addColumnHeader("EliteAAA Server Instances")
		   				.addRowGroupHeaders("Counters");
		for(String tableData : tableDataColumns) {
			tableSchema.addTableData(new TableData().addTableData(tableData));
		}
		
		List<JsonData> jsonDataList = new ArrayList<JsonData>(tableSchema.idList().size());
		for(ESIWidgetData esiWidgetData : esiWidgetDAO.getESIWidgetDataList()) {
			 tableSchema.addFirstRowData(Arrays.asList(esiWidgetData.getEsi()));
			 
			 String id = tableDataColumns[0] + "_"; // ACCESS Reject
			 id += esiWidgetData.getEsi();
			 JsonData jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessAccept());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[1] + "_"; // ACCESS Challange
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessChallange());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[2] + "_"; // ACCESS Accept
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessAccept());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[3] + "_"; // REQUETS DROP
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getRequestDrop());
			 jsonDataList.add(jsonData);
			 
			
		}
		
		WidgetJSON widgetJSON = new WidgetJSON(header, JSONObject.fromObject(tableSchema).toString());
		jsonArray.add(widgetJSON);
		
		/*Random random  = new Random();
		jsonDataList = new ArrayList<JsonData>(tableSchema.idList().size());
		for(String id : tableSchema.idList()) {
			JsonData jsonData = new JsonData();
			jsonData.setId(id);
			jsonData.setValue(String.valueOf(random.nextInt(2000)));
			jsonDataList.add(jsonData);
		}*/
		
		String jsonStr = JSONArray.fromObject(jsonDataList).toString();
		header = new Header();
		header.setId(widgetId);
		header.setType("tabledata");
		widgetJSON = new WidgetJSON(header, jsonStr);
		jsonArray.add(widgetJSON);
		return jsonArray.toString();
	}

	private TableSchema createDefaultTableSchema() {
		return new TableSchema()
				   .addColumnHeader("EliteAAA Server Instances")
				   .addRowGroupHeaders("Counters")
				   .addTableData(new TableData().addTableData("Access Request"))
				   .addTableData(new TableData().addTableData("Access Reject"))
				   .addTableData(new TableData().addTableData("Access Accept"))
				   .addTableData(new TableData().addTableData("Access Challange"))
				   .addFirstRowData(Arrays.asList("AAA-Delhi"))
				   .addFirstRowData(Arrays.asList("AAA-Mumbai"))
				   .addFirstRowData(Arrays.asList("AAA-Banglore"))
				   .addFirstRowData(Arrays.asList("AAA-Kolkatta"))
				   ;
		
	}
	
	

	
	public WidgetJSON provideData() {
		List<JsonData> jsonDataList = new ArrayList<JsonData>();
		List<ESIWidgetData> esiWidgetDataList = esiWidgetDAO.getESIWidgetDataList(1);
		if(esiWidgetDataList.isEmpty()) {
			System.out.println("No Records Found");
			return null;
		}
		
		for(ESIWidgetData esiWidgetData : esiWidgetDataList) {
			 tableSchema.addFirstRowData(Arrays.asList(esiWidgetData.getEsi()));
			 
			 String id = tableDataColumns[0] + "_"; // ACCESS Reject
			 id += esiWidgetData.getEsi();
			 JsonData jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessAccept());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[1] + "_"; // ACCESS Challange
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessChallange());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[2] + "_"; // ACCESS Accept
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getAccessAccept());
			 jsonDataList.add(jsonData);
			 
			 id = tableDataColumns[3] + "_"; // REQUETS DROP
			 id += esiWidgetData.getEsi();
			 jsonData = new JsonData();
			 jsonData.setId(id);
			 jsonData.setValue(esiWidgetData.getRequestDrop());
			 jsonDataList.add(jsonData);
			 
			
		}
		/*Random random = new Random();
		
		for(int i =0 ; i < 2 ; i++) {
			JsonData jsonData = new JsonData();
			jsonData.setId(tableSchema.idList().get(random.nextInt(tableSchema.idList().size())));
			jsonData.setValue(String.valueOf(random.nextInt(2000)));
			jsonDataList.add(jsonData);
		}*/
		String jsonStr = JSONArray.fromObject(jsonDataList).toString();
		Header header = new Header();
		header.setType("tabledata");
		WidgetJSON widgetJSON = new WidgetJSON(header, jsonStr);
		return widgetJSON;
	}
	
	
	public String getName() {
		return ID;
	}
	
	

}
