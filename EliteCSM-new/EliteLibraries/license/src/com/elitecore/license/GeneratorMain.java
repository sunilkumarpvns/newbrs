/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 1, 2007
 *  Created By kaushikvira
 */
package com.elitecore.license;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseModuleConstants;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.commons.util.constant.CommonConstants;
import com.elitecore.license.core.EliteLicenceKeyGenerator;


/**
 * @author kaushikvira
 *
 */
public class GeneratorMain {
public static void main( String args[] ) {
        
    try{
        String publicKey = null;
        if(args.length != 1)
        {
          System.out.println("Public Key is not set");   
        }
        else
        {
        	System.out.println("Public Key File Name :- " +args[0]);
        	// String path = LicenseConstants.LICENSE_DIRECTORY;
        	File nodeFile = new File(args[0] + "radiusserver.pubkey");
        	try (InputStream inputStream = new FileInputStream(nodeFile);
        			DataInputStream dataInuptStream = new DataInputStream(inputStream); ) {
        		byte[] buffer = new byte[(int)nodeFile.length()];
        		dataInuptStream.readFully(buffer);
        		try{
        			publicKey = new String(buffer,CommonConstants.UTF8);
        		}catch(UnsupportedEncodingException e){
        			publicKey = new String(buffer);
        		}
        		System.out.println("PublicKey:-" + publicKey);
        	}
        }
        List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
        
        LicenseData licData = new LicenseData();
        licData.setName(LicenseNameConstants.SYSTEM_NODE);
        licData.setType(LicenseTypeConstants.NODE);
        licData.setModule(LicenseModuleConstants.SYSTEM);
        licData.setVersion("Development Version");
        licData.setValue(publicKey);
        lstLicenseData.add(licData);
        
        
        licData = new LicenseData();
        licData.setName(LicenseNameConstants.SYSTEM_EXPIRY);
        licData.setType(LicenseTypeConstants.EXPIRY_DATE);
        licData.setModule(LicenseModuleConstants.SYSTEM);
        licData.setVersion("Development Version");
        //licData.setValue(d);
        lstLicenseData.add(licData);
        
        licData = new LicenseData();
        //licData.setName(LicenseNameConstants.RADIUS_CLIENTS);
        licData.setType(LicenseTypeConstants.CLIENTS);
        licData.setModule(LicenseModuleConstants.SYSTEM_RADIUS);
        licData.setVersion("Development Version");
        //licData.setValue(10L);
        lstLicenseData.add(licData);
        
        licData = new LicenseData();
        licData.setName(LicenseNameConstants.SYSTEM_MODULE);
        licData.setType(LicenseTypeConstants.MODULE);
        licData.setModule(LicenseModuleConstants.SYSTEM);
        licData.setVersion("Development Version");
        licData.setValue(LicenseModuleConstants.SYSTEM);
        lstLicenseData.add(licData);
        
        EliteLicenceKeyGenerator eliteLicgen = new EliteLicenceKeyGenerator();
        String lickey = eliteLicgen.genreateLicense(lstLicenseData);
        
        try (OutputStream out = new FileOutputStream(LicenseConstants.LICENSE_DIRECTORY + File.separator + "local_node"  + LicenseConstants.LICESE_FILE_EXT);
        		DataOutputStream dataOutputStream = new DataOutputStream(out);) { 
        	try{
        		dataOutputStream.write(lickey.getBytes(CommonConstants.UTF8));
        	}catch(UnsupportedEncodingException e){ //NOSONAR
        		dataOutputStream.write(lickey.getBytes());
        	}
        }
        
        System.out.println("License Genrateated successfully");
        System.out.println("Additional Detail");
      
        Iterator<LicenseData> it = lstLicenseData.iterator();
        
        int i =1;
        while(it.hasNext())
        {
            licData = it.next();
            
            System.out.println("~~~~~~~~~~~license "+ (i++) +"~~~~~~~~~~~~~~~");
            
            System.out.println("Name :-"+licData.getName());
            System.out.println("Modual :-"+licData.getModule());
            System.out.println("Type :-"+licData.getType());
            System.out.println("Value :-"+licData.getValue());
            System.out.println("Addtional Key :-"+licData.getAdditionalKey());
            System.out.println("Status :-"+licData.getStatus());
            System.out.println("Description :-"+licData.getDescription());
        }
       
    }
    catch(Exception e) //NOSONAR
    {
        e.printStackTrace(); //NOSONAR
    }
    }
}
