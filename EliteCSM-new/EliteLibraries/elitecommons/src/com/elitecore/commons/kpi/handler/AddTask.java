package com.elitecore.commons.kpi.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.data.ColumnData;
import com.elitecore.commons.kpi.data.Entry;
import com.elitecore.commons.kpi.data.Row;
import com.elitecore.commons.kpi.data.TableData;
import com.elitecore.commons.kpi.data.Value;
import com.elitecore.commons.kpi.util.KpiUtil;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class AddTask extends BaseIntervalBasedTask {

	private static final String CREATETIME_COLUMN_NAME = "CREATETIME";
	private static final String MODULE = "ADD_TASK";
	private TableData table;
	private InsertData insertData;
	private KpiConfiguration kpiConfig;
	
	public AddTask(TableData table, InsertData insertData, KpiConfiguration kpiConfig) {
		this.table = table;
		this.insertData = insertData;
		this.kpiConfig = kpiConfig;
	}

	@Override
	public long getInterval() {
		return kpiConfig.getQueryInterval();
	}

	@Override
	public void execute() {
		
		if(table.getClientBasedTableFromAccessMethod() != null) {
			generateDynamicEntries();
		}

		if(insertData.isLimitReached()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Queue for entries is full for table: " + insertData.getTableName() + ", Reason: entries might not be dumping. So, skipping entries");
			return;
		}
		
		for(Row row : table.getRows()) {
			List<Value> values = new ArrayList<Value>();
			
			values.add(new Value(getCurrentTimeStamp(), CREATETIME_COLUMN_NAME, java.sql.Types.TIMESTAMP));

			for(ColumnData column : row.getColumns()) {
				values.add(new Value(column.getCurrentValue(), column.getColumnName(), column.getType()));
			}  
			insertData.addRecords(new Entry(values));
		}
	}

	private void generateDynamicEntries() {
		Object clientBasedTableFromAccessMethod = table.getClientBasedTableFromAccessMethod();
		
		generateRowsForAllClientEntries(clientBasedTableFromAccessMethod);
	}
	
	private void generateRowsForAllClientEntries(Object clientBasedTableFromAccessMethod) {
		Object[] clientBasedEntries = KpiUtil.getEntries(clientBasedTableFromAccessMethod);
		
		for(int entryIndex = 0 ; entryIndex < clientBasedEntries.length ; entryIndex++) {
			Row row = new Row(clientBasedEntries[entryIndex]);
		
			if(!table.contains(row)) {
				row.generateColumns();
				table.addRow(row);
			}
		}
	}
	
	private Timestamp getCurrentTimeStamp() {
		Calendar cal = Calendar.getInstance();
		return new Timestamp(cal.getTimeInMillis());
	}
}
