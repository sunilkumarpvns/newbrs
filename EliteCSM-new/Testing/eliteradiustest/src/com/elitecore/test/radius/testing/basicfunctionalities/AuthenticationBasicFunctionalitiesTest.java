/*
 *  Crestel AAA Test Case
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Sep 18, 2006
 *  Created By Ezhava Baiju D
 */

 
package com.elitecore.test.radius.testing.basicfunctionalities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestSuite;

import com.elitecore.test.radius.testcase.EliteRadiusTestCase;
import com.elitecore.test.radius.testing.base.BaseAuthenticationTest;


public class AuthenticationBasicFunctionalitiesTest extends BaseAuthenticationTest {

         
    public AuthenticationBasicFunctionalitiesTest(String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        String strRadiusTestHome = System.getProperty("RADIUS_TEST_HOME");
        System.out.println("Radius Test Home Property : " + strRadiusTestHome);
        String strFilename = strRadiusTestHome + File.separator + "test-cases/radius/auth-service/001-basic-functionalities.xml";
        System.out.println("Test File : " + strFilename);
        parseRadiusTestCase(new File(strFilename));
    }
    
    public static TestSuite suite() {
        System.out.println("Junit - 1");
        TestSuite suite = new TestSuite();
        suite.addTest(new AuthenticationBasicFunctionalitiesTest("testAAABasicFunctionalities"));
        return suite;
    }

    //-------------------------------------------------------------------------
    // Test get/setStringValue method of StringAttribute
    // Test cases:
    //   1. "" value
    //   2. 10 character long String
    //   3. null value
    //   4. "0" value
    //   5. "4294967296" value
    //-------------------------------------------------------------------------
    
    
    public void testAAABasicFunctionalities() {

        try {
            String strRadiusTestHome = System.getProperty("RADIUS_TEST_HOME");
            System.out.println("Radius Test Home Property : " + strRadiusTestHome);
            String strFilename = strRadiusTestHome + File.separator + "test-cases/radius/auth-service/001-basic-functionalities.xml";
            System.out.println("Test File : " + strFilename);
            ArrayList<EliteRadiusTestCase> aaatest = parseRadiusTestCase(new File(strFilename));
            
            Iterator<EliteRadiusTestCase> iterator = aaatest.iterator();
         
            while(iterator.hasNext()) {
        	   
                EliteRadiusTestCase radiusTestCase = iterator.next();
                byte[] responsePacketBytes = doTest(radiusTestCase.getRequestDataBytes());
                byte[] expectedPacketBytes = radiusTestCase.getResponseDataBytes();
                boolean isSuccess = true;

                for(int i=0; i < expectedPacketBytes.length; i++) {
                    if (responsePacketBytes[i] != expectedPacketBytes[i]) {
        			    isSuccess = false;
        		    }
        	    }
               
                if(isSuccess) {
            	    System.out.println(getClass().getSimpleName() + " : " + radiusTestCase.getTestCaseId() + "  Status = Success");
                }else {
            	    System.out.println(getClass().getSimpleName() + " : " + radiusTestCase.getTestCaseId() + "  Status = Failed");
            	    System.out.println("Expected : " + bytesToHex(expectedPacketBytes));
            	    System.out.println("Received : " + bytesToHex(responsePacketBytes));
            	    fail("testAAABasicFunctionalities failed, reason : one of the test case failed");
                }
            }
            
        } catch(Exception e) {
            fail("testAAABasicFunctionalities failed, reason: "+e.getMessage());
        }
    }
    
   
    
    
}
