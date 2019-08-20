package com.elitecore.aaa.radius.service.dynauth;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.radius.conf.impl.DynAuthDBScanDetail;
import com.elitecore.aaa.radius.conf.impl.DynAuthExternalSystemDetail;
import com.elitecore.aaa.radius.conf.impl.DynAuthResponseCodeDetail;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public abstract class DynAuthDBScanningTask extends BaseIntervalBasedTask {
	
	private static final String MODULE = "DYNA-AUTH-DB-SCANNING-TASK";
		
	private static final String UPDATE_QUERY = "UPDATE TBLDYNAUTHREQUESTS SET RESPONSECODE=?, TRIEDCOUNT=? WHERE SLNO=?";
	private static final String SUCCESS_DELET_QUERY = "delete from TBLDYNAUTHREQUESTS where ENDTIME < CURRENT_TIMESTAMP and RESPONSECODE = 0";
	private static final String FAILURE_DELET_QUERY = "delete from TBLDYNAUTHREQUESTS where ENDTIME < CURRENT_TIMESTAMP and RESPONSECODE <> 0";
	
	private static final String PROXY_ERROR = "Proxy Error";
	
	private static final int SUCCESS_RECORD = 0;
    private static final int FAILURE_RECORD = 1;
   
    private static final int DELETE_ACTION = 2;
    
    private static final String ACK = "ACK";
    private static final String NAK = "NAK";
    
    private static int requestCounter = 0;
    private static int responseCounter = 0;
    
	private RadDynAuthServiceContext serviceContext;
    private Object listMoniter;
    
    private long initialDelay;
	private long intervalSeconds;
	private DynAuthExternalSystemDetail externalDataBaseDetail;
	
	private String SELECT_QUERY;
    
    public  DynAuthDBScanningTask(RadDynAuthServiceContext serviceContext,long initialDelay,long intervalSeconds) {
		this.serviceContext = serviceContext;
		this.initialDelay = initialDelay;
		this.intervalSeconds = intervalSeconds;
		this.listMoniter = new Object();
	}
    
    public void init() {
    	externalDataBaseDetail = serviceContext.getDynAuthConfiguration().getExternalDataBaseDetail();
    	DynAuthResponseCodeDetail dynAuthResponseCodeDetail = externalDataBaseDetail.getResponceCodeData();
    	SELECT_QUERY = "select SLNO as slno,PACKETTYPE as packettype, USERID as userid , NASIPADDRESS as nasipaddress," +
    			"ACCTSESSIONID as acctsessionid , AVPSTRINGPAIR as avpstringpair , TRIEDCOUNT as triedcount from (select * from TBLDYNAUTHREQUESTS where " +
    			"((STARTTIME <= CURRENT_TIMESTAMP) AND (CURRENT_TIMESTAMP < ENDTIME OR ENDTIME = STARTTIME) AND (TRIEDCOUNT <= 3)) AND (PACKETTYPE in (40, 43)) AND (RESPONSECODE is NULL) " +
    			"order by SLNO) table_alias";
    	if (dynAuthResponseCodeDetail == null || dynAuthResponseCodeDetail.getIsRetryEnabled() == false) {
    		return;
    	}
    	List<Integer> responceCodeList = dynAuthResponseCodeDetail.getResponceCodeList();
    	if (Collectionz.isNullOrEmpty(responceCodeList)) {
    		return;
    	}
    	StringBuffer strTemp = new StringBuffer();
    	for (int l = 0; l < responceCodeList.size(); l++) {
    		if (strTemp.length() > 0 ) {
    			strTemp.append(',');
    		}
    		strTemp.append(responceCodeList.get(l));
    	}

    	String strSubQuery = " AND (RESPONSECODE is null OR RESPONSECODE IN (" + strTemp.toString() + ")) order by SLNO) table_alias";
    	int retrylimit = dynAuthResponseCodeDetail.getRetryLimit();

    	if (retrylimit < 0) {
    		SELECT_QUERY = "select SLNO as slno,PACKETTYPE as packettype, USERID as userid , NASIPADDRESS as nasipaddress," +
    				"ACCTSESSIONID as acctsessionid , AVPSTRINGPAIR as avpstringpair , TRIEDCOUNT as triedcount " +
    				"from (select * from TBLDYNAUTHREQUESTS where ((STARTTIME <= CURRENT_TIMESTAMP) AND " +
    				"(CURRENT_TIMESTAMP < ENDTIME OR ENDTIME = STARTTIME)) AND (PACKETTYPE in (40, 43))" + strSubQuery;
    	} else {
    		SELECT_QUERY = "select SLNO as slno,PACKETTYPE as packettype, USERID as userid , NASIPADDRESS as nasipaddress," +
    				"ACCTSESSIONID as acctsessionid , AVPSTRINGPAIR as avpstringpair , TRIEDCOUNT as triedcount " +
    				"from (select * from TBLDYNAUTHREQUESTS where ((STARTTIME <= CURRENT_TIMESTAMP) AND " +
    				"(CURRENT_TIMESTAMP < ENDTIME OR ENDTIME = STARTTIME) AND (TRIEDCOUNT <= " 
    				+ retrylimit +")) AND (PACKETTYPE in (40, 43))" + strSubQuery;
    	}
    }
    
	@Override
	public void execute(AsyncTaskContext context) {
		scanAndSubmitDynaAuthRequestsFromDatabase();
		while (requestCounter != responseCounter) { 
			try {
				listMoniter.wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public long getInitialDelay() {
		return initialDelay;
	}

    private void scanAndSubmitDynaAuthRequestsFromDatabase() {
    	
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (LogManager.getLogger().isDebugLogLevel()) {
        	LogManager.getLogger().debug(MODULE, "Scanning and Submitting requests from database.");
        }
      
        Connection connection =null;

        if (externalDataBaseDetail.getActionOnSucess() == DELETE_ACTION) {
        	cleanupDatabase(SUCCESS_RECORD);
        }
        
        if (externalDataBaseDetail.getActionOnFailure() == DELETE_ACTION) {
        	cleanupDatabase(FAILURE_RECORD);
        }
        
        DynAuthDBScanDetail datasourceDetail = externalDataBaseDetail.getDatasourceDetail();
        try {
        	DBConnectionManager connectionManager = DBConnectionManager.getInstance(datasourceDetail.getDataSourceName());
        	connection = connectionManager.getConnection();
        	
        	String recordFetchQuery = SELECT_QUERY;
        	if(DBVendors.ORACLE.name().equals(connectionManager.getVendor().name())){
        		recordFetchQuery += " where rownum<=? order by rownum";
        	}else if(DBVendors.POSTGRESQL.name().equals(connectionManager.getVendor().name())){
        		recordFetchQuery += " limit ?";
        	}
        	
        	preparedStatement = connection.prepareStatement(recordFetchQuery);
        	preparedStatement.setInt(1,datasourceDetail.getMaxRecordPerScan());
        	resultSet = preparedStatement.executeQuery();

        	int packetType;
        	String nasIPAddress;
        	String userName;
        	String acctSessionId;
        	String avpStringPair;

        	while (resultSet.next()) {

        		final int slno = resultSet.getInt("slno");
        		packetType = resultSet.getInt("packettype");
        		nasIPAddress = resultSet.getString("nasipaddress");
        		userName = resultSet.getString("userid");
        		acctSessionId = resultSet.getString("acctsessionid");
        		avpStringPair = resultSet.getString("avpstringpair");
        		final int triedCount = resultSet.getInt("triedcount");

        		if (packetType == RadiusConstants.COA_REQUEST_MESSAGE || packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {

        			RadiusPacket radiusPacket = new RadiusPacket();
        			radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());
        			radiusPacket.setPacketType(packetType);
        			IRadiusAttribute attr = null;
        			if (nasIPAddress != null && nasIPAddress.length()>0) {
        				addAttibuteToRequestPacket(radiusPacket, RadiusAttributeConstants.NAS_IP_ADDRESS, nasIPAddress, attr);   
        			}

        			if (userName != null && userName.length()>0) {
        				addAttibuteToRequestPacket(radiusPacket, RadiusAttributeConstants.USER_NAME, userName, attr);
        			}
                        
        			if (acctSessionId != null && acctSessionId.length()>0) {
        				addAttibuteToRequestPacket(radiusPacket, RadiusAttributeConstants.ACCT_SESSION_ID, acctSessionId, attr);
        			}

        			if (avpStringPair != null && avpStringPair.length()>0) {
        				ArrayList<IRadiusAttribute> attrList = parseAVPString(avpStringPair);
        				if (attrList != null && attrList.size()>0) {
        					if (attrList.contains(Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_FILTER_RULE)) ||
        							attrList.contains(Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP))	) {
        						int listSize = attrList.size();
        						StringBuffer strNASFilterRuleValue = new StringBuffer();
        						IRadiusAttribute attributefromList;
        						for (int i=0;i<listSize;i++) {
        							attributefromList = attrList.get(i);
        							if (attributefromList!=null) {
        								if (attributefromList.getType() == RadiusAttributeConstants.NAS_FILTER_RULE) {
        									if (strNASFilterRuleValue.length() > 0) {
        										strNASFilterRuleValue.append('\0');
        									}
        									strNASFilterRuleValue.append(attributefromList.getStringValue());
        									attrList.remove(i);
        								} else if (attributefromList.getType() == RadiusAttributeConstants.FILTER_ID) {
        									attrList.remove(i);
        								} else if (attributefromList.getType() == RadiusAttributeConstants.EVENT_TIMESTAMP && !externalDataBaseDetail.getIsAddEventTimeAttribute()) {
        									attrList.remove(i);
        								}
        							}
        						}

        						byte[] strNASFilterRuleValueBytes = null;
        						try {
        							strNASFilterRuleValueBytes = strNASFilterRuleValue.toString().getBytes(CommonConstants.UTF8);
        						} catch(UnsupportedEncodingException e) {
        							strNASFilterRuleValueBytes = strNASFilterRuleValue.toString().getBytes();
        						}
        						Collection<byte[]> radiusAttrInResponse = RadiusUtility.getByteChunks(strNASFilterRuleValueBytes, 253); 
        						Iterator<byte[]> radiusAttrItr = radiusAttrInResponse.iterator();
        						while (radiusAttrItr.hasNext()) {
        							IRadiusAttribute radiusAttrAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_FILTER_RULE);
        							radiusAttrAttribute.setValueBytes((byte[])radiusAttrItr.next());			
        							radiusPacket.addAttribute(radiusAttrAttribute);
        						}

        					}
        				}
        				radiusPacket.addAttributes(attrList);
        			}

        			//Add Event-Timestamp Attribute

        			if (externalDataBaseDetail.getIsAddEventTimeAttribute()) {
        				IRadiusAttribute eventAttribute =  radiusPacket.getRadiusAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP);
        				if (eventAttribute == null) {
        					IRadiusAttribute eventTimestampAttr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP);
        					if (eventTimestampAttr != null) {
        						eventTimestampAttr.setLongValue( System.currentTimeMillis()/1000);
        						radiusPacket.addAttribute(eventTimestampAttr);
        					}    
        				} else {
        					eventAttribute.setLongValue( System.currentTimeMillis()/1000);
        				}
        			}                      

        			radiusPacket.refreshInfoPacketHeader();
        			radiusPacket.refreshPacketHeader();
        			 
                    String sharedSecret = serviceContext.getServerContext().getServerConfiguration().getRadClientConfiguration().getClientSharedSecret(InetAddress.getLocalHost().getHostAddress(), packetType);
        			byte[] authenticator = getRequestAuthenticator(packetType, radiusPacket, sharedSecret);
        			radiusPacket.setAuthenticator(authenticator);
        			
        			if (LogManager.getLogger().isInfoLogLevel()) {
        				LogManager.getLogger().info(MODULE, "Adding DynaAuth request to InitialRequestQueue.  Request Packet: " + radiusPacket.toString());
        			}

        			try {
        				handleLocalRequest(radiusPacket.getBytes(),  new ILocalResponseListener() {

        					public void responseReceived( byte[] responseBytes) {
        						RadiusPacket packet = new RadiusPacket();
        						packet.setBytes(responseBytes);
        						serviceContext.getServerContext().getTaskScheduler()
        							.scheduleSingleExecutionTask(new DatabaseUpdatationOnResponseTask(slno, packet, triedCount));

        					}

        					public void requestDropped(byte[] responseBytes) {
        						serviceContext.getServerContext().getTaskScheduler()
        							.scheduleSingleExecutionTask(new DatabaseUpdatationOnResponseTask(slno, null, triedCount));

        					}

        					public void requestTimedout(byte[] responseBytes) {
        						serviceContext.getServerContext().getTaskScheduler()
        							.scheduleSingleExecutionTask(new DatabaseUpdatationOnResponseTask(slno, null, triedCount));

        					}
        				});
        				requestCounter++;


        			} catch (UnknownHostException e) {

        			}



        		} else {
        			if (LogManager.getLogger().isWarnLogLevel()) {
        				LogManager.getLogger().warn(MODULE, "Invalid DynaAuth request . Parameters are null or target system not found.");
        			}
        			updateRecord(slno, PROXY_ERROR, DynAuthErrorCode.OtherProxyProcessingError.value, triedCount);
        		}
        		try {
        			Thread.sleep(datasourceDetail.getDelatBetweenSubsequentRequest());
        		} catch (InterruptedException ex) {
        			LogManager.getLogger().trace(MODULE, ex);
        		}
        	}

        } catch (DataSourceException e) {
        	if (LogManager.getLogger().isWarnLogLevel()) {
       			LogManager.getLogger().warn(MODULE, "Skipping current schedule DBScanningTask, Database connection Data Source is unavailable, Reason: " + e.getMessage());
        	}
        	LogManager.getLogger().trace(MODULE, e);
        } catch(SQLException ex) {
        	if (LogManager.getLogger().isErrorLogLevel()) {
        		LogManager.getLogger().error(MODULE, "Failed to scan and submit dyna auth request from database. Reason: " + ex.getMessage());
        	}
        	LogManager.getLogger().trace(MODULE, ex);
        } catch (UnknownHostException e1) {
        	if(LogManager.getLogger().isErrorLogLevel()) {
        		LogManager.getLogger().error(MODULE, "Failed to fetch shared secret. Reason: " + e1.getMessage());
        	}
        	LogManager.getLogger().trace(MODULE, e1);
		} finally {
        	DBUtility.closeQuietly(resultSet);
        	DBUtility.closeQuietly(preparedStatement);
        	DBUtility.closeQuietly(connection);
        }
    }
  
    private byte[] getRequestAuthenticator(int packetType, RadiusPacket radiusPacket, String sharedSecret) {
    	if (packetType==RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || packetType == RadiusConstants.COA_REQUEST_MESSAGE 
    			|| packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {
    		return RadiusUtility.generateRFC2866RequestAuthenticator(radiusPacket, sharedSecret);
    	}
    	return RadiusUtility.generateRFC2865RequestAuthenticator();
    }

	private ArrayList<IRadiusAttribute> parseAVPString(String avpString) {
    	 ArrayList<IRadiusAttribute> attrList = new ArrayList<IRadiusAttribute>();
    	 IRadiusAttribute attr = null;
    	 if (avpString != null) {
    		 avpString = avpString.trim();
    		 String[] strTokenArr = null;
    		 strTokenArr = avpString.split(",");
    		 int cnt = 0;
    		 String strToken = null;
    		 String strTemp = null;
    		 while (cnt < strTokenArr.length) {
    			 if (strToken == null) {
    				 strToken = strTokenArr[cnt].trim();
    				 cnt++;
    			 } else {
    				 strToken = strToken + strTokenArr[cnt].trim();
    				 strTemp = strTokenArr[cnt].trim();
    				 cnt++;
    			 }
    			 if (strToken != null) {
    				 if (strTemp != "") {
    					 if ((strToken.endsWith("\\") && (cnt >= strTokenArr.length))) {
    						 strToken = strToken.substring(0,strToken.length()-1)+",";
    					 }
    					 if ( !strToken.endsWith("\\") ) {
    						 strToken.trim();
    						 attr = getAttributeFromString(strToken);
    						 if (attr != null) {
    							 attrList.add(attr);
    						 }
    						 strToken = null;
    					 } else if(cnt < strTokenArr.length) {
    						 strToken = strToken.substring(0,strToken.length()-1)+",";
    					 }
    				 } else {
    					 strToken.trim();
    					 attr = getAttributeFromString(strToken);
    					 if (attr != null) {
    						 attrList.add(attr);
    					 }
    					 strToken = null;
    				 }
    			 }
    		 }
    	 }
    	 return attrList;
     }
	
	private IRadiusAttribute getAttributeFromString(String strToken) {
    	 StringTokenizer strTokenizer = new StringTokenizer(strToken,"=");
    	 String strData = null;
    
    	 if (strTokenizer.hasMoreTokens()) {
    		 String attributeId = strTokenizer.nextToken();
    		 if (strTokenizer.hasMoreTokens()) {
    			 strData = strTokenizer.nextToken();
    		 }
			try {
				IRadiusAttribute radAttribute = Dictionary.getInstance().getKnownAttribute(attributeId);
				if (radAttribute == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id :"+attributeId+" null value returned");
					}
					return null;
				}
				radAttribute.setStringValue(strData);
     			return radAttribute;
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
				return null;
			}
    	 }
    	 return null;
     }

	@Override
	public long getInterval() {
		return this.intervalSeconds;
	}
	
	private void addAttibuteToRequestPacket(RadiusPacket requestPacket,int attributeId,String attributeValue,IRadiusAttribute attribute) {
		attribute = Dictionary.getInstance().getKnownAttribute(attributeId);
		if (attribute!=null) {
			attribute.setStringValue(attributeValue);
			requestPacket.addAttribute(attribute);
		}	
	}
	private void cleanupDatabase(int intRecordType) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection conn =  null;
		int intResult = 0;
		DynAuthDBScanDetail datasourceDetail  = externalDataBaseDetail.getDatasourceDetail();
        try {
        	conn =  DBConnectionManager.getInstance(datasourceDetail.getDataSourceName()).getConnection();
        	if (!conn.isClosed()) {
        		if (intRecordType == SUCCESS_RECORD) {
        			if (LogManager.getLogger().isInfoLogLevel()) {
        				LogManager.getLogger().info(MODULE,"Cleaning Success Records");
        			}
        			preparedStatement = conn.prepareStatement(SUCCESS_DELET_QUERY);
        		}
        		else if (intRecordType == FAILURE_RECORD) {
        			if (LogManager.getLogger().isInfoLogLevel()) {
        				LogManager.getLogger().info(MODULE,"Cleaning Failure Records");
        			}
        			preparedStatement = conn.prepareStatement(FAILURE_DELET_QUERY);
        		}
        		
        		intResult = preparedStatement.executeUpdate();
        		if (intResult <= 0) {
        			if (LogManager.getLogger().isInfoLogLevel()) {
        				LogManager.getLogger().info(MODULE,"No Records found whose End Time is less than Current Time.");
        			}
        		} else {
        			if (LogManager.getLogger().isInfoLogLevel()) {
        				LogManager.getLogger().info(MODULE,"Number of Records deleted : " + intResult);
        			}
        		}
        		conn.commit();
        	}
        } catch (DataSourceException e) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Connection to datasource: " + datasourceDetail.getDataSourceName() + ", Reason: " + e.getMessage() + ". Failed to update database based on response");
			}
			LogManager.getLogger().trace(MODULE, e);
		} catch (SQLException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to updating database based on response. Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(MODULE, ex);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}
    }

    private void updateRecord(final int requestId, final String errorMesssage, final int errorCode, final int triedCount) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
        	connection =  DBConnectionManager.getInstance(externalDataBaseDetail.getDatasourceDetail().getDataSourceName()).getConnection();
        	if (!connection.isClosed()) {
        		preparedStatement = connection.prepareStatement(UPDATE_QUERY);
        		preparedStatement.setInt(1, errorCode);
        		if (externalDataBaseDetail != null && externalDataBaseDetail.getResponceCodeData() != null 
        				&& externalDataBaseDetail.getResponceCodeData().getRetryLimit() > 0) {
        			preparedStatement.setInt(2, triedCount + 1);
        		} else {
        			preparedStatement.setInt(2, triedCount);
        		}
        		preparedStatement.setInt(3, requestId);
        		preparedStatement.setQueryTimeout(10);
        		preparedStatement.execute();
        		connection.commit();
        	} else {
        		if (LogManager.getLogger().isWarnLogLevel()) {
        			LogManager.getLogger().warn(MODULE, "Failed to update DynaAuthRequest status in database. Reason: DB connection not available");
        		}
        	}
        } catch (DataSourceException e) {
        	if (LogManager.getLogger().isWarnLogLevel()) {
        		LogManager.getLogger().warn(MODULE, "Connection to datasource: , " + externalDataBaseDetail.getDatasourceDetail().getDataSourceName() 
        				+ " is unavailable, Reason: " + e.getMessage() + ".Failed to update DynaAuthRequest status in database");
        	}
        	LogManager.getLogger().trace(MODULE, e);
        } catch(SQLException ex) {
        	LogManager.getLogger().trace(MODULE, ex);
        } finally {
        	DBUtility.closeQuietly(preparedStatement);
        	DBUtility.closeQuietly(connection);
        }    
    }

    
	public abstract void handleLocalRequest(byte[] requestRowBytes,ILocalResponseListener responseListener) throws UnknownHostException;
	
	class  DatabaseUpdatationOnResponseTask extends BaseSingleExecutionAsyncTask {
		
		private int requestId;
		private RadiusPacket responsePacket;
		final private int triedCount;
		
		public DatabaseUpdatationOnResponseTask(int requestId,RadiusPacket responsePacket, int triedCount) {
			this.requestId = requestId;
			this.responsePacket = responsePacket;
			this.triedCount = triedCount;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			
			try {
				LogManager.getLogger().info(MODULE, "Updating Database for Request Id : " + requestId + " and Received Response Packet : " + responsePacket);
				
				if (responsePacket == null) {
					updateRecord(requestId, PROXY_ERROR, DynAuthErrorCode.OtherProxyProcessingError.value, triedCount);
				} else if (responsePacket.getPacketType() == RadiusConstants.COA_ACK_MESSAGE ||
						responsePacket.getPacketType() == RadiusConstants.DISCONNECTION_ACK_MESSAGE) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Received ACK packet. Updating request record with ACK.");
					}
					updateRecord(requestId, ACK, 0, triedCount);
				} else if (responsePacket.getPacketType() == RadiusConstants.COA_NAK_MESSAGE ||
						responsePacket.getPacketType() == RadiusConstants.DISCONNECTION_NAK_MESSAGE) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Received NAK packet. Updating request record with NAK ");
					}
					IRadiusAttribute attr = responsePacket.getRadiusAttribute(RadiusAttributeConstants.ERROR_CAUSE);
					int errorCode = -1;
					if (attr != null) {
					    errorCode = attr.getIntValue();
					}
					updateRecord(requestId, NAK, errorCode, triedCount);
				}
				responseCounter++;
			
				synchronized (listMoniter) {
					listMoniter.notify();
				}
			} catch (Exception e) {
				responseCounter++;
				synchronized (listMoniter) {
					listMoniter.notify();
				}
				if(LogManager.getLogger().isErrorLogLevel()) {
					LogManager.getLogger().error(MODULE, "Failed to update DynaAuthRequest status. Reason: "+e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);
			}	
			
		}

	}
}