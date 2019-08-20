package com.elitecore.aaa.core.drivers;

import java.util.List;

import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.fileio.loactionalloactor.FileLocationAllocatorFactory;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public abstract class BaseDetailLocalAcctDriver extends BaseAcctDriver {
	private static String MODULE = "BASE-DETAIL-ACCT-DRIVER";
	public static final String INP_EXTENSION = "inp";
	private IFileLocationAllocater fileLocationAllocator;
	
	public BaseDetailLocalAcctDriver(ServerContext serverContext) {
		super(serverContext);
		// TODO Auto-generated constructor stub
	}
	
	public void setFileLocationAllocator(String fileAllcatorType, int rolloverTime, String user, String password, String address, String destinationLocation, int port, String postOperation,String folderSepretor,String archiveLocation,String originalExtension) throws FileAllocatorException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, " \n\t\tfileAllcatorType     : "+fileAllcatorType
																+"\n\t\tuserName 			 : "+user
																+"\n\t\tpassword 			 : "+"****"
																+"\n\t\tserverAddress 	     : "+address
																+"\n\t\tserverPort  		 : "+port
																+"\n\t\tdestinationLocation  : "+destinationLocation
																+"\n\t\tarchiveLocation : "+archiveLocation
																+"\n\t\tPostOperation  : "+postOperation);
		}
		
		
		if(password!=null && password.contains(":")){
			String[] encoded = password.split(":");
			if(encoded.length==2 && ("16".equalsIgnoreCase(encoded[0]) ||"32".equalsIgnoreCase(encoded[0]) ||"64".equalsIgnoreCase(encoded[0]))){
				try {
					password = PasswordEncryption.getInstance().decrypt(encoded[1], Integer.parseInt(encoded[0]));
				} catch (NumberFormatException e) {
					LogManager.getLogger().trace(MODULE, e);
				} catch (NoSuchEncryptionException e) {
					LogManager.getLogger().trace(MODULE, e);
				} catch (DecryptionNotSupportedException e) {
					LogManager.getLogger().trace(MODULE, e);
				} catch (DecryptionFailedException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		
		IFileLocationAllocater fileLocationAllocator = FileLocationAllocatorFactory.getInstance().getFileLocationAllocater(fileAllcatorType);
		fileLocationAllocator.initialize(user, password, address, destinationLocation, port, postOperation, folderSepretor, archiveLocation, originalExtension);
		
		if(!fileLocationAllocator.getPermission()){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error,Permission Not Available : "+destinationLocation);
		}
		this.fileLocationAllocator = fileLocationAllocator;
		
		//Scheduling Failover Task.
	//		FileLocationFailOverAttempt failOverAttempt = new FileLocationFailOverAttempt(getExtension()+"_REMOTE_LOCATOR_FAILOVER");
	//		failOverAttempt.setDriver(getDriverId());
	//		schedulreInternalTask(failOverAttempt , rolloverTime, rolloverTime);
	}
	
	
		
	protected IFileLocationAllocater getFileLocationAllocator(){
		return fileLocationAllocator;
	}
	
	public final String fillChar(String input, int length, char chr){

		if (input != null && input.length() >0){
			StringBuilder stringBuffer = new StringBuilder();
			for(int i = input.length(); i<=length; i++){
				stringBuffer.append(chr);
			}
			stringBuffer.append(input);
			return stringBuffer.toString();
		}
		return "";
	}


	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}
	
	@Override
	public void scan() {
		//No need of any implementation
	}

	protected abstract String getAvPairSeparator();
	protected abstract String getEventDateFormat(); 
	protected abstract List<AttributeRelation> getAttributeRelationList(); 
	protected abstract boolean getUseDictionaryValue();
}