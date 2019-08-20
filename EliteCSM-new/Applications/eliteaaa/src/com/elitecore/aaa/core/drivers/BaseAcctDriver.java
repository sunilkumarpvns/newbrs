package com.elitecore.aaa.core.drivers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.DriverProcessTimeoutException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceRequest;

public abstract class BaseAcctDriver extends BaseDriver implements IEliteAcctDriver {

	public static final String MODULE = "BASE-ACCT-Drvr";
	public BaseAcctDriver(ServerContext serverContext) {
		super(serverContext);
	}

	@Override
	public final void handleAccountingRequest(ServiceRequest serviceRequest)throws DriverProcessFailedException {
		incrementTotalRequests();
		try{
			long requestSentTime = System.currentTimeMillis();
			handleServiceRequest(serviceRequest);
			long servingTime = System.currentTimeMillis() - requestSentTime;
			updateAverageResponseTime(servingTime);
			incrementTotalSuccess();
		}catch(DriverProcessTimeoutException ex){
			incrementTotalTimedoutResponses();
			if(ex.getTimeoutDuration() > 0){
				updateAverageResponseTime(ex.getTimeoutDuration());
			}
			throw ex;
		}catch (DriverProcessFailedException ex) {
			incrementTotalErrorResponses();
			throw ex;
		}
	}
	
	protected abstract void handleServiceRequest(ServiceRequest serviceRequest)throws DriverProcessFailedException;
	
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> setCDRSequenceObjectfromFile(String fileName){
		Map<String,Object> cdrSequenceCounterMap= new ConcurrentHashMap<String,Object>();
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE,"Reading CDR Sequence Number Details From File");
		FileInputStream fis;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			if( (cdrSequenceCounterMap = ((ConcurrentHashMap<String,Object>)ois.readObject())) == null) {
				cdrSequenceCounterMap = new ConcurrentHashMap<String,Object>();
			}
		} catch (FileNotFoundException e1) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,"CDR-Sequence File Not Found.");
		} catch (IOException ioExp) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,ioExp);
		} catch (ClassNotFoundException cnfExp) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,cnfExp);
		} finally {
			Closeables.closeQuietly(ois);
		}	
	return cdrSequenceCounterMap;
	}
	
	protected void serializeCounterMapInFile(String fileName, Map<String,Object>cdrCounterMap){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cdrCounterMap);
			oos.close();
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,"Error while writing the CDR sequencing information into the file. "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	@Override
	public String cdrflush(){		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "CDR Flush of Base is called");
		}
		return "CDR Flushed Successfully";
	}
}
