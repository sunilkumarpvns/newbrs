package com.elitecore.license;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.commons.util.constant.CommonConstants;

public class PubkeyGenMain {
    
    public static void main( String aa[] ) {
        String path = LicenseConstants.LICENSE_DIRECTORY;
        String pKey ="";// new ElitePublickeyGenerator().generatePublicKey(LicenseConstants.DEFAULT_ADDITIONAL_KEY);
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String tempKey = inetAddress.getHostName();
            
            OutputStream out = new FileOutputStream(path + "\\" + tempKey + "." + LicenseConstants.PUBLIC_KEY_FILE_EXT);
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            try{
            	dataOutputStream.write(pKey.getBytes(CommonConstants.UTF8));
            }catch(UnsupportedEncodingException e){
            	dataOutputStream.write(pKey.getBytes());
            }
            //out.write(licenseData.getLicenseKey().getBytes());
            dataOutputStream.close();
            out.close();
            System.out.println("Public Key " + path + "\\" + tempKey + "." + LicenseConstants.PUBLIC_KEY_FILE_EXT + " generated");
        }
        catch (UnknownHostException uhe) {
            System.out.println("Public Key generation unsuccesful..");
            uhe.printStackTrace();
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("Public Key generation unsuccesful..");
            fnfe.printStackTrace();
        }
        catch (IOException ioe) {
            System.out.println("Public Key generation unsuccesful..");
            ioe.printStackTrace();
        }
        
        //System.out.println("Decyprpted : " + EncryptDecrypt.decrypt(pKey));
        
    }
    
}
