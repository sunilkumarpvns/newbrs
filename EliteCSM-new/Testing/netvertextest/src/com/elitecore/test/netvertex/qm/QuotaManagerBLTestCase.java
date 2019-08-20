package com.elitecore.test.netvertex.qm;


import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.system.comm.target.db.DBTargetSystem;
import com.elitecore.netvertex.qm.constant.QuotaManagerConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.test.netvertex.base.BaseTestCase;

public class QuotaManagerBLTestCase extends BaseTestCase {

	QMBLManager qmblManager = null;
	String dataSourceName;
	Map<String, String> dataSourceMap = new HashMap<String, String>();
	DBTargetSystem targetSystem = null;
	PCRFRequest request = new PCRFRequestImpl();
	PCRFResponse response = new PCRFResponseImpl();

	public QuotaManagerBLTestCase() {


		dataSourceName = "Test Datasource";

		dataSourceMap.put("DATASOURCE_NAME", "Test Datasource");
		dataSourceMap.put("CONNECTION_URL", "jdbc:oracle:thin:@192.168.8.70:1521:eliteaaa");
		dataSourceMap.put("USER_NAME", "netvertexunt60");
		dataSourceMap.put("PASSWORD", "netvertexunt60");
		dataSourceMap.put("MINIMUM_POOL_SIZE", "2");
		dataSourceMap.put("MAXIMUM_POOL_SIZE", "5");


		try{
			DBConnectionManager dbConnectionManager	= DBConnectionManager.getInstance(dataSourceMap.get("DATASOURCE_NAME"));
			dbConnectionManager.init(dataSourceMap.get("CONNECTION_URL"),dataSourceMap.get("USER_NAME"),dataSourceMap.get("PASSWORD"),2,5);
		}catch (DatabaseInitializationException e) {
			//throw new DriverInitializationFailedException("Datasource initialization Failed.",e);
		}catch (DatabaseTypeNotSupportedException e) {
			//throw new DriverInitializationFailedException("Datasource Type is not Supported.",e);
		}

		targetSystem = new DBTargetSystem(dataSourceName, dataSourceMap);
	}
    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void testReserveQuota() {
    	//long usedQuota = 0;
//    	long remainQuota = 0;

    	QMBLManager blManager = new QMBLManager(targetSystem);
    	request.setAttribute("PACKAGE-NAME", "SILVER");
    	blManager.reservationRequest("TEST_TIME", QuotaManagerConstants.MODE_UPLOAD, "STY0000", request, response);
    	LogManager.getLogger().trace("TEST", response.getAttribute("MESSAGE"));

    	//For Normal TIMEBASED User Upload
    	//usedQuota = qmblManager.getUsedQuota("TEST_TIME", QuotaManagerConstants.MODE_UPLOAD, "GOLD", null);
    	//assertEquals(1000, usedQuota);
    	//remainQuota = qmblManager.getRemaingQuota("TEST_TIME", QuotaManagerConstants.MODE_UPLOAD, "GOLD", null);
    	//assertEquals(99000, remainQuota);

    	//For Normal User Download
    	//usedQuota = qmblManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_DOWNLOAD, null);
    	//assertEquals(102400, usedQuota);

    	//For Service Based User Upload
    	//usedQuota = qmblManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "STY0000");
    	//assertEquals(102400, usedQuota);

    	//For Service Based User Download
    	//usedQuota = qmblManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_DOWNLOAD, "STY0000");
    	//assertEquals(102400, usedQuota);

    	//For Service Based User Total
    	//usedQuota = qmblManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_TOTAL, null);
    	//assertEquals(204800, usedQuota);

    }

