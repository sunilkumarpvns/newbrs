package com.elitecore.core.util.cli.cmd.logmonitorext.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;

public abstract class BaseMonitor<T,V> implements Monitor<T, V> {

	
	private Map<String, MonitorExpression<T,V>> expressions;
	
	public BaseMonitor(TaskScheduler taskScheduler){
		this.expressions = Collections.synchronizedMap(new LinkedHashMap<String, MonitorExpression<T,V>>(2,1));
		taskScheduler.scheduleIntervalBasedTask(new RemoveTimeoutConditionTask());
	}

	@Override
	public void add(String condition, long time) throws Exception {
		expressions.put(condition, createMonitorExpression(condition, time));
	}
	
	protected abstract MonitorExpression<T, V> createMonitorExpression(String condition, long time) throws Exception;

	@Override
	public String clear(String condition) {
		if(expressions.remove(condition) != null){
			return "Condition: " + condition + " removed successfully from " + getType() + " monitor";
		} else {
			return "No match found for condition: " + condition + " in " + getType() + " monitor";
		}
			
		
	}

	@Override
	public String clearAll() {
		expressions.clear();
		return "All condition removed successfully from " + getType() + " monitor";
	}

	@Override
	public String list() {
		return getViewOutput(); 
	}
	
	private String getViewOutput() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		
		
		if(expressions == null || expressions.isEmpty()){
			out.println("No expression added to "+ getType() +"monitor");
		} else {
			String[] header = {"Condition","Start Time","Duration(min)","Expiry Time"};
			int[] width = {43,30,17,30};
			TableFormatter formatter = new TableFormatter(header, width, TableFormatter.ALL_BORDER);
			for(MonitorExpression<T,V> expression : getExpressions()){
				String[] data = new String[4];
				data[0] = expression.getExpressionStr();
				data[1] = (new Date(expression.getStartTime())).toString();
				long time = expression.getExpiryTime();
				String expiryTime;
				if(time != Monitor.NO_TIME_LIMIT){
					data[2] = Long.toString(expression.getDuration());
					Calendar calender = Calendar.getInstance();
					calender.setTimeInMillis(expression.getExpiryTime());
					expiryTime = calender.getTime().toString();
				} else {
					data[2] = "NO TIME LIMIT";
					expiryTime = "NO TIME LIMIT";
				}
				data[3] = expiryTime;
				formatter.addRecord(data, new int [] {TableFormatter.LEFT,TableFormatter.CENTER,TableFormatter.CENTER,TableFormatter.CENTER});
			}
			
			out.println(formatter.getFormattedValues());
		}
		
		out.flush();
		out.close();
		return stringWriter.toString();
		
	}
	
	@Override	
	public Collection<MonitorExpression<T,V>> getExpressions(){
		return expressions.values();
	}
	
	@Override
	public String getHotkeyHelp(){
		StringWriter writer =new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		printWriter.print("'" + getType() +"':{");
		printWriter.print("}");
		return writer.toString();
	}
	
	
	private class RemoveTimeoutConditionTask extends BaseIntervalBasedTask{

		@Override
		public long getInterval() {
			return 10;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			if(expressions == null || expressions.isEmpty()){
				return;
			}
			
			long currentTimeMillis = System.currentTimeMillis();
			for(MonitorExpression<T,V> expression : expressions.values()){
				if(expression.getExpiryTime() != Monitor.NO_TIME_LIMIT  && expression.getExpiryTime() <= currentTimeMillis){
					expressions.remove(expression.getExpressionStr());
				}
			}
		}
		
		@Override
		public boolean isFixedDelay() {
			return true;
		}
		
	}

}
