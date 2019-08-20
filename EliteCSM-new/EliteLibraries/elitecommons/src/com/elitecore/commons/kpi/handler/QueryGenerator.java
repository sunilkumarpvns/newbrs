package com.elitecore.commons.kpi.handler;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.kpi.data.ColumnData;
import com.elitecore.commons.kpi.data.TableData;

public class QueryGenerator {
	
	public Map<String, String> generateQuerys(String instanceId, Set<TableData> tables) {
		Map<String, String> querys = new HashMap<String, String>();
		
		for(TableData table : tables) {
			StringBuilder query = new StringBuilder();
			StringBuilder inParams = new StringBuilder();
			query.append("INSERT INTO " + table.getTableName() + "(INSTANCEID,CREATETIME,");
			inParams.append("?,");
			List<ColumnData> columns = table.getRows().get(0).getColumns();
			
			for(int columnIndex = 0 ; columnIndex < columns.size() ; columnIndex++) {
				if(columnIndex == columns.size()-1) {
					query.append(columns.get(columnIndex).getColumnName());
					inParams.append("?");
					break;
				}
				query.append(columns.get(columnIndex).getColumnName() + ",");
				inParams.append("?,");
			}
			
			query.append(") VALUES ('" + instanceId + "'," + inParams + ")");
			querys.put(table.getTableName(), query.toString());	
		}
		
		return querys;
	}
}
