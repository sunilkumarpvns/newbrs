package com.elitecore.netvertexsm.util.remotecommunications;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.ws.logger.Logger;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class RemoteMethodInvocator {
	private static final String  MODULE=RemoteMethodInvocator.class.getSimpleName();
	private static  NetServerBLManager netServerBLManager=null;
	private static RemoteMethodInvocator remoteMethodInvocator;
	static{
		remoteMethodInvocator = new RemoteMethodInvocator();
	}
	
	public static RemoteMethodInvocator getInstance(){
		return remoteMethodInvocator;
	}
 
	/**To execute remote method(Mbean Call) on NetServerInstance passed as a paramenter.
	 * @param netServerInstanceData
	 * @param objectName
	 * @param methodName
	 * @param parameter
	 * @param parameterTypes
	 * @return Object 
	 */
	public static Object invokeRemoteMethod(INetServerInstanceData netServerInstanceData,String objectName,String methodName,Object[] parameter,String[] parameterTypes) throws Exception {
		
			IRemoteCommunicationManager remoteCommunicationManager = null;
				Object obj=null; 
				String ipaddress=netServerInstanceData.getAdminHost();
				int port=netServerInstanceData.getAdminPort();
				String netServerCode;
				try {
					netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
					remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
					remoteCommunicationManager.init(ipaddress,port,netServerCode, true);
					String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
					if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){

						obj=remoteCommunicationManager.execute( objectName,methodName,parameter,parameterTypes);

					}else{
						Logger.logDebug(MODULE,"Version of Remote Server is not matching with NetServerInstance "+ipaddress);
					}

				} catch (NoSuchEncryptionException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
					throw e;
				} catch (EncryptionFailedException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
					throw e;
				} catch (InitializationFailedException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
					throw e;
				} catch (UnidentifiedServerInstanceException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
					throw e;
				} catch (CommunicationException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
					throw e;
				}finally{
					try {
						if(remoteCommunicationManager != null)
							remoteCommunicationManager.close();
					} catch (Exception e2) {
					}  
		    		
				}

				return obj;
		
	}
	//
	/**
	 * To execute remote method(Mbean Call) on all NetServerInstance available in database. 
	 * @param objectName
	 * @param methodName
	 * @param parameter
	 * @param parameterTypes
	 * @return object
	 */
	public static Object invokeRemoteMethod(String objectName,String methodName,Object[] parameter,String[] parameterTypes) {
		Object obj=null; 
		netServerBLManager = new NetServerBLManager();
		try {
			for (NetServerInstanceData netServerInstanceData :netServerBLManager.getNetServerInstanceList()){
				obj=invokeRemoteMethod(netServerInstanceData, objectName, methodName, parameter, parameterTypes);
				if(obj!=null){
					break;
				}}
		}catch (DataManagerException e) {
			Logger.logError(MODULE,"Error executing remote method "+methodName+" Reason: "+e.getMessage());
		}

		return obj;


	}
	
 
	/**To execute remote method(Mbean Call) on NetServerInstance passed as a paramenter.
	 * @param netServerInstanceData
	 * @param objectName
	 * @param methodName
	 * @param parameter
	 * @param parameterTypes
	 * @return Object 
	 */
	public static Object invokeRemoteMethod(NetServerInstanceData netServerInstanceData,String objectName,String methodName,Object[] parameter,String[] parameterTypes) {
		
			IRemoteCommunicationManager remoteCommunicationManager = null;
				Object obj=null; 
				String ipaddress=netServerInstanceData.getAdminHost();
				int port=netServerInstanceData.getAdminPort();
				String netServerCode;
				try {
					netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
					remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
					remoteCommunicationManager.init(ipaddress,port,netServerCode, true);
					String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
					if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){

						obj=remoteCommunicationManager.execute( objectName,methodName,parameter,parameterTypes);

					}else{
						Logger.logDebug(MODULE,"Version of Remote Server is not matching with NetServerInstance "+ipaddress);
					}

				} catch (NoSuchEncryptionException e) {
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
				} catch (InitializationFailedException e) {
					e.printStackTrace();
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
				} catch (UnidentifiedServerInstanceException e) {
					e.printStackTrace();
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
				} catch (CommunicationException e) {
                     e.printStackTrace();
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
				} catch (EncryptionFailedException e) {
					e.printStackTrace();
					Logger.logError(MODULE,"Error executing remote method "+methodName+", Reason: "+e.getMessage());
				} finally {
					if (remoteCommunicationManager != null) {
						remoteCommunicationManager.close();
					}
				}

				return obj;
		
	}
	
	
}
