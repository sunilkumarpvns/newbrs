package com.elitecore.elitesm.web.dashboard.widget.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.JsonData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableData;
import com.elitecore.elitesm.web.dashboard.widget.json.TableSchema;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

public class PeerWidgetDataProvider {
	private TableSchema tableSchema = createDefaultTableSchema();
	private static final String ID = "PEER";
	
	public String provideInitialData(String widgetId) {
		JSONArray jsonArray = new JSONArray();
		Header header = new Header();
		header.setId(ID);
		header.setType("tableschema");
		WidgetJSON widgetJSON = new WidgetJSON(header, JSONObject.fromObject(tableSchema).toString());
		jsonArray.add(widgetJSON);
		
		Random random = new Random();
		List<JsonData> jsonDataList = new ArrayList<JsonData>(tableSchema.idList().size());
		for(String id : tableSchema.idList()) {
			JsonData jsonData = new JsonData();
			jsonData.setId(id);
			jsonData.setValue(String.valueOf(random.nextInt(2000)));
			jsonDataList.add(jsonData);
		}
		
		String jsonStr = JSONArray.fromObject(jsonDataList).toString();
		header = new Header();
		header.setId(ID);
		header.setType("tabledata");
		widgetJSON = new WidgetJSON(header, jsonStr);
		jsonArray.add(widgetJSON);
		return jsonArray.toString();
	}

	public WidgetJSON provideData() {
		Random random = new Random();
		List<JsonData> jsonDataList = new ArrayList<JsonData>(3);
		for(int i =0 ; i < 2 ; i++) {
			JsonData jsonData = new JsonData();
			jsonData.setId(tableSchema.idList().get(random.nextInt(tableSchema.idList().size())));
			jsonData.setValue(String.valueOf(random.nextInt(2000)));
			jsonDataList.add(jsonData);
		}
		String jsonStr = JSONArray.fromObject(jsonDataList).toString();
		Header header = new Header();
		header.setId(ID);
		header.setType("tabledata");
		WidgetJSON widgetJSON = new WidgetJSON(header, jsonStr);
		return widgetJSON;
	}
	
	private TableSchema createDefaultTableSchema() {
		return new TableSchema()
				   .addColumnHeader("Counter Details")
				   .addRowGroupHeaders("Peer")
				   .addRowGroupHeaders("CMD")
				   .addTableData(new TableData().addTableData("dsc1.eliteaaa.com").addTableData("CE"))
				   .addTableData(new TableData().addTableData("dsc1.eliteaaa.com").addTableData("CC"))
				   .addTableData(new TableData().addTableData("dsc2.eliteaaa.com").addTableData("CE"))
				   .addTableData(new TableData().addTableData("dsc2.eliteaaa.com").addTableData("CC"))
				   .addFirstRowData(Arrays.asList("Request, Rx".split(",")))
				   .addFirstRowData(Arrays.asList("Request, Rt".split(",")))
				   .addFirstRowData(Arrays.asList("Answer, Rx".split(",")))
				   .addFirstRowData(Arrays.asList("Request, RR".split(",")))
				   ;
		
	}

	public String getName() {
		return ID;
	}
	

}
