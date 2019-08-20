package com.elitecore.aaa.core.drivers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.fileio.loactionalloactor.BaseCommonFileAllocator;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.fileio.loactionalloactor.FileLocationAllocatorFactory;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.commons.util.sequencer.SynchronizedSequencer;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import static com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration.PREFIX;


public abstract class BaseClassicCSVAcctDriver extends BaseAcctDriver {
	private static String MODULE = "BASE-CLASSIC-ACCT-DRIVER";
	public static final String INP_EXTENSION = "inp";
	private IFileLocationAllocater fileLocationAllocator;
	String strKey = null;
	public static final String GLOBAL_COUNTER="GLOBAL_COUNTER";
	
	
	public BaseClassicCSVAcctDriver(ServerContext serverContext) {
		super(serverContext);
	}
	
	public void setFileLocationAllocator(String fileAllcatorType, int rolloverTime, String user, String password, String address, String destinationLocation, int port, String postOperation,String folderSepretor,String archiveLocations,String originalExtension) throws FileAllocatorException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, " \n\t\tfileAllcatorType     : "+fileAllcatorType
																+"\n\t\tuserName 			 : "+user
																+"\n\t\tpassword 			 : "+"****"
																+"\n\t\tserverAddress 	     : "+address
																+"\n\t\tserverPort  		 : "+port
																+"\n\t\tdestinationLocation  : "+destinationLocation
																+"\n\t\tarchiveLocations : "+archiveLocations
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
		fileLocationAllocator.initialize(user, password, address, destinationLocation, port, postOperation, folderSepretor, archiveLocations, originalExtension);

		if(!fileLocationAllocator.getPermission()){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error,Permission Not Available : "+destinationLocation);
		}
		this.fileLocationAllocator = fileLocationAllocator;
	}
	
	protected IFileLocationAllocater getFileLocationAllocator(){
		return fileLocationAllocator;
	}
	
	protected final String fillChar(String input, int length, char chr){

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
	
	protected void changeFilesExtension(File file, String sourceExt, String destinationExt){
		if (file.isDirectory()) {
			try {
				File [] fileList = file.listFiles();
				if(fileList != null){
					for (int i=0;i<fileList.length;i++){
						changeFilesExtension(fileList[i],sourceExt,destinationExt);
					}
				}
			}catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE,"Unable to rename file "+file.getAbsolutePath());
			}
		} else {
			if(file.getName().endsWith("."+sourceExt) || file.getName().endsWith("."+BaseCommonFileAllocator.UIP_EXTENSION)){
				try {
					File newFile = null;
					String fileName =null;
					if(isSequencingEnabled()){
						ISequencer tempSequencer;
						
						if(getGlobalization() && getCdrSequenceCounterMap()!=null){
							tempSequencer = (ISequencer) getCdrSequenceCounterMap().get(GLOBAL_COUNTER);
							if(tempSequencer == null){
								tempSequencer = new SynchronizedSequencer(getStartSequence(), getEndSequence(), getPrefix(), getSuffix());
								tempSequencer.init();
								getCdrSequenceCounterMap().put(GLOBAL_COUNTER, tempSequencer);
							}
							String strSequence = tempSequencer.getSequence();
							tempSequencer.increment();
							
							if(getPattern().equalsIgnoreCase(PREFIX))
								fileName = strSequence + "_"+ file.getName().substring(0,file.getName().lastIndexOf("."));
							else
								fileName =file.getName().substring(0,file.getName().lastIndexOf(".")) + "_" + strSequence;
							fileName  =  fileName+"."+destinationExt;
						}else if(!getGlobalization() && getCdrSequenceCounterMap()!=null) {
							fileName =file.getName().substring(0,file.getName().lastIndexOf(getFileName().substring(0,getFileName().lastIndexOf('.'))));
							ISequencer localSequencer = (ISequencer) getCdrSequenceCounterMap().get(file.getParent()+ File.separator + fileName);
							if(localSequencer == null){
								localSequencer = new SynchronizedSequencer(getStartSequence(), getEndSequence(), getPrefix(), getSuffix());
								localSequencer.init();
								getCdrSequenceCounterMap().put(file.getParent() + File.separatorChar+ fileName, localSequencer);
							}else{
								tempSequencer = new SynchronizedSequencer(getStartSequence(), getEndSequence(), getPrefix(), getSuffix());;
								tempSequencer.init();
								if(!localSequencer.equals(tempSequencer)){
									localSequencer = (ISequencer) tempSequencer.clone();
								}
							}
							
							String strSequence = localSequencer.getSequence();
							localSequencer.increment();

							if(getPattern().equalsIgnoreCase(PREFIX))
								fileName = strSequence + "_"+file.getName().substring(0,file.getName().lastIndexOf("."));
							else
								fileName =file.getName().substring(0,file.getName().lastIndexOf(".")) + "_" + strSequence;
							fileName = fileName+"."+destinationExt;
						}
					}else{
						fileName = file.getName().substring(0,file.getName().lastIndexOf("."))+"."+destinationExt; 
					}
					if(file != null) {
						if(getFileLocationAllocator()!=null){
							file = getFileLocationAllocator().manageExtension(file, INP_EXTENSION, BaseCommonFileAllocator.UIP_EXTENSION,fileName);
							if(file != null){
								newFile = getFileLocationAllocator().transferFile(file); 
								if(newFile!=null) {
									if(!getFileLocationAllocator().postOperation(newFile)){
										if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
											LogManager.getLogger().error(MODULE,"Post Process for the Classic CSV files for FILE REMOTE ALLOCATION unsuccessfull, Extension changed to pof.");
										getFileLocationAllocator().manageExtension(newFile, BaseCommonFileAllocator.STAR_WILDCARD_CHARACTER, BaseCommonFileAllocator.POF_EXTENSION,fileName);//Extension Changed to Post Operation Failed(.pof) and leave on the same location.
										newFile.delete();
										if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
											LogManager.getLogger().error(MODULE,"Post Process Failed,file extension changed to pof (Post Operation Failed).");
									}else{
										if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
											LogManager.getLogger().trace(MODULE,"File uploded : "+newFile.getName());
									}
								}
							}else{
								if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
									LogManager.getLogger().error(MODULE,"Error in STARTING operation of REMOTE FILE ALLOCATION of file : "+fileName);
							}
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE,"Remote File Allocator for LOCAL");
							newFile = new File(file.getParent(),fileName);
							file.renameTo(newFile);//Normal Scenario. RLA==LOCAL
						}
					}
				}catch (FileAllocatorException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE,"Error in Transferring file, Reason : "+e.getMessage());
				} catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE,"Unable to rename file "+file.getAbsolutePath());
				}
			}
		}
	}
	
	public String getKey() {
		return this.strKey;
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}
	
	@Override
	public void scan() {
		//No need of any implementation
	}

	protected abstract String getDelimeterLast();
	protected abstract String getDelimeterFirst();
	protected abstract SimpleDateFormat getSimpleDateFormat();
	protected abstract String getHeader();
	protected abstract String getDelimeter();
	protected abstract boolean getGlobalization();
	protected abstract String getPattern();
	protected abstract String getFileName();
	protected abstract String getSequenceRange();
	protected abstract Map<String,Object> getCdrSequenceCounterMap();
	protected abstract ISequencer getCdrGlobalSequencer();
	protected abstract List<AttributesRelation> getAttributesRelationList();
	protected abstract String getPrefix();
	protected abstract String getSuffix();
	protected abstract boolean isSequencingEnabled();
	protected abstract String getStartSequence();
	protected abstract String getEndSequence();
	
}