    @Test
    public void testReportQuota(){

    	long usedQuota = 0;
    	long remainQuota = 0;

    	QMBLManager blManager = new QMBLManager(targetSystem);

    	request.setAttribute("PACKAGE-NAME", "SILVER");
    	request.setAttribute("MAX-SESSION-DATA-UPLOAD", "10240000");
    	request.setAttribute("MAX-SESSION-DATA-TOTAL", "10240000");
    	request.setAttribute("MAX-SESSION-DATA-DOWNLOAD", "10240000");
    	request.setAttribute("SESSION-DATA-TRANSFER-UPLOAD", "10240");
    	request.setAttribute("SESSION-DATA-TRANSFER-DOWNLOAD", "10240");
    	request.setAttribute("SESSION-DATA-TRANSFER-TOTAL", "10240");
    	request.setAttribute("MAX-SESSION-TIME", "1000000");
    	request.setAttribute("SESSION-TIME-TRANSFER", "1000");
    	request.setAttribute("CORE-SESSIONID", "ABC");
    	request.setAttribute("FRAMEDIPADDRESS", "127.0.0.1");
    	request.setAttribute("ACCESSGATEWAYIPADDRESS", "127.0.0.1");
    	request.setAttribute("LOCATIONID", "1");
    	request.setAttribute("SESSION-STARTTIME", "15-JUN-2011 3:00:00");
    	request.setAttribute("SESSION-STOPTIME", "15-JUN-2011 4:15:00");

    	//blManager.reservationRequest("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "STY0001", request, response);

    	blManager.reportRequest("TEST_TIME", QuotaManagerConstants.MODE_UPLOAD, "STY0000", request, response);
    	LogManager.getLogger().trace("TEST", response.getAttribute("MESSAGE"));

//    	usedQuota = blManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "SILVER", "STY0001");
//    	assertEquals(10240, usedQuota);
//
//    	remainQuota = blManager.getRemainQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "SILVER", "STY0001");
//    	assertEquals(10229760,remainQuota);

//    	request.setAttribute("USED-SESSION-UPLOAD", "10240");
//    	request.setAttribute("SESSIONSTOPTIME", "01 Jan 2011 1:30:00");
//
//    	qmblManager.reportRequest("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "STY0000", request, response);
    }

    @Test
    public void testGetUsedQuota(){
    	QMBLManager blManager = new QMBLManager(targetSystem);
    	long usedQuota = blManager.getUsedQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "SILVER", "", "STY0000");
    	assertEquals(10240, usedQuota);
    }

    @Test
    public void testGetRemainingQuota(){
    	QMBLManager blManager = new QMBLManager(targetSystem);
    	long remainQuota = blManager.getRemainQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "SILVER", "", "STY0000");
    	assertEquals(10229760, remainQuota);
    }

    @Test
    public void testInsufficientBalance(){
    	QMBLManager blManager = new QMBLManager(targetSystem);

    	long remainQuota = 0;
    	request.setAttribute("PACKAGE-NAME", "SILVER");
    	request.setAttribute("MAX-SESSION-DATA-UPLOAD", "10240000");
    	request.setAttribute("SESSION-DATA-TRANSFER-UPLOAD", "10240");
    	request.setAttribute("CORE-SESSIONID", "ABC");
    	request.setAttribute("FRAMEDIPADDRESS", "127.0.0.1");
    	request.setAttribute("ACCESSGATEWAYIPADDRESS", "127.0.0.1");
    	request.setAttribute("LOCATIONID", "1");
    	request.setAttribute("SESSION-STARTTIME", "15-JUN-2011 3:00:00");
    	request.setAttribute("SESSION-STOPTIME", "15-JUN-2011 4:15:00");

    	//Getting remainingQuota...
    	remainQuota = blManager.getRemainQuota("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "SILVER", "", "STY0000");

    	//Exhausting Balance by reporting remainingQuota...
    	request.setAttribute("SESSION-DATA-TRANSFER-UPLOAD", remainQuota + "");
    	blManager.reportRequest("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "STY0000", request, response);
    	LogManager.getLogger().trace("TEST", response.getAttribute("MESSAGE"));

    	//Trying to reserveQuota...
    	blManager.reservationRequest("TEST_VOLUME", QuotaManagerConstants.MODE_UPLOAD, "STY0000", request, response);
    	LogManager.getLogger().trace("TEST", response.getAttribute("MESSAGE"));
    }


    @After
    public void tearDown() throws Exception {

    }

    public static void main(String args[]){
    	QuotaManagerBLTestCase test = new QuotaManagerBLTestCase();
    	test.testGetUsedQuota();
    	//test.testReportReserveQuota();
    }
}
