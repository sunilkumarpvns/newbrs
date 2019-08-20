package com.elitecore.core.commons.fileio.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.elitecore.core.systemx.esix.TaskScheduler;

public abstract class BaseFileStream {
	//rolling type
	public static final int TIME_BASED_ROLLING = 1;
	public static final int SIZE_BASED_ROLLING = 2;
	public static final int RECORD_BASED_ROLLING =3;
	
	//rolling unit
	public static final int TIME_BASED_ROLLING_EVERY_MINUTE = 3;
	public static final int TIME_BASED_ROLLING_EVERY_HOUR = 4;
	public static final int TIME_BASED_ROLLING_EVERY_DAY = 5; 
	public static final int RECORD_BASED_MAX_NUMBER_OF_RECORDS =100;
	
	protected TaskScheduler taskScheduler;
	protected List<Future<?>> taskScedulerFutureList = new ArrayList<Future<?>>();
	
	public void close(){
		for (Future<?> taskScedulerFutureTask : taskScedulerFutureList) {
			taskScedulerFutureTask.cancel(true);
		}
	}
}
